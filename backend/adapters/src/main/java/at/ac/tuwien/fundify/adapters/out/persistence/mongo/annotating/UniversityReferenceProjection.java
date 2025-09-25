package at.ac.tuwien.fundify.adapters.out.persistence.mongo.annotating;

import io.quarkus.mongodb.panache.common.ProjectionFor;
import org.bson.types.ObjectId;

@ProjectionFor(UniversityMongoEntity.class)
public record UniversityReferenceProjection(
        ObjectId _id,
        String risId,
        String emailDomain,
        String acronym
) {}