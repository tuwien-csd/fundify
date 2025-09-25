package at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding;

import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.ObjectIdUtils;
import at.ac.tuwien.fundify.application.port.out.persistence.FunderRepository;
import at.ac.tuwien.fundify.domain.common.FunderId;
import at.ac.tuwien.fundify.domain.common.RisId;
import at.ac.tuwien.fundify.domain.funding.Funder;
import at.ac.tuwien.fundify.domain.funding.FunderCreate;
import at.ac.tuwien.fundify.domain.funding.FunderReference;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import lombok.extern.jbosslog.JBossLog;
import org.bson.types.ObjectId;

@JBossLog
@ApplicationScoped
public class FunderMongoRepository implements FunderRepository, PanacheMongoRepository<FunderMongoEntity> {

    @Override
    public Funder persist(Funder funder) {
        FunderMongoEntity entity = FunderMongoEntityMapper.INSTANCE.fromDomain(funder);
        persist(entity);
        return FunderMongoEntityMapper.INSTANCE.toDomain(entity);
    }

    @Override
    public Funder add(FunderCreate funder) {
        FunderMongoEntity entity = FunderMongoEntityMapper.INSTANCE.fromDomain(funder);
        persist(entity);
        return FunderMongoEntityMapper.INSTANCE.toDomain(entity);
    }

  @Override
    public Optional<Funder> update(Funder funder) {
        FunderMongoEntity entity = FunderMongoEntityMapper.INSTANCE.fromDomain(funder);
        update(entity);
        return Optional.ofNullable(FunderMongoEntityMapper.INSTANCE.toDomain(entity));
    }

    @Override
    public boolean delete(FunderId id) {
        return deleteById(ObjectIdUtils.toObjectId(id.value()));
    }

    @Override
    public Optional<Funder> findById(FunderId id) {
        FunderMongoEntity entity = findById(ObjectIdUtils.toObjectId(id.value()));
        return Optional.ofNullable(FunderMongoEntityMapper.INSTANCE.toDomain(entity));
    }

    @Override
    public Optional<Funder> findByEmailDomain(String emailDomain) {
        FunderMongoEntity entity = find("emailDomain", emailDomain).firstResult();
        return Optional.ofNullable(FunderMongoEntityMapper.INSTANCE.toDomain(entity));
    }

    @Override
    public List<Funder> listAllFunders() {
        return FunderMongoEntityMapper.INSTANCE.toDomain(listAll());
    }

    @Override
    public Optional<FunderReference> findReferenceById(FunderId id) {
        FunderReferenceProjection result = find("_id", ObjectIdUtils.toObjectId(id.value())).project(FunderReferenceProjection.class).firstResult();
        return Optional.ofNullable(FunderReferenceProjectionMapper.INSTANCE.toFundingDomain(result));
    }

    @Override
    public Optional<FunderReference> findReferenceByRisId(RisId risId) {
        FunderReferenceProjection result = find("risId", risId.toString()).project(FunderReferenceProjection.class).firstResult();
        return Optional.ofNullable(FunderReferenceProjectionMapper.INSTANCE.toFundingDomain(result));
    }

    @Override
    public List<FunderReference> findReferencesByIds(List<FunderId> ids) {
        List<ObjectId> objectIds = ids.stream().map(id -> ObjectIdUtils.toObjectId(id.value())).toList();
        return FunderReferenceProjectionMapper.INSTANCE.toFundingDomain(
                find("_id in ?1", objectIds).project(FunderReferenceProjection.class).list()
        );
    }

    @Override
    public Optional<FunderReference> findReferenceByEmailDomain(String emailDomain) {
        FunderReferenceProjection result = find("emailDomain", emailDomain).project(FunderReferenceProjection.class).firstResult();
        return Optional.ofNullable(FunderReferenceProjectionMapper.INSTANCE.toFundingDomain(result));
    }

    @Override
    public List<FunderReference> listAllFunderReferences() {
        return FunderReferenceProjectionMapper.INSTANCE.toFundingDomain(findAll().project(FunderReferenceProjection.class).list());
    }

    @Override
    public List<FunderReference> searchReferences(String query) {
        return FunderReferenceProjectionMapper.INSTANCE.toFundingDomain(
                find("acronym like ?1 or name.text like ?1", "/.*" + query + ".*/i")
                        .project(FunderReferenceProjection.class).list()
        );
    }
}
