package at.ac.tuwien.refop.adapters.out.persistence.mongo.funding;

import at.ac.tuwien.refop.adapters.out.persistence.mongo.common.MongoCrossReferenceResolver;
import at.ac.tuwien.refop.adapters.out.persistence.mongo.common.ObjectIdUtils;
import at.ac.tuwien.refop.application.port.out.persistence.ProgramQuery;
import at.ac.tuwien.refop.domain.common.EPublicationStatus;
import at.ac.tuwien.refop.domain.common.ETargetGroup;
import at.ac.tuwien.refop.domain.common.FunderId;
import at.ac.tuwien.refop.domain.common.ProgramId;
import at.ac.tuwien.refop.domain.common.RisId;
import at.ac.tuwien.refop.domain.funding.Program;
import at.ac.tuwien.refop.domain.funding.ProgramReference;
import at.ac.tuwien.refop.domain.funding.vo.enums.EAustrianState;
import at.ac.tuwien.refop.domain.funding.vo.enums.ERegionalScope;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class ProgramMongoEntityQuery implements ProgramQuery {

    private static final String MONGO_ID_PROPERTY = "_id";
    private static final String STATUS_PROPERTY = "status";
    private static final String FUNDER_ID_PROPERTY = "funderId";
    private static final String RIS_ID_PROPERTY = "risId";

    private final MongoCrossReferenceResolver mongoCrossReferenceResolver;

    @Override
    public Optional<Program> find(RisId risId) {
        ProgramMongoEntity program = ProgramMongoEntity.find(RIS_ID_PROPERTY, risId.toString()).firstResult();
        if (program == null) {
            return Optional.empty();
        }
        return Optional.of(ProgramMongoEntityMapper.INSTANCE.toDomain(program, mongoCrossReferenceResolver));
    }

    @Override
    public Optional<Program> find(RisId risId, EPublicationStatus status) {
        ProgramMongoEntity program = ProgramMongoEntity.find(
                new ProgramMongoEntityQueryBuilder()
                        .withRisId(risId)
                        .withStatus(status)
                        .build()
        ).firstResult();

        if (program == null) {
            return Optional.empty();
        }
        return Optional.of(ProgramMongoEntityMapper.INSTANCE.toDomain(program, mongoCrossReferenceResolver));
    }

    @Override
    public Optional<Program> find(ProgramId programId) {
        ProgramMongoEntity program = ProgramMongoEntity.findById(ObjectIdUtils.toObjectId(
            programId.value()));
        if (program == null) {
            return Optional.empty();
        }
        return Optional.of(ProgramMongoEntityMapper.INSTANCE.toDomain(program, mongoCrossReferenceResolver));
    }

    @Override
    public List<Program> findAll() {
      List<ProgramMongoEntity> programMongoEntities = CallMongoEntity.listAll();
      return ProgramMongoEntityMapper.INSTANCE.toDomain(programMongoEntities, mongoCrossReferenceResolver);
    }

    @Override
    public List<Program> find(EPublicationStatus status) {
        List<ProgramMongoEntity> programMongoEntities = ProgramMongoEntity
                .list(STATUS_PROPERTY, status);
        return ProgramMongoEntityMapper.INSTANCE.toDomain(programMongoEntities, mongoCrossReferenceResolver);
    }

    @Override
    public List<Program> find(FunderId funderId, EPublicationStatus status) {
        List<ProgramMongoEntity> result = ProgramMongoEntity
                .find(new ProgramMongoEntityQueryBuilder()
                        .withFunderId(funderId)
                        .withStatus(status)
                        .build()
                ).list();
        return ProgramMongoEntityMapper.INSTANCE.toDomain(result, mongoCrossReferenceResolver);
    }

    @Override
    public List<Program> find(
            ETargetGroup targetGroup,
            EAustrianState region,
            FunderId funderId,
            ERegionalScope applicantsScope,
            EPublicationStatus status
    ) {
        List<ProgramMongoEntity> result = ProgramMongoEntity.find(
                new ProgramMongoEntityQueryBuilder()
                        .withTargetGroup(targetGroup)
                        .withRegion(region)
                        .withFunderId(funderId)
                        .withApplicantsScope(applicantsScope)
                        .withStatus(status)
                        .build()
        ).list();
        return ProgramMongoEntityMapper.INSTANCE.toDomain(result, mongoCrossReferenceResolver);
    }

    @Override
    public Optional<ProgramReference> findReference(ProgramId id) {
        if (id == null) {
            return Optional.empty();
        }
        ProgramReferenceProjection programReferenceProjection = ProgramMongoEntity
                .find(MONGO_ID_PROPERTY, ObjectIdUtils.toObjectId(id.value()))
                .project(ProgramReferenceProjection.class).firstResult();

        return programReferenceProjection == null ? Optional.empty() :
                Optional.of(ProgramReferenceProjectionMapper.INSTANCE
                        .toFundingDomainUnresolved(programReferenceProjection));
    }

    @Override
    public Optional<ProgramReference> findReference(RisId risId) {
        ProgramReferenceProjection programReferenceProjection = ProgramMongoEntity.find(RIS_ID_PROPERTY, risId.toString())
                .project(ProgramReferenceProjection.class).firstResult();

        return programReferenceProjection == null ? Optional.empty() :
                Optional.of(ProgramReferenceProjectionMapper.INSTANCE
                        .toFundingDomainUnresolved(programReferenceProjection));

    }

    @Override
    public List<ProgramReference> findReference(EPublicationStatus status) {
        List<ProgramReferenceProjection> programReferenceProjections = ProgramMongoEntity
                .find(STATUS_PROPERTY, EPublicationStatus.PUBLISHED)
                .project(ProgramReferenceProjection.class)
                .list();

        return ProgramReferenceProjectionMapper.INSTANCE.toFundingDomainUnresolved(programReferenceProjections);
    }

    @Override
    public List<ProgramReference> findReference(FunderId funderId) {
        List<ProgramReferenceProjection> programReferenceProjections = ProgramMongoEntity
                .find(FUNDER_ID_PROPERTY, ObjectIdUtils.toObjectId(funderId.value()))
                .project(ProgramReferenceProjection.class)
                .list();

        return ProgramReferenceProjectionMapper.INSTANCE.toFundingDomainUnresolved(programReferenceProjections);
    }
}
