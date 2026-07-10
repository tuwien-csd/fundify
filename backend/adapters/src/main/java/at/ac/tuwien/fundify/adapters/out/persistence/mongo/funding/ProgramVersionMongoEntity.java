package at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding;

import at.ac.tuwien.fundify.domain.common.DateRange;
import at.ac.tuwien.fundify.domain.common.TranslatedText;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EUpdateSource;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.bson.types.ObjectId;

@MongoEntity(database = "refop", collection = "program_versions")
public class ProgramVersionMongoEntity extends PanacheMongoEntity {

    public ObjectId programId;
    public LocalDateTime versionedAt;
    public EUpdateSource updateSource;
    public List<TranslatedText> description;
    public DateRange duration;
}
