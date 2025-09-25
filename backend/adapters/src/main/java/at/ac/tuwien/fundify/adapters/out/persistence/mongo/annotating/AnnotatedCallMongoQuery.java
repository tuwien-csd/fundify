package at.ac.tuwien.fundify.adapters.out.persistence.mongo.annotating;

import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.MongoCrossReferenceResolver;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.ObjectIdUtils;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding.CallIdentifierProjection;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding.CallMongoEntity;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding.CallMongoEntityQueryBuilder;
import at.ac.tuwien.fundify.application.port.out.persistence.AnnotatedCallQuery;
import at.ac.tuwien.fundify.domain.annotating.AnnotatedCallId;
import at.ac.tuwien.fundify.domain.annotating.UniversityId;
import at.ac.tuwien.fundify.domain.common.CallId;
import at.ac.tuwien.fundify.domain.common.EPublicationStatus;
import at.ac.tuwien.fundify.domain.common.ETargetGroup;
import at.ac.tuwien.fundify.domain.common.FunderId;
import at.ac.tuwien.fundify.domain.dto.AnnotatedCallDTO;
import at.ac.tuwien.fundify.domain.dto.CallPreviewDTO;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EAustrianState;
import at.ac.tuwien.fundify.domain.funding.vo.enums.ECallType;
import at.ac.tuwien.fundify.domain.funding.vo.enums.ERegionalScope;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;


@ApplicationScoped
@RequiredArgsConstructor
public class AnnotatedCallMongoQuery implements AnnotatedCallQuery {

    private final MongoCrossReferenceResolver mongoCrossReferenceResolver;

    @Override
    public Optional<AnnotatedCallDTO> find(UniversityId universityId, CallId callId, EPublicationStatus status) {
        AnnotatedCallMongoEntity entity = AnnotatedCallMongoEntity
                .find("universityId = ?1 and callId = ?2 and status = ?3",
                        ObjectIdUtils.toObjectId(universityId.value()),
                        ObjectIdUtils.toObjectId(callId.value()),
                        status)
                .firstResult();
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(AnnotatedCallMongoEntityMapper.INSTANCE.toDTO(entity, mongoCrossReferenceResolver));
    }

    @Override
    public Optional<AnnotatedCallDTO> find(AnnotatedCallId id) {
        AnnotatedCallMongoEntity entity = AnnotatedCallMongoEntity.findById(ObjectIdUtils.toObjectId(id.value()));
        return Optional.of(AnnotatedCallMongoEntityMapper.INSTANCE.toDTO(entity, mongoCrossReferenceResolver));
    }

    @Override
    public Optional<AnnotatedCallDTO> find(CallId callId, UniversityId universityId) {
        AnnotatedCallMongoEntity entity = AnnotatedCallMongoEntity
                .find(
                        "callId = ?1 and universityId = ?2",
                        ObjectIdUtils.toObjectId(callId.value()),
                        ObjectIdUtils.toObjectId(universityId.value())
                ).firstResult();

        if (entity == null) {
            return Optional.empty();
        }

        return Optional.of(AnnotatedCallMongoEntityMapper.INSTANCE.toDTO(entity, mongoCrossReferenceResolver));
    }


    @Override
    public List<AnnotatedCallDTO> find(UniversityId universityId) {
        ObjectId universityObjectId = ObjectIdUtils.toObjectId(universityId.value());
        List<AnnotatedCallMongoEntity> entities = AnnotatedCallMongoEntity
                .find("universityId", universityObjectId)
                .list();

        return AnnotatedCallMongoEntityMapper.INSTANCE.toDTO(entities, mongoCrossReferenceResolver);
    }

    @Override
    public Optional<CallPreviewDTO> find(CallId callId) {
        CallPreviewProjection result = CallMongoEntity
                .find("_id", ObjectIdUtils.toObjectId(callId.value()))
                .project(CallPreviewProjection.class)
                .firstResult();
        if (result == null) {
            return Optional.empty();
        }
        return Optional.of(CallPreviewProjectionMapper.INSTANCE.toDTO(result, mongoCrossReferenceResolver));
    }

  @Override
  public List<AnnotatedCallDTO> findAll() {
    List<AnnotatedCallMongoEntity> entities = AnnotatedCallMongoEntity
        .findAll()
        .list();
    return AnnotatedCallMongoEntityMapper.INSTANCE.toDTO(entities, mongoCrossReferenceResolver);
  }

  @Override
    public List<AnnotatedCallDTO> find(
            UniversityId universityId,
            ECallType callType,
            ETargetGroup targetGroup,
            Boolean runningCalls,
            EAustrianState region,
            FunderId funderId,
            ERegionalScope applicantsScope,
            EPublicationStatus status
    ) {
        List<CallIdentifierProjection> idMappings = queryCallIdentifierProjections(
                callType,
                targetGroup,
                runningCalls,
                region,
                funderId,
                applicantsScope,
                status
        );
        List<AnnotatedCallMongoEntity> result = idMappings.stream().map(
                idMapping -> (AnnotatedCallMongoEntity) AnnotatedCallMongoEntity.find(
                        "callId = ?1 and universityId = ?2",
                        idMapping._id(),
                        ObjectIdUtils.toObjectId(universityId.value())
                ).firstResult()
        ).filter(Objects::nonNull).toList();

        return AnnotatedCallMongoEntityMapper.INSTANCE.toDTO(result, mongoCrossReferenceResolver);
    }

    private List<CallIdentifierProjection> queryCallIdentifierProjections(
            ECallType callType,
            ETargetGroup targetGroup,
            Boolean runningCalls,
            EAustrianState region,
            FunderId funderId,
            ERegionalScope applicantsScope,
            EPublicationStatus status) {

        return CallMongoEntity.find(
                new CallMongoEntityQueryBuilder()
                        .withCallType(callType)
                        .withTargetGroup(targetGroup)
                        .withRunningCalls(runningCalls)
                        .withRegion(region)
                        .withFunderId(funderId)
                        .withApplicantsScope(applicantsScope)
                        .withStatus(status)
                        .build()
        ).project(CallIdentifierProjection.class).list();
    }
}