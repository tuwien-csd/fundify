package at.ac.tuwien.refop.adapters.out.persistence.mongo.funding;

import at.ac.tuwien.refop.domain.funding.vo.Identifier;
import at.ac.tuwien.refop.domain.common.PostAddress;
import at.ac.tuwien.refop.domain.common.TranslatedText;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

import java.util.List;

@MongoEntity(database="refop", collection = "funders")
public class FunderMongoEntity extends PanacheMongoEntity {

    // mandatory fields
    public List<TranslatedText> name;
    public String risId; // only unique for Ris Synergy funders (e.g. not for External Companies)
    public String website;
    public String emailDomain;
    // optional fields
    public String acronym;
    public List<Identifier> identifiers;
    public String submissionSystem;
    public String phone;

    public String crossRefDoi;
    public PostAddress postAddress;
    public Boolean externallyAdministered;
}