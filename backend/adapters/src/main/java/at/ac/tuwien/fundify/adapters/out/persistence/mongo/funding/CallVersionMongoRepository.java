package at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding;

import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.ObjectIdUtils;
import at.ac.tuwien.fundify.application.port.out.persistence.CallVersionRepository;
import at.ac.tuwien.fundify.domain.common.CallId;
import at.ac.tuwien.fundify.domain.funding.CallVersion;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import org.bson.types.ObjectId;

@ApplicationScoped
public class CallVersionMongoRepository implements CallVersionRepository {

    @Override
    public void persist(CallVersion callVersion) {
        CallVersionMongoEntity entity = new CallVersionMongoEntity();
        entity.callId = ObjectIdUtils.toObjectId(callVersion.getCallId().value());
        entity.versionedAt = callVersion.getVersionedAt();
        entity.updateSource = callVersion.getUpdateSource();
        entity.name = callVersion.getName();
        entity.description = callVersion.getDescription();
        entity.eligibleApplicants = callVersion.getEligibleApplicants();
        entity.callStages = callVersion.getCallStages();
        entity.callVolumeAmount = callVersion.getCallVolumeAmount();
        entity.website = callVersion.getWebsite();
        entity.persist();
    }

    @Override
    public List<CallVersion> findByCallId(CallId callId) {
        ObjectId objectId = ObjectIdUtils.toObjectId(callId.value());
        return CallVersionMongoEntity.<CallVersionMongoEntity>list("callId", objectId)
            .stream()
            .map(this::toDomain)
            .toList();
    }

    @Override
    public void deleteByCallId(CallId callId) {
        ObjectId objectId = ObjectIdUtils.toObjectId(callId.value());
        CallVersionMongoEntity.delete("callId", objectId);
    }

    private CallVersion toDomain(CallVersionMongoEntity entity) {
        return new CallVersion(
            entity.id != null ? entity.id.toHexString() : null,
            new CallId(entity.callId.toHexString()),
            entity.versionedAt,
            entity.updateSource,
            entity.name,
            entity.description,
            entity.eligibleApplicants,
            entity.callStages,
            entity.callVolumeAmount,
            entity.website
        );
    }
}
