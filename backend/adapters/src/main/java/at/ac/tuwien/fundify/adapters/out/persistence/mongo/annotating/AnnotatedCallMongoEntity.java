package at.ac.tuwien.fundify.adapters.out.persistence.mongo.annotating;

import at.ac.tuwien.fundify.domain.annotating.CallAnnotation;
import at.ac.tuwien.fundify.domain.common.EPublicationStatus;
import at.ac.tuwien.fundify.domain.common.FundifyUser;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.types.ObjectId;
import java.time.LocalDateTime;

@MongoEntity(database = "refop", collection = "annotatedCalls")
public class AnnotatedCallMongoEntity extends PanacheMongoEntity {
    public EPublicationStatus status;
    public ObjectId callId;
    public CallAnnotation annotation;
    public ObjectId universityId;
    public LocalDateTime lastUpdatedAt;
    public FundifyUser lastUpdatedBy;
}