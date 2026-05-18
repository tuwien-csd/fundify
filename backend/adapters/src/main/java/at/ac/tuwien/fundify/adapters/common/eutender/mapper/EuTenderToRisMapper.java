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
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisTimeSpan;
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
import java.util.Set;
import java.util.UUID;
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

        call.setCharacteristics(List.of(RisFundingCharacteristic.INTERNATIONAL_PROGRAMME));

        call.setTargetGroups(new ArrayList<>());

        List<String> keywords = meta != null ? meta.getKeywords() : null;
        call.setSubjects(mapKeywordsToSubjects(keywords));

        call.setLegalType(RisLegalType.PROJECT27);
        call.setApplicationLanguages(List.of("en"));
        call.setFullyFunded(false);
        call.setSubmissionModes(List.of(RisSubmissionMode.ONLINE_FULL));

        RisTimeSpan minDuration = new RisTimeSpan();
        minDuration.setMonths(12);
        call.setMinProjectDuration(minDuration);

        RisTimeSpan maxDuration = new RisTimeSpan();
        maxDuration.setMonths(60);
        call.setMaxProjectDuration(maxDuration);

        EuTenderBudgetOverview budget = parseBudget(meta);
        if (budget != null && budget.getBudgetTopicActionMap() != null) {
            long total = budget.getBudgetTopicActionMap().values().stream()
                .flatMap(List::stream)
                .filter(a -> a.getBudgetYearMap() != null)
                .flatMap(a -> a.getBudgetYearMap().values().stream())
                .mapToLong(Long::longValue)
                .sum();
            if (total > 0) {
                RisVolume volume = new RisVolume();
                volume.setCurrency("EUR");
                volume.setAmount(BigDecimal.valueOf(total));
                call.setAmount(volume);
            }

        }

        return call;
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
        if (first.getDeadlineDates() != null && !first.getDeadlineDates().isEmpty()) {
            if (first.getStatus() != null && "Open".equalsIgnoreCase(first.getStatus().getAbbreviation())) {
                return RisFundingType.ONGOING_CALL;
            }
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
