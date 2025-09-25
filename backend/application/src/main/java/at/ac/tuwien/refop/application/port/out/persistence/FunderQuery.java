package at.ac.tuwien.refop.application.port.out.persistence;

import at.ac.tuwien.refop.domain.common.FunderId;
import at.ac.tuwien.refop.domain.common.RisId;
import at.ac.tuwien.refop.domain.funding.Funder;
import at.ac.tuwien.refop.domain.funding.FunderReference;

import java.util.List;
import java.util.Optional;

public interface FunderQuery {

    Optional<Funder> findById(FunderId id);

    Optional<Funder> findByEmailDomain(String emailDomain);

    List<Funder> listAllFunders();

    Optional<FunderReference> findReferenceById(FunderId id);

    List<FunderReference> findReferencesByIds(List<FunderId> ids);

    Optional<FunderReference> findReferenceByRisId(RisId risId);

    Optional<FunderReference> findReferenceByEmailDomain(String emailDomain);

    List<FunderReference> listAllFunderReferences();

    List<FunderReference> searchReferences(String query);
}