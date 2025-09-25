package at.ac.tuwien.fundify.adapters.out.persistence.mongo.annotating;

import at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding.CallMongoEntity;
import at.ac.tuwien.fundify.domain.annotating.CallStagePreview;
import at.ac.tuwien.fundify.domain.common.EFundingCharacteristic;
import at.ac.tuwien.fundify.domain.common.ETargetGroup;
import at.ac.tuwien.fundify.domain.common.TranslatedText;
import io.quarkus.mongodb.panache.common.ProjectionFor;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.List;

@ProjectionFor(CallMongoEntity.class)
public record CallPreviewProjection(
        ObjectId _id,
        LocalDateTime registrationDate,
        LocalDateTime lastSync,
        List<TranslatedText> name,
        ObjectId funderId,
        ObjectId partOfId,
        List<ETargetGroup> targetGroups,
        List<EFundingCharacteristic> characteristics,
        List<CallStagePreview> callStages
) {}