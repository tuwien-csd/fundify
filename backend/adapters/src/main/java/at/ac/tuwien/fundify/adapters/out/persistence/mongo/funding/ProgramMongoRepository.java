package at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding;

import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.MongoCrossReferenceResolver;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.ObjectIdUtils;
import at.ac.tuwien.fundify.application.port.out.persistence.ProgramRepository;
import at.ac.tuwien.fundify.domain.common.ProgramId;
import at.ac.tuwien.fundify.domain.funding.Program;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;

@ApplicationScoped
public class ProgramMongoRepository implements ProgramRepository {

    private final MongoCrossReferenceResolver mongoCrossReferenceResolver;

    @Inject
    ProgramMongoRepository(MongoCrossReferenceResolver mongoCrossReferenceResolver) {
        this.mongoCrossReferenceResolver = mongoCrossReferenceResolver;
    }

    @Override
    public Program persist(Program program) {
        ProgramMongoEntity entity = ProgramMongoEntityMapper.INSTANCE.fromDomain(program);
        entity.persist();

        return ProgramMongoEntityMapper.INSTANCE.toDomain(entity, mongoCrossReferenceResolver);
    }

    @Override
    public Optional<Program> update(Program program) {
        ProgramMongoEntity entity = ProgramMongoEntityMapper.INSTANCE.fromDomain(program);
        entity.update();

        return Optional.ofNullable(ProgramMongoEntityMapper.INSTANCE.toDomain(entity, mongoCrossReferenceResolver));
    }

    @Override
    public boolean delete(ProgramId id) {
        return ProgramMongoEntity.deleteById(ObjectIdUtils.toObjectId(id.value()));
    }

    @Override
    public Optional<Program> findById(ProgramId id) {
        ProgramMongoEntity program = ProgramMongoEntity.findById(ObjectIdUtils.toObjectId(id.value()));
        if (program == null) {
            return Optional.empty();
        }
        return Optional.of(ProgramMongoEntityMapper.INSTANCE.toDomain(program, mongoCrossReferenceResolver));
    }
}
