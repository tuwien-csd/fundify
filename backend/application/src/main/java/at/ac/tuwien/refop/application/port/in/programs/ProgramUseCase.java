package at.ac.tuwien.refop.application.port.in.programs;

import at.ac.tuwien.refop.domain.common.ProgramId;
import at.ac.tuwien.refop.domain.common.exceptions.FundifyException;
import at.ac.tuwien.refop.domain.funding.Program;

public interface ProgramUseCase {

    // CRUD operations for Program
    Program addProgram(Program program) throws FundifyException;

    Program getProgram(ProgramId id) throws FundifyException;

    Program updateProgram(Program program) throws FundifyException;

    void deleteProgram(ProgramId id) throws FundifyException;

}
