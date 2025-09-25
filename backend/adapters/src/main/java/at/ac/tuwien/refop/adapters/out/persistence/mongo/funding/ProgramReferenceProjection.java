package at.ac.tuwien.refop.adapters.out.persistence.mongo.funding;

import at.ac.tuwien.refop.domain.common.TranslatedText;
import at.ac.tuwien.refop.domain.funding.vo.Identifier;
import io.quarkus.mongodb.panache.common.ProjectionFor;
import org.bson.types.ObjectId;

import java.util.List;

@ProjectionFor(ProgramMongoEntity.class)
public record ProgramReferenceProjection(
        ObjectId _id,
        String risId,
        String acronym,
        List<TranslatedText> name,
        List<String> website,
        List<Identifier> identifiers,
        ObjectId funderId
) {}