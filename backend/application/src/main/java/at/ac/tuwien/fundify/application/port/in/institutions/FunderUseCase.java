package at.ac.tuwien.fundify.application.port.in.institutions;

import at.ac.tuwien.fundify.domain.common.FunderId;
import at.ac.tuwien.fundify.domain.common.exceptions.FundifyException;
import at.ac.tuwien.fundify.domain.funding.Funder;
import at.ac.tuwien.fundify.domain.funding.FunderCreate;
import at.ac.tuwien.fundify.domain.funding.FunderReference;
import java.util.List;

public interface FunderUseCase {

    // CRUD operations for Funder
    Funder addFunder(FunderCreate funder);

    Funder getFunder(FunderId id) throws FundifyException;

    Funder updateFunder(Funder funder) throws FundifyException;

    void deleteFunder(FunderId id) throws FundifyException;

    List<Funder> getAllFunders();

    // CRUD operations for AnnotatingFunderReference
    FunderReference getFunderReference(FunderId id) throws FundifyException;

    List<FunderReference> getAllFunderReferences();

    List<FunderReference> searchFunderReferences(String query);
}