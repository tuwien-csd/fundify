package at.ac.tuwien.fundify.bootstrap.e2e;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

import at.ac.tuwien.fundify.adapters.common.ris.mapper.RisCallMapper;
import at.ac.tuwien.fundify.adapters.common.ris.mapper.RisProgramMapper;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisCall;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisFunding;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisFundingType;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisProgramme;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding.CallMongoEntity;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding.FunderMongoEntity;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding.ProgramMongoEntity;
import at.ac.tuwien.fundify.adapters.out.ris.network.DatabaseSyncScheduler;
import at.ac.tuwien.fundify.adapters.out.ris.network.client.GenericRisFundingRestClient;
import at.ac.tuwien.fundify.application.port.out.persistence.CallQuery;
import at.ac.tuwien.fundify.application.port.out.persistence.ProgramQuery;
import at.ac.tuwien.fundify.application.port.out.ris.network.FundingRemoteRepository;
import at.ac.tuwien.fundify.domain.common.EPublicationStatus;
import at.ac.tuwien.fundify.domain.funding.Call;
import at.ac.tuwien.fundify.domain.funding.Program;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EEntryOrigin;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EIdentifierType;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class FundingSynchronizationTest {

    @Inject
    DatabaseSyncScheduler databaseSyncScheduler;

    @InjectMock
    FundingRemoteRepository fundingRemoteRepository;

    @Inject
    MockRestClient mockRestClient;

    @Inject
    CallQuery callQuery;

    @Inject
    ProgramQuery programQuery;

    @BeforeEach
    void cleanUp() {
        CallMongoEntity.deleteAll();
        ProgramMongoEntity.deleteAll();
        FunderMongoEntity.deleteAll();
    }

    @Test
    void whenJobTriggered_thenNewExternalRisFundingsArePersistedToDB() {
        List<Call> calls = fetchCalls(mockRestClient, "New_");
        List<Program> programs = fetchPrograms(mockRestClient, "New_");

        FunderMongoEntity funder = new FunderMongoEntity();
        funder.risId = "ris:MID:orgunit:1";
        funder.persist();

        when(fundingRemoteRepository.fetchAllCalls()).thenReturn(calls);
        when(fundingRemoteRepository.fetchAllPrograms()).thenReturn(programs);

        databaseSyncScheduler.synchronizeExternalFundingData();

        List<Call> syncedCalls = callQuery.find(EPublicationStatus.PUBLISHED);
        List<Program> syncedPrograms = programQuery.find(EPublicationStatus.PUBLISHED);
        Assertions.assertEquals(calls.size(), syncedCalls.size());
        Assertions.assertEquals(EPublicationStatus.PUBLISHED, syncedCalls.get(0).getStatus());
        Assertions.assertEquals(EEntryOrigin.ENDPOINT, syncedCalls.get(0).getEntryOrigin());

        Assertions.assertEquals(programs.size(), syncedPrograms.size());
        Assertions.assertEquals(EPublicationStatus.PUBLISHED, syncedPrograms.get(0).getStatus());
        Assertions.assertEquals(EEntryOrigin.ENDPOINT, syncedPrograms.get(0).getEntryOrigin());
    }

    @Test
    void whenJobTriggered_thenNewExternalRisFundingsHaveRegistrationDateAndLastSync() {
        List<Call> calls = fetchCalls(mockRestClient, "New_");
        List<Program> programs = fetchPrograms(mockRestClient, "New_");

        FunderMongoEntity funder = new FunderMongoEntity();
        funder.risId = "ris:MID:orgunit:1";
        funder.persist();

        when(fundingRemoteRepository.fetchAllCalls()).thenReturn(calls);
        when(fundingRemoteRepository.fetchAllPrograms()).thenReturn(programs);

        databaseSyncScheduler.synchronizeExternalFundingData();

        Call exampleCall = callQuery.find(EPublicationStatus.PUBLISHED).get(0);
        Assertions.assertNotNull(exampleCall.getRegistrationDate());
        Assertions.assertNotNull(exampleCall.getLastSync());
        Assertions.assertEquals(exampleCall.getRegistrationDate(), exampleCall.getLastSync());

        Program exampleProgram = programQuery.find(EPublicationStatus.PUBLISHED).get(0);
        Assertions.assertNotNull(exampleProgram.getRegistrationDate());
        Assertions.assertNotNull(exampleProgram.getLastSync());
        Assertions.assertEquals(exampleProgram.getRegistrationDate(), exampleProgram.getLastSync());
    }

    @Test
    void whenJobTriggered_thenNewExternalRisFundingsWithoutIdentifiersGetAssignedRisIdentifier() {
        List<Call> calls = fetchCalls(mockRestClient, "New_");
        List<Program> programs = fetchPrograms(mockRestClient, "New_");

        FunderMongoEntity funder = new FunderMongoEntity();
        funder.risId = "ris:MID:orgunit:1";
        funder.persist();

        when(fundingRemoteRepository.fetchAllCalls()).thenReturn(calls);
        when(fundingRemoteRepository.fetchAllPrograms()).thenReturn(programs);

        databaseSyncScheduler.synchronizeExternalFundingData();

        Call exampleCall = callQuery.find(EPublicationStatus.PUBLISHED).get(0);
        assertThat(exampleCall.getExternalIdentifier().getIdentifiers())
                .isNotEmpty()
                .anyMatch(identifier ->
                        identifier.type() == EIdentifierType.RIS_SYNERGY &&
                                identifier.value().startsWith("ris:MID:funding:")
                );

      Program exampleProgram = programQuery.find(EPublicationStatus.PUBLISHED).get(0);
        assertThat(exampleProgram.getExternalIdentifier().getIdentifiers())
                .isNotEmpty()
                .anyMatch(identifier ->
                        identifier.type() == EIdentifierType.RIS_SYNERGY &&
                                identifier.value().startsWith("ris:MID:funding:")
                );
    }

@Test
void whenJobTriggered_thenExistingExternalRisFundingsAreUpdatedInDB() {
    FunderMongoEntity funder = new FunderMongoEntity();
    funder.risId = "ris:MID:orgunit:1";
    funder.persist();

    List<Call> existingCalls = fetchCalls(mockRestClient, "Existing_");
    List<Program> existingPrograms = fetchPrograms(mockRestClient, "Existing_");
    when(fundingRemoteRepository.fetchAllCalls()).thenReturn(existingCalls);
    when(fundingRemoteRepository.fetchAllPrograms()).thenReturn(existingPrograms);
    databaseSyncScheduler.synchronizeExternalFundingData();

    List<Call> updatedCalls = fetchCalls(mockRestClient, "Updated_");
    List<Program> updatedPrograms = fetchPrograms(mockRestClient, "Updated_");
    when(fundingRemoteRepository.fetchAllCalls()).thenReturn(updatedCalls);
    when(fundingRemoteRepository.fetchAllPrograms()).thenReturn(updatedPrograms);
    databaseSyncScheduler.synchronizeExternalFundingData();

    List<Call> syncedCalls = callQuery.find(EPublicationStatus.PUBLISHED);
    List<Program> syncedPrograms = programQuery.find(EPublicationStatus.PUBLISHED);
    Assertions.assertEquals(existingCalls.size(), syncedCalls.size());
    Assertions.assertEquals(EPublicationStatus.PUBLISHED, syncedCalls.get(0).getStatus());
    Assertions.assertEquals(EEntryOrigin.ENDPOINT, syncedCalls.get(0).getEntryOrigin());

    Assertions.assertEquals(updatedPrograms.size(), syncedPrograms.size());
    Assertions.assertEquals(EPublicationStatus.PUBLISHED, syncedPrograms.get(0).getStatus());
    Assertions.assertEquals(EEntryOrigin.ENDPOINT, syncedPrograms.get(0).getEntryOrigin());
}

@Test
void whenJobTriggered_thenExistingExternalRisFundingsHaveUpdatedLastSync() {
    FunderMongoEntity funder = new FunderMongoEntity();
    funder.risId = "ris:MID:orgunit:1";
    funder.persist();

    List<Call> existingCalls = fetchCalls(mockRestClient, "Existing_");
    List<Program> existingPrograms = fetchPrograms(mockRestClient, "Existing_");
    when(fundingRemoteRepository.fetchAllCalls()).thenReturn(existingCalls);
    when(fundingRemoteRepository.fetchAllPrograms()).thenReturn(existingPrograms);
    databaseSyncScheduler.synchronizeExternalFundingData();

    List<Call> updatedCalls = fetchCalls(mockRestClient, "Updated_");
    List<Program> updatedPrograms = fetchPrograms(mockRestClient, "Updated_");
    when(fundingRemoteRepository.fetchAllCalls()).thenReturn(updatedCalls);
    when(fundingRemoteRepository.fetchAllPrograms()).thenReturn(updatedPrograms);
    databaseSyncScheduler.synchronizeExternalFundingData();

    Call exampleSyncedCall = callQuery.find(EPublicationStatus.PUBLISHED).get(0);
    Assertions.assertNotNull(exampleSyncedCall.getRegistrationDate());
    Assertions.assertNotEquals(exampleSyncedCall.getRegistrationDate(), exampleSyncedCall.getLastSync());

    Program exampleSyncedProgram = programQuery.find(EPublicationStatus.PUBLISHED).get(0);
    Assertions.assertNotNull(exampleSyncedProgram.getRegistrationDate());
    Assertions.assertNotEquals(exampleSyncedProgram.getRegistrationDate(), exampleSyncedProgram.getLastSync());
}

private List<Program> fetchPrograms(GenericRisFundingRestClient client, String acronymSuffix) {
    List<RisFunding> programs = client.getFundings(RisFundingType.PROGRAMME);

    return programs.stream()
            .map(RisProgramme.class::cast)
            .peek(p -> p.setAcronym(acronymSuffix + p.getAcronym()))
            .map(p -> RisProgramMapper.INSTANCE.toDomain(p, client.getMemberId()))
            .toList();
}

private List<Call> fetchCalls(GenericRisFundingRestClient client, String acronymSuffix) {
    List<RisFunding> calls = client.getFundings(RisFundingType.CALL);

    return calls.stream()
            .map(RisCall.class::cast)
            .peek(c -> c.setAcronym(acronymSuffix + c.getAcronym()))
            .map(c -> RisCallMapper.INSTANCE.toDomain(c, client.getMemberId()))
            .toList();
}
}