package at.ac.tuwien.fundify.application.port.out.persistence;

import at.ac.tuwien.fundify.domain.funding.Program;
import at.ac.tuwien.fundify.domain.common.ProgramId;

import java.util.Optional;

public interface ProgramRepository {


    Program persist(Program program);

    Optional<Program> update(Program program);

    boolean delete(ProgramId id);

    Optional<Program> findById(ProgramId id);

}