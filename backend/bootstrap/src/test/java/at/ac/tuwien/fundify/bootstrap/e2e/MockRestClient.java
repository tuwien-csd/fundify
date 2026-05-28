package at.ac.tuwien.fundify.bootstrap.e2e;

import at.ac.tuwien.fundify.adapters.common.ris.model.v1.*;
import at.ac.tuwien.fundify.adapters.out.ris.network.client.GenericRisFundingRestClient;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class MockRestClient implements GenericRisFundingRestClient {

    @Override
    public List<RisFunding> getFundings(RisFundingType fundingType) {
        if (fundingType == RisFundingType.CALL) {
            return getRisCalls();
        } else if (fundingType == RisFundingType.PROGRAMME) {
            return getRisPrograms();
        }
        return List.of();
    }

    @Override
    public RisFunding getFunding(String id) {
        return null;
    }

    @Override
    public String getMemberId() {
        return "mid";
    }

    private List<RisFunding> getRisCalls() {
        RisCall mockCall = new RisCall();
        mockCall.setId("1");
        mockCall.setType(RisFundingType.CALL);
        mockCall.setAcronym("Call 1");
        mockCall.setFunder(getFunders());

        return List.of(mockCall);
    }

    private List<RisFunding> getRisPrograms() {
        RisFunding mockProgram = new RisProgramme();
        mockProgram.setId("1");
        mockProgram.setType(RisFundingType.PROGRAMME);
        mockProgram.setAcronym("Program 1");
        mockProgram.setFunder(getFunders());

        return List.of(mockProgram);
    }

    private List<RisFunder> getFunders() {
        RisFunder mockRisFunder = new RisFunder();
        mockRisFunder.setFunderType(RisOrganisationFundingRoleEnum.EXECUTIVE_ORGANISATION);

        RisOrgUnit mockOrgUnit = new RisOrgUnit();
        mockOrgUnit.setId("1");
        mockOrgUnit.acronym("Funder 1");
        mockRisFunder.setFunder(mockOrgUnit);

        return List.of(mockRisFunder);
    }
}