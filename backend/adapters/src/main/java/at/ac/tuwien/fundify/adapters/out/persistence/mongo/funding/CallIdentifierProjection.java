package at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding;

import at.ac.tuwien.fundify.domain.funding.vo.Identifier;
import io.quarkus.mongodb.panache.common.ProjectionFor;
import org.bson.types.ObjectId;

import java.util.List;

@ProjectionFor(CallMongoEntity.class)
public record CallIdentifierProjection (
        ObjectId _id,
        List<Identifier> identifiers
) {}