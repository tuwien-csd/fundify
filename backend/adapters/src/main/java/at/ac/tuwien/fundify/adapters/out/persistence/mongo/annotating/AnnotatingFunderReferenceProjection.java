package at.ac.tuwien.fundify.adapters.out.persistence.mongo.annotating;

import at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding.FunderMongoEntity;
import at.ac.tuwien.fundify.domain.common.TranslatedText;
import io.quarkus.mongodb.panache.common.ProjectionFor;
import org.bson.types.ObjectId;

import java.util.List;

@ProjectionFor(FunderMongoEntity.class)
public record AnnotatingFunderReferenceProjection (
    ObjectId _id,
    String acronym,
    List<TranslatedText> name
) {}