package at.ac.tuwien.fundify.adapters.out.persistence.mongo.common;

import at.ac.tuwien.fundify.domain.annotating.UniversityReference;
import at.ac.tuwien.fundify.domain.funding.FunderReference;
import at.ac.tuwien.fundify.domain.funding.ProgramReference;
import org.bson.types.ObjectId;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CommonReferenceMapper {

    CommonReferenceMapper INSTANCE = Mappers.getMapper(CommonReferenceMapper.class);

    default UniversityReference resolveUniversityReference(ObjectId universityMongoEntityId, @Context MongoCrossReferenceResolver resolver) {
        return resolver.resolveUniversityReference(universityMongoEntityId);
    }

    default FunderReference resolveFunderReference(ObjectId funderId, @Context MongoCrossReferenceResolver resolver) {
        return resolver.resolveFunderReference(funderId);
    }

    default ProgramReference resolveProgramReference(ObjectId programId, @Context MongoCrossReferenceResolver resolver) {
        return resolver.resolveProgramReference(programId);
    }
}
