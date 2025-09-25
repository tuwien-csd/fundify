package at.ac.tuwien.refop.adapters.out.persistence.mongo.funding;

import at.ac.tuwien.refop.domain.common.*;
import at.ac.tuwien.refop.domain.funding.vo.Identifier;
import at.ac.tuwien.refop.domain.funding.vo.StandardizedSubject;
import at.ac.tuwien.refop.domain.funding.vo.enums.*;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.List;

@MongoEntity(database="refop", collection = "programs")
public class ProgramMongoEntity extends PanacheMongoEntity {
    //meta data fields
    public EPublicationStatus status;
    public EEntryOrigin entryOrigin;
    public LocalDateTime registrationDate;
    public LocalDateTime lastSync;
    // mandatory fields
    public String risId;
    public List<TranslatedText> name;
    public List<ETargetGroup> targetGroups;
    public List<StandardizedSubject> subjects;
    public List<TranslatedText> description;
    public List<EFundingCharacteristic> characteristics;
    public List<String> website;
    public EFundingScheme fundingScheme;
    public ELegalType legalType;
    public ObjectId funderId;
    // optional fields
    public String acronym;
    public List<Identifier> identifiers;
    public List<List<TranslatedText>> programTracks;
    public List<ECareerStage> careerStages;
    public DateRange duration;
}