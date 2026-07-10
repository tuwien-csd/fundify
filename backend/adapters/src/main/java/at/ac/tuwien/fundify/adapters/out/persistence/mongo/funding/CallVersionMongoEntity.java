package at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding;

import at.ac.tuwien.fundify.domain.common.TranslatedText;
import at.ac.tuwien.fundify.domain.funding.vo.CallStage;
import at.ac.tuwien.fundify.domain.funding.vo.MonetaryNumber;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EUpdateSource;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.bson.types.ObjectId;

@MongoEntity(database = "refop", collection = "call_versions")
public class CallVersionMongoEntity extends PanacheMongoEntity {

    public ObjectId callId;
    public LocalDateTime versionedAt;
    public EUpdateSource updateSource;
    public List<TranslatedText> name;
    public List<TranslatedText> description;
    public List<TranslatedText> eligibleApplicants;
    public List<CallStage> callStages;
    public MonetaryNumber callVolumeAmount;
    public List<String> website;
}
