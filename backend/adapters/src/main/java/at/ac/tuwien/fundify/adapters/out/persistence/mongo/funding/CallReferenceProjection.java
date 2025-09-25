package at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding;

import at.ac.tuwien.fundify.domain.common.TranslatedText;
import at.ac.tuwien.fundify.domain.funding.vo.Identifier;
import io.quarkus.mongodb.panache.common.ProjectionFor;
import org.bson.types.ObjectId;

import java.util.List;

@ProjectionFor(CallMongoEntity.class)
public record CallReferenceProjection(
    ObjectId _id,
    String risId,
    List<TranslatedText> name,
    String acronym,
    List<Identifier> identifiers,
    ObjectId funderId,
    ObjectId partOfId
) {}