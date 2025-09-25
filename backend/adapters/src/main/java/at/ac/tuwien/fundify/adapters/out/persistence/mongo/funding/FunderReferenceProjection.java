package at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding;

import at.ac.tuwien.fundify.domain.common.TranslatedText;
import at.ac.tuwien.fundify.domain.funding.vo.Identifier;
import io.quarkus.mongodb.panache.common.ProjectionFor;
import org.bson.types.ObjectId;

import java.util.List;

@ProjectionFor(FunderMongoEntity.class)
public record FunderReferenceProjection(
        ObjectId _id,
        String risId,
        String emailDomain,
        List<TranslatedText> name,
        String website,
        String acronym,
        List<Identifier> identifiers
) {}