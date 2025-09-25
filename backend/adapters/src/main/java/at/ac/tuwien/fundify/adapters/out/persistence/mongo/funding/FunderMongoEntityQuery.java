package at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding;

import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.ObjectIdUtils;
import at.ac.tuwien.fundify.application.port.out.persistence.FunderQuery;
import at.ac.tuwien.fundify.domain.common.FunderId;
import at.ac.tuwien.fundify.domain.common.RisId;
import at.ac.tuwien.fundify.domain.funding.Funder;
import at.ac.tuwien.fundify.domain.funding.FunderReference;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import lombok.extern.jbosslog.JBossLog;
import org.bson.types.ObjectId;

@JBossLog
@ApplicationScoped
public class FunderMongoEntityQuery implements FunderQuery {

    private static final String MONGO_ID_PROPERTY = "_id";
    private static final String RIS_ID_PROPERTY = "risId";
    private static final String EMAIL_DOMAIN_PROPERTY = "emailDomain";

    @Override
    public Optional<Funder> findById(FunderId id) {
        FunderMongoEntity entity = FunderMongoEntity.findById(ObjectIdUtils.toObjectId(id.value()));
        return Optional.ofNullable(FunderMongoEntityMapper.INSTANCE.toDomain(entity));
    }

    @Override
    public Optional<Funder> findByEmailDomain(String emailDomain) {
        FunderMongoEntity entity = FunderMongoEntity.find(EMAIL_DOMAIN_PROPERTY, emailDomain).firstResult();
        return Optional.ofNullable(FunderMongoEntityMapper.INSTANCE.toDomain(entity));
    }

    @Override
    public List<Funder> listAllFunders() {
        return FunderMongoEntityMapper.INSTANCE.toDomain(FunderMongoEntity.listAll());
    }

    @Override
    public Optional<FunderReference> findReferenceById(FunderId id) {
        FunderReferenceProjection result = FunderMongoEntity.find(MONGO_ID_PROPERTY, ObjectIdUtils.toObjectId(id.value()))
                .project(FunderReferenceProjection.class).firstResult();
        return Optional.ofNullable(FunderReferenceProjectionMapper.INSTANCE.toFundingDomain(result));
    }

    @Override
    public Optional<FunderReference> findReferenceByRisId(RisId risId) {
        FunderReferenceProjection result = FunderMongoEntity.find(RIS_ID_PROPERTY, risId.toString()).project(FunderReferenceProjection.class).firstResult();
        return Optional.ofNullable(FunderReferenceProjectionMapper.INSTANCE.toFundingDomain(result));
    }

    @Override
    public List<FunderReference> findReferencesByIds(List<FunderId> ids) {
        List<ObjectId> objectIds = ids.stream().map(id -> ObjectIdUtils.toObjectId(id.value())).toList();
        return FunderReferenceProjectionMapper.INSTANCE.toFundingDomain(
                FunderMongoEntity.find("_id in ?1", objectIds).project(FunderReferenceProjection.class).list()
        );
    }

    @Override
    public Optional<FunderReference> findReferenceByEmailDomain(String emailDomain) {
        FunderReferenceProjection result = FunderMongoEntity.find(EMAIL_DOMAIN_PROPERTY, emailDomain)
                .project(FunderReferenceProjection.class).firstResult();
        return Optional.ofNullable(FunderReferenceProjectionMapper.INSTANCE.toFundingDomain(result));
    }

    @Override
    public List<FunderReference> listAllFunderReferences() {
        return FunderReferenceProjectionMapper.INSTANCE.toFundingDomain(FunderMongoEntity.findAll()
                .project(FunderReferenceProjection.class).list());
    }

    @Override
    public List<FunderReference> searchReferences(String query) {
        return FunderReferenceProjectionMapper.INSTANCE.toFundingDomain(
                FunderMongoEntity.find("acronym like ?1 or name.text like ?1", "/.*" + query + ".*/i")
                        .project(FunderReferenceProjection.class).list()
        );
    }
}
