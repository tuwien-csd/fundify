package at.ac.tuwien.fundify.adapters.common.eutender.mapper;

import at.ac.tuwien.fundify.adapters.common.eutender.model.EuTenderMetadata;
import at.ac.tuwien.fundify.adapters.common.eutender.model.EuTenderResult;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisCall;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisFunding;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisFundingCharacteristic;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisFundingType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EuTenderToRisMapperTest {

    private final EuTenderToRisMapper mapper = EuTenderToRisMapper.INSTANCE;

    private EuTenderResult resultWith(EuTenderMetadata meta) {
        EuTenderResult result = new EuTenderResult();
        result.setMetadata(meta);
        return result;
    }

    private EuTenderMetadata metaWithTitle(String title) {
        EuTenderMetadata meta = new EuTenderMetadata();
        meta.setTitle(List.of(title));
        return meta;
    }

    private String openActionJson() {
        return """
            [{
              "status": {"id": 31094501, "abbreviation": "Open", "description": "Open"},
              "deadlineDates": ["09 March 2023"]
            }]
            """;
    }

    private String closedActionJson() {
        return """
            [{
              "status": {"id": 31094503, "abbreviation": "Closed", "description": "Closed"},
              "deadlineDates": ["11 June 2020"]
            }]
            """;
    }

    @Test
    void toRisCall_withTitle_setsName() {
        EuTenderMetadata meta = metaWithTitle("Horizon Europe Research Call");
        RisFunding result = mapper.toRisCall(resultWith(meta));

        RisCall call = (RisCall) result;
        assertNotNull(call.getName());
        assertEquals(1, call.getName().size());
        assertEquals("Horizon Europe Research Call", call.getName().getFirst().getText());
        assertEquals("en", call.getName().getFirst().getLang());
    }

    @Test
    void toRisCall_setsEuropeanCommissionAsFunder() {
        RisFunding result = mapper.toRisCall(resultWith(metaWithTitle("Test")));

        RisCall call = (RisCall) result;
        assertNotNull(call.getFunder());
        assertEquals(1, call.getFunder().size());
        assertEquals("ec-european-commission", call.getFunder().getFirst().getFunder().getId());
    }

    @Test
    void toRisCall_setsEuFundingCharacteristic() {
        RisCall call = (RisCall) mapper.toRisCall(resultWith(metaWithTitle("Test")));

        assertEquals(List.of(RisFundingCharacteristic.EU_FUNDING), call.getCharacteristics());
    }

    @Test
    void toRisCall_withUrl_setsWebsite() {
        EuTenderMetadata meta = metaWithTitle("Test");
        EuTenderResult result = resultWith(meta);
        result.setUrl("https://ec.europa.eu/some-call");

        RisCall call = (RisCall) mapper.toRisCall(result);

        assertNotNull(call.getWebsite());
        assertTrue(call.getWebsite().contains("https://ec.europa.eu/some-call"));
    }

    @Test
    void toRisCall_withIdentifierAndCallId_setsEuIdIdentifier() {
        EuTenderMetadata meta = new EuTenderMetadata();
        meta.setTitle(List.of("Test Call"));
        meta.setIdentifier(List.of("HORIZON-2023-MSCA-01"));
        meta.setCallccm2Id(List.of("42"));

        RisCall call = (RisCall) mapper.toRisCall(resultWith(meta));

        assertNotNull(call.getIdentifiers());
        assertEquals(1, call.getIdentifiers().size());
        assertEquals("HORIZON-2023-MSCA-01-[42]", call.getIdentifiers().getFirst().getValue());
    }

    @Test
    void toRisCall_withHtmlDescription_stripsTagsAndDecodesEntities() {
        EuTenderMetadata meta = new EuTenderMetadata();
        meta.setTitle(List.of("Test"));
        meta.setDescriptionByte(List.of("<p>Hello &amp; World</p><br/><div>Details</div>"));

        RisCall call = (RisCall) mapper.toRisCall(resultWith(meta));

        assertNotNull(call.getDescription());
        String text = call.getDescription().getFirst().getText();
        assertFalse(text.contains("<p>"), "HTML tags should be stripped");
        assertTrue(text.contains("&"), "HTML entities should be decoded");
        assertTrue(text.contains("Hello"), "Text content should be preserved");
    }

    @Test
    void toRisCall_withNoActions_returnsTypeCall() {
        EuTenderMetadata meta = metaWithTitle("Test");
        meta.setActions(List.of());

        RisCall call = (RisCall) mapper.toRisCall(resultWith(meta));

        assertEquals(RisFundingType.CALL, call.getType());
    }

    @Test
    void toRisCall_withNullMetadata_returnsTypeCall() {
        RisFunding result = mapper.toRisCall(resultWith(null));

        assertEquals(RisFundingType.CALL, result.getType());
    }

    @Test
    void toRisCall_withOpenStatusAndDeadline_returnsOngoingCall() {
        EuTenderMetadata meta = metaWithTitle("Open Call");
        meta.setActions(List.of(openActionJson()));

        RisCall call = (RisCall) mapper.toRisCall(resultWith(meta));

        assertEquals(RisFundingType.ONGOING_CALL, call.getType());
    }

    @Test
    void toRisCall_withClosedStatus_returnsTypeCall() {
        EuTenderMetadata meta = metaWithTitle("Closed Call");
        meta.setActions(List.of(closedActionJson()));

        RisCall call = (RisCall) mapper.toRisCall(resultWith(meta));

        assertEquals(RisFundingType.CALL, call.getType());
    }

    @Test
    void toRisCall_withMalformedActionsJson_fallsBackToTypeCall() {
        EuTenderMetadata meta = metaWithTitle("Test");
        meta.setActions(List.of("not-valid-json"));

        RisCall call = (RisCall) mapper.toRisCall(resultWith(meta));

        assertEquals(RisFundingType.CALL, call.getType());
    }

    @Test
    void toRisCall_withBudgetAcrossMultipleTopics_usesOwnTopicBudgetOnly() {
        EuTenderMetadata meta = metaWithTitle("Budget Test");
        meta.setCcm2Id(List.of("topic2"));
        meta.setBudgetOverview(List.of("""
            {
              "budgetTopicActionMap": {
                "topic1": [{"budgetYearMap": {"2023": 3000000}}],
                "topic2": [{"budgetYearMap": {"2023": 2000000, "2024": 500000}}]
              }
            }
            """));

        RisCall call = (RisCall) mapper.toRisCall(resultWith(meta));

        assertNotNull(call.getAmount());
        assertEquals(0, BigDecimal.valueOf(2500000).compareTo(call.getAmount().getAmount()));
        assertEquals("EUR", call.getAmount().getCurrency());
    }

    @Test
    void toRisCall_withoutCcm2Id_matchesTopicByActionIdentifier() {
        EuTenderMetadata meta = metaWithTitle("Budget Test");
        meta.setIdentifier(List.of("HORIZON-CL4-2023-DIGITAL-01-02"));
        meta.setBudgetOverview(List.of("""
            {
              "budgetTopicActionMap": {
                "111": [{"action": "HORIZON-CL4-2023-DIGITAL-01-01 - RIA", "budgetYearMap": {"2023": 3000000}}],
                "222": [{"action": "HORIZON-CL4-2023-DIGITAL-01-02 - RIA", "budgetYearMap": {"2023": 2000000}}]
              }
            }
            """));

        RisCall call = (RisCall) mapper.toRisCall(resultWith(meta));

        assertNotNull(call.getAmount());
        assertEquals(0, BigDecimal.valueOf(2000000).compareTo(call.getAmount().getAmount()));
    }

    @Test
    void toRisCall_withSingleTopicBudget_usesThatTopic() {
        EuTenderMetadata meta = metaWithTitle("Budget Test");
        meta.setBudgetOverview(List.of("""
            {
              "budgetTopicActionMap": {
                "topic1": [{"budgetYearMap": {"2023": 3000000}}]
              }
            }
            """));

        RisCall call = (RisCall) mapper.toRisCall(resultWith(meta));

        assertNotNull(call.getAmount());
        assertEquals(0, BigDecimal.valueOf(3000000).compareTo(call.getAmount().getAmount()));
    }

    @Test
    void toRisCall_withUnmatchableTopicBudget_doesNotSetAmount() {
        EuTenderMetadata meta = metaWithTitle("Budget Test");
        meta.setCcm2Id(List.of("topic3"));
        meta.setIdentifier(List.of("HORIZON-UNKNOWN"));
        meta.setBudgetOverview(List.of("""
            {
              "budgetTopicActionMap": {
                "topic1": [{"budgetYearMap": {"2023": 3000000}}],
                "topic2": [{"budgetYearMap": {"2023": 2000000}}]
              }
            }
            """));

        RisCall call = (RisCall) mapper.toRisCall(resultWith(meta));

        assertNotNull(call.getAmount());
        assertEquals(0, BigDecimal.ZERO.compareTo(call.getAmount().getAmount()));
    }

    @Test
    void toRisCall_withZeroBudget_setsZeroAmount() {
        EuTenderMetadata meta = metaWithTitle("Zero Budget");
        meta.setBudgetOverview(List.of("""
            {
              "budgetTopicActionMap": {
                "topic1": [{"budgetYearMap": {"2023": 0}}]
              }
            }
            """));

        RisCall call = (RisCall) mapper.toRisCall(resultWith(meta));

        assertNotNull(call.getAmount());
        assertEquals(0, BigDecimal.ZERO.compareTo(call.getAmount().getAmount()));
    }

    @Test
    void toRisCall_withMalformedBudgetJson_doesNotSetAmount() {
        EuTenderMetadata meta = metaWithTitle("Test");
        meta.setBudgetOverview(List.of("not-valid-json"));

        RisCall call = (RisCall) mapper.toRisCall(resultWith(meta));

        assertNull(call.getAmount());
    }

    @Test
    void toRisCall_withMatchingKeyword_mapsToSubject() {
        EuTenderMetadata meta = metaWithTitle("Test");
        // "Mathematics" is an exact match for OEFOS code "101"
        meta.setKeywords(List.of("Mathematics"));

        RisCall call = (RisCall) mapper.toRisCall(resultWith(meta));

        assertNotNull(call.getSubjects());
        assertTrue(call.getSubjects().stream().anyMatch(s -> "101".equals(s.getValue())),
            "Keyword 'Mathematics' should map to OEFOS subject code 101");
    }

    @Test
    void toRisCall_withUnknownKeyword_returnsEmptySubjects() {
        EuTenderMetadata meta = metaWithTitle("Test");
        meta.setKeywords(List.of("xyzzy-no-match"));

        RisCall call = (RisCall) mapper.toRisCall(resultWith(meta));

        assertNotNull(call.getSubjects());
        assertTrue(call.getSubjects().isEmpty());
    }

    @Test
    void toRisCall_withNullKeywords_returnsEmptySubjects() {
        EuTenderMetadata meta = metaWithTitle("Test");
        meta.setKeywords(null);

        RisCall call = (RisCall) mapper.toRisCall(resultWith(meta));

        assertNotNull(call.getSubjects());
        assertTrue(call.getSubjects().isEmpty());
    }
}
