package at.ac.tuwien.refop.application.port.out.ris.network;

import java.util.List;

import at.ac.tuwien.refop.domain.funding.Call;
import at.ac.tuwien.refop.domain.funding.Program;

public interface FundingRemoteRepository {
    List<Program> fetchAllPrograms();
    List<Call> fetchAllCalls();
}