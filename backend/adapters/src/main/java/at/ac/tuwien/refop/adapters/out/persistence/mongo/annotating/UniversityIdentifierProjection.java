package at.ac.tuwien.refop.adapters.out.persistence.mongo.annotating;

import io.quarkus.mongodb.panache.common.ProjectionFor;
import org.bson.types.ObjectId;

@ProjectionFor(UniversityMongoEntity.class)
public record UniversityIdentifierProjection (
    ObjectId _id,
    String risId
){ }