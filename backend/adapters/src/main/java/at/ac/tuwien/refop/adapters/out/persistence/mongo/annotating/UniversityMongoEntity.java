package at.ac.tuwien.refop.adapters.out.persistence.mongo.annotating;

import at.ac.tuwien.refop.adapters.in.rest.dto.PostAddressWebModel;
import at.ac.tuwien.refop.domain.common.TranslatedText;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

import java.util.List;

@MongoEntity(database = "refop", collection = "universities")
public class UniversityMongoEntity extends PanacheMongoEntity {
    public String risId;
    public List<TranslatedText> name;
    public String acronym;
    public String emailDomain;
    public String website;
    public String submissionSystem;
    public String phone;
    public String crossRefDoi;
    public PostAddressWebModel postAddress;
}