package at.ac.tuwien.fundify.application.port.out.persistence;

import at.ac.tuwien.fundify.domain.common.ProgramId;
import at.ac.tuwien.fundify.domain.funding.ProgramVersion;
import java.util.List;

public interface ProgramVersionRepository {

    void persist(ProgramVersion programVersion);

    List<ProgramVersion> findByProgramId(ProgramId programId);
}
