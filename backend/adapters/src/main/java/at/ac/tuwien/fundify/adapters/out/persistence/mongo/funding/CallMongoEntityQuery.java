package at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding;

import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.MongoCrossReferenceResolver;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.ObjectIdUtils;
import at.ac.tuwien.fundify.application.port.out.persistence.CallQuery;
import at.ac.tuwien.fundify.domain.common.CallId;
import at.ac.tuwien.fundify.domain.common.EPublicationStatus;
import at.ac.tuwien.fundify.domain.common.ETargetGroup;
import at.ac.tuwien.fundify.domain.common.FunderId;
import at.ac.tuwien.fundify.domain.common.RisId;
import at.ac.tuwien.fundify.domain.dto.CallDTO;
import at.ac.tuwien.fundify.domain.funding.Call;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EAustrianState;
import at.ac.tuwien.fundify.domain.funding.vo.enums.ECallType;
import at.ac.tuwien.fundify.domain.funding.vo.enums.ERegionalScope;
import org.bson.Document;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CallMongoEntityQuery implements CallQuery {

    private static final String STATUS_PROPERTY = "status";
    private static final String FUNDER_ID_PROPERTY = "funderId";
    private static final String RIS_ID_PROPERTY = "risId";

    private final MongoCrossReferenceResolver mongoCrossReferenceResolver;

    @Inject
    CallMongoEntityQuery(MongoCrossReferenceResolver mongoCrossReferenceResolver) {
        this.mongoCrossReferenceResolver = mongoCrossReferenceResolver;
    }

    @Override
    public Optional<Call> find(CallId callId) {
      CallMongoEntity call = CallMongoEntity.findById(ObjectIdUtils.toObjectId(callId.value()));
        if (call == null) {
            return Optional.empty();
        }
        return Optional.of(CallMongoEntityMapper.INSTANCE.toDomain(call, mongoCrossReferenceResolver));
    }

    @Override
    public List<Call> findAll() {
        List<CallMongoEntity> calls = CallMongoEntity.listAll();
        return CallMongoEntityMapper.INSTANCE.toDomain(calls, mongoCrossReferenceResolver);
    }

    @Override
    public Optional<Call> find(RisId risId) {
        CallMongoEntity call = CallMongoEntity.find(RIS_ID_PROPERTY, risId.toString()).firstResult();
        if (call == null) {
            return Optional.empty();
        }
        return Optional.of(CallMongoEntityMapper.INSTANCE.toDomain(call, mongoCrossReferenceResolver));
    }

    @Override
    public Optional<Call> find(RisId risId, EPublicationStatus status) {
        CallMongoEntity call = CallMongoEntity.find(
                new CallMongoEntityQueryBuilder()
                        .withRisId(risId)
                        .withStatus(status)
                        .build()
        ).firstResult();

        if (call == null) {
            return Optional.empty();
        }
        return Optional.of(CallMongoEntityMapper.INSTANCE.toDomain(call, mongoCrossReferenceResolver));
    }

    @Override
    public List<Call> find(EPublicationStatus status) {
        List<CallMongoEntity> call = CallMongoEntity.find(STATUS_PROPERTY, status).list();
        return CallMongoEntityMapper.INSTANCE.toDomain(call, mongoCrossReferenceResolver);
    }

    @Override
    public List<Call> find(FunderId funderId) {
        List<CallMongoEntity> calls = CallMongoEntity.find(FUNDER_ID_PROPERTY, ObjectIdUtils.toObjectId(funderId.value())).list();
        return CallMongoEntityMapper.INSTANCE.toDomain(calls, mongoCrossReferenceResolver);
    }

    @Override
    public List<Call> find(FunderId funderId, EPublicationStatus status) {
        List<CallMongoEntity> calls = CallMongoEntity.find(
                new CallMongoEntityQueryBuilder()
                        .withFunderId(funderId)
                        .withStatus(status)
                        .build()
        ).list();
        return CallMongoEntityMapper.INSTANCE.toDomain(calls, mongoCrossReferenceResolver);
    }

    @Override
    public List<Call> find(
            ECallType callType,
            ETargetGroup targetGroup,
            Boolean runningCalls,
            EAustrianState region,
            FunderId funderId,
            ERegionalScope applicantsScope,
            EPublicationStatus status) {

        List<CallMongoEntity> result = CallMongoEntity.find(
                new CallMongoEntityQueryBuilder()
                        .withCallType(callType)
                        .withTargetGroup(targetGroup)
                        .withRunningCalls(runningCalls)
                        .withRegion(region)
                        .withFunderId(funderId)
                        .withApplicantsScope(applicantsScope)
                        .withStatus(status)
                        .build(),
                new Document("_id", 1)
        ).list();
        return CallMongoEntityMapper.INSTANCE.toDomain(result, mongoCrossReferenceResolver);
    }


  @Override
  @Deprecated
  public Optional<CallDTO> findDto(RisId risId, EPublicationStatus status) {
    CallMongoEntity call = CallMongoEntity.find(
        new CallMongoEntityQueryBuilder()
            .withRisId(risId)
            .withStatus(status)
            .build()
    ).firstResult();

    if (call == null) {
      return Optional.empty();
    }
    return Optional.of(CallMongoEntityMapper.INSTANCE.toDTO(call, mongoCrossReferenceResolver));
  }

    @Override
    @Deprecated
    public List<CallDTO> findDto(
            ECallType callType,
            ETargetGroup targetGroup,
            Boolean runningCalls,
            EAustrianState region,
            FunderId funderId,
            ERegionalScope applicantsScope,
            EPublicationStatus status) {

        List<CallMongoEntity> result = CallMongoEntity.find(
                new CallMongoEntityQueryBuilder()
                        .withCallType(callType)
                        .withTargetGroup(targetGroup)
                        .withRunningCalls(runningCalls)
                        .withRegion(region)
                        .withFunderId(funderId)
                        .withApplicantsScope(applicantsScope)
                        .withStatus(status)
                        .build(),
                new Document("_id", 1)
        ).list();
        return CallMongoEntityMapper.INSTANCE.toDTOs(result, mongoCrossReferenceResolver);
    }
}
