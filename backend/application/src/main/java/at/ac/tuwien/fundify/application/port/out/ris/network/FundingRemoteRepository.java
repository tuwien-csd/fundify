package at.ac.tuwien.fundify.application.port.out.ris.network;

import java.util.List;

import at.ac.tuwien.fundify.domain.funding.Call;
import at.ac.tuwien.fundify.domain.funding.Program;

public interface FundingRemoteRepository {
    List<Program> fetchAllPrograms();
    List<Call> fetchAllCalls();
}