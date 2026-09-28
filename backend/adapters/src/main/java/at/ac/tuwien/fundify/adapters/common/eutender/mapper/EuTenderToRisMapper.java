package at.ac.tuwien.fundify.adapters.common.eutender.mapper;

import at.ac.tuwien.fundify.adapters.common.eutender.model.EuTenderAction;
import at.ac.tuwien.fundify.adapters.common.eutender.model.EuTenderBudgetOverview;
import at.ac.tuwien.fundify.adapters.common.eutender.model.EuTenderMetadata;
import at.ac.tuwien.fundify.adapters.common.eutender.model.EuTenderResult;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisCall;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisFunder;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisFunding;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisFundingCharacteristic;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisFundingType;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisIdentifier;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisIdentifierTypeEnum;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisLegalType;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisOrgUnit;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisOrganisationFundingRoleEnum;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisSubject;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisSubmissionMode;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisText;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisTranslationEnum;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisVolume;
import at.ac.tuwien.fundify.adapters.in.rest.constants.SubjectStore;
import at.ac.tuwien.fundify.adapters.in.rest.dto.StandardizedSubjectWebModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
public class EuTenderToRisMapper {

    public static final EuTenderToRisMapper INSTANCE = new EuTenderToRisMapper();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private EuTenderToRisMapper() {}

    public RisFunding toRisCall(EuTenderResult result) {
        RisCall call = new RisCall();
        call.setId(UUID.randomUUID().toString());

        EuTenderMetadata meta = result.getMetadata();
        List<EuTenderAction> actions = parseActions(meta);

        RisFundingType type = determineType(actions);
        call.setType(type);

        String title = first(meta != null ? meta.getTitle() : null);
        if (title != null) {
            call.setName(List.of(risText(title)));
        }

        call.setAcronym(first(meta != null ? meta.getIdentifier() : null));

        String descHtml = first(meta != null ? meta.getDescriptionByte() : null);
        if (descHtml != null) {
            call.setDescription(List.of(risText(stripHtml(descHtml))));
        }

        if (result.getUrl() != null) {
            call.setWebsite(List.of(result.getUrl()));
        }

        String identifierValue = first(meta != null ? meta.getIdentifier() : null);
        if (identifierValue != null) {
            RisIdentifier identifier = new RisIdentifier();
            identifier.setType(RisIdentifierTypeEnum.EU_ID);
            identifier.setValue(identifierValue + "-" + meta.getCallccm2Id());
            call.setIdentifiers(List.of(identifier));
        }

        call.setFunder(List.of(europeanCommissionFunder()));

        call.setCharacteristics(List.of(RisFundingCharacteristic.EU_FUNDING));

        call.setTargetGroups(new ArrayList<>());

        List<String> keywords = meta != null ? meta.getKeywords() : null;
        call.setSubjects(mapKeywordsToSubjects(keywords));

        call.setLegalType(RisLegalType.PROJECT27);
        call.setApplicationLanguages(List.of("en"));
        call.setFullyFunded(null);
        call.setSubmissionModes(List.of(RisSubmissionMode.ONLINE_FULL));

        call.setMinProjectDuration(null);
        call.setMaxProjectDuration(null);

        List<EuTenderBudgetOverview.BudgetTopicAction> topicActions = topicActions(parseBudget(meta), meta);
        call.setAmount(topicVolume(topicActions));
        call.setMinProjectVolume(contribution(topicActions, EuTenderBudgetOverview.BudgetTopicAction::getMinContribution, true));
        call.setMaxProjectVolume(contribution(topicActions, EuTenderBudgetOverview.BudgetTopicAction::getMaxContribution, false));

        call.setDmpRequired(true);
        call.setDmpGuidelines("DMP required for submission");
         return call;
    }

    /**
     * The budget entries belonging to this topic, or {@code null} if there is no budget overview at all.
     * Returns an empty list if a budget overview exists but none of its entries match this topic.
     */
    private List<EuTenderBudgetOverview.BudgetTopicAction> topicActions(EuTenderBudgetOverview budget, EuTenderMetadata meta) {
        if (budget == null || budget.getBudgetTopicActionMap() == null || budget.getBudgetTopicActionMap().isEmpty()) {
            return null;
        }
        List<EuTenderBudgetOverview.BudgetTopicAction> actions = findTopicActions(budget.getBudgetTopicActionMap(), meta);
        return actions != null ? actions : List.of();
    }

    /**
     * Relevant is the budget available for this topic (project), not the total volume of the call,
     * so only the budget entry belonging to this topic is summed up.
     */
    private RisVolume topicVolume(List<EuTenderBudgetOverview.BudgetTopicAction> topicActions) {
        if (topicActions == null) {
            return null;
        }
        return eur(sumBudget(topicActions));
    }

    /**
     * Min/max project volume come from the "Contributions" column of the topic's budget table,
     * i.e. the EU contribution per project, not the overall topic budget.
     */
    private RisVolume contribution(List<EuTenderBudgetOverview.BudgetTopicAction> topicActions,
                                   Function<EuTenderBudgetOverview.BudgetTopicAction, Long> getter, boolean min) {
        List<Long> values = new ArrayList<>();
        collectContributions(topicActions, getter, values);
        return values.stream()
            .reduce(min ? Math::min : Math::max)
            .map(this::eur)
            .orElse(null);
    }

    private void collectContributions(List<EuTenderBudgetOverview.BudgetTopicAction> actions,
                                      Function<EuTenderBudgetOverview.BudgetTopicAction, Long> getter, List<Long> values) {
        if (actions == null) {
            return;
        }
        for (EuTenderBudgetOverview.BudgetTopicAction action : actions) {
            if (action == null) {
                continue;
            }
            if (getter.apply(action) != null) {
                values.add(getter.apply(action));
            } else if (action.getBudgetTopicActionMap() != null) {
                action.getBudgetTopicActionMap().values().forEach(nested -> collectContributions(nested, getter, values));
            }
        }
    }

    private RisVolume eur(long amount) {
        RisVolume volume = new RisVolume();
        volume.setCurrency("EUR");
        volume.setAmount(BigDecimal.valueOf(amount));
        return volume;
    }

    /**
     * The budget overview covers the whole call, keyed by topic ccm2 id. Falls back to matching the
     * action prefix against the topic identifier, and to the single entry if the call has only one topic.
     */
    private List<EuTenderBudgetOverview.BudgetTopicAction> findTopicActions(
        Map<String, List<EuTenderBudgetOverview.BudgetTopicAction>> budgetTopicActionMap, EuTenderMetadata meta) {

        String topicId = first(meta != null ? meta.getCcm2Id() : null);
        if (topicId != null && budgetTopicActionMap.containsKey(topicId)) {
            return budgetTopicActionMap.get(topicId);
        }

        String identifier = first(meta != null ? meta.getIdentifier() : null);
        if (identifier != null) {
            List<EuTenderBudgetOverview.BudgetTopicAction> byAction = budgetTopicActionMap.values().stream()
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .filter(a -> a.getAction() != null
                    && (a.getAction().equals(identifier) || a.getAction().startsWith(identifier + " ")))
                .toList();
            if (!byAction.isEmpty()) {
                return byAction;
            }
        }

        if (budgetTopicActionMap.size() == 1) {
            return budgetTopicActionMap.values().iterator().next();
        }

        return null;
    }

    private long sumBudget(List<EuTenderBudgetOverview.BudgetTopicAction> actions) {
        if (actions == null) {
            return 0;
        }
        long sum = 0;
        for (EuTenderBudgetOverview.BudgetTopicAction action : actions) {
            if (action == null) {
                continue;
            }
            if (action.getBudgetYearMap() != null && !action.getBudgetYearMap().isEmpty()) {
                sum += action.getBudgetYearMap().values().stream()
                    .filter(Objects::nonNull)
                    .mapToLong(Long::longValue)
                    .sum();
            } else if (action.getBudgetTopicActionMap() != null) {
                // only descend when the action itself carries no budget, otherwise sub-actions are counted twice
                for (List<EuTenderBudgetOverview.BudgetTopicAction> nested : action.getBudgetTopicActionMap().values()) {
                    sum += sumBudget(nested);
                }
            }
        }
        return sum;
    }

    private List<EuTenderAction> parseActions(EuTenderMetadata meta) {
        if (meta == null || meta.getActions() == null || meta.getActions().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return OBJECT_MAPPER.readValue(meta.getActions().getFirst(), new TypeReference<>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private EuTenderBudgetOverview parseBudget(EuTenderMetadata meta) {
        if (meta == null || meta.getBudgetOverview() == null || meta.getBudgetOverview().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(meta.getBudgetOverview().getFirst(), EuTenderBudgetOverview.class);
        } catch (Exception e) {
            return null;
        }
    }

    private RisFundingType determineType(List<EuTenderAction> actions) {
        if (actions == null || actions.isEmpty()) {
            return RisFundingType.CALL;
        }
        EuTenderAction first = actions.getFirst();
        if (first.getDeadlineDates() != null && !first.getDeadlineDates().isEmpty() &&
            first.getStatus() != null && "Open".equalsIgnoreCase(first.getStatus().getAbbreviation())) {
                return RisFundingType.ONGOING_CALL;
        }
        return RisFundingType.CALL;
    }

    private RisText risText(String text) {
        RisText risText = new RisText();
        risText.setLang("en");
        risText.setTrans(RisTranslationEnum.O);
        risText.setText(text);
        return risText;
    }

    private RisFunder europeanCommissionFunder() {
        RisOrgUnit orgUnit = new RisOrgUnit();
        orgUnit.setId("ec-european-commission");
        orgUnit.setName(List.of(risText("European Commission")));
        orgUnit.setWebsite("https://ec.europa.eu/");

        RisFunder funder = new RisFunder();
        funder.setFunderType(RisOrganisationFundingRoleEnum.EXECUTIVE_ORGANISATION);
        funder.setFunder(orgUnit);
        return funder;
    }

    private String first(List<String> list) {
        if (list == null || list.isEmpty()) return null;
        return list.getFirst();
    }

    private List<RisSubject> mapKeywordsToSubjects(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return new ArrayList<>();
        }
        Collection<StandardizedSubjectWebModel> subjects = SubjectStore.getAllSubjects();
        Set<String> matchedCodes = new LinkedHashSet<>();
        for (String keyword : keywords) {
            String lowerKeyword = keyword.toLowerCase().trim();
            for (StandardizedSubjectWebModel subject : subjects) {
                String lowerTitle = subject.title().toLowerCase();
                if (lowerTitle.equals(lowerKeyword)
                        || lowerTitle.contains(lowerKeyword)
                        || lowerKeyword.contains(lowerTitle)) {
                    matchedCodes.add(subject.code());
                    break;
                }
            }
        }
        return matchedCodes.stream()
            .map(code -> {
                RisSubject subject = new RisSubject();
                subject.setValue(code);
                subject.setFraction(BigDecimal.ONE);
                return subject;
            })
            .toList();
    }

    private String stripHtml(String html) {
        if (html == null) return null;
        return html
            .replaceAll("(?i)<(p|div|h[1-6]|br|li)[^>]*>", "\n")
            .replaceAll("<[^>]+>", "")
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("\n{3,}", "\n\n")
            .trim();
    }
}
