package at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding;

import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.ObjectIdUtils;
import at.ac.tuwien.fundify.application.port.out.persistence.ProgramVersionRepository;
import at.ac.tuwien.fundify.domain.common.ProgramId;
import at.ac.tuwien.fundify.domain.funding.ProgramVersion;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import org.bson.types.ObjectId;

@ApplicationScoped
public class ProgramVersionMongoRepository implements ProgramVersionRepository {

    @Override
    public void persist(ProgramVersion programVersion) {
        ProgramVersionMongoEntity entity = new ProgramVersionMongoEntity();
        entity.programId = ObjectIdUtils.toObjectId(programVersion.getProgramId().value());
        entity.versionedAt = programVersion.getVersionedAt();
        entity.updateSource = programVersion.getUpdateSource();
        entity.description = programVersion.getDescription();
        entity.duration = programVersion.getDuration();
        entity.persist();
    }

    @Override
    public List<ProgramVersion> findByProgramId(ProgramId programId) {
        ObjectId objectId = ObjectIdUtils.toObjectId(programId.value());
        return ProgramVersionMongoEntity.<ProgramVersionMongoEntity>list("programId", objectId)
            .stream()
            .map(this::toDomain)
            .toList();
    }

    private ProgramVersion toDomain(ProgramVersionMongoEntity entity) {
        return new ProgramVersion(
            entity.id != null ? entity.id.toHexString() : null,
            new ProgramId(entity.programId.toHexString()),
            entity.versionedAt,
            entity.updateSource,
            entity.description,
            entity.duration
        );
    }
}
