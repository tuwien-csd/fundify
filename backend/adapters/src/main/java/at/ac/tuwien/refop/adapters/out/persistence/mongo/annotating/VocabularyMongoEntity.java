package at.ac.tuwien.refop.adapters.out.persistence.mongo.annotating;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.types.ObjectId;
import at.ac.tuwien.refop.domain.common.EVocabularyType;

import java.util.Set;

@MongoEntity(database = "refop", collection = "vocabularies")
public class VocabularyMongoEntity extends PanacheMongoEntity {
    public ObjectId universityId;
    public EVocabularyType type;
    public Set<String> entries;
}