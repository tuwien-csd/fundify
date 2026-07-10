package at.ac.tuwien.fundify.adapters.out.persistence.mongo.registration;

import io.quarkus.mongodb.panache.PanacheMongoEntityBase;
import io.quarkus.mongodb.panache.common.MongoEntity;
import java.time.Instant;
import org.bson.codecs.pojo.annotations.BsonId;

@MongoEntity(database = "refop", collection = "registrationRequests")
public class RegistrationRequestMongoEntity extends PanacheMongoEntityBase {

  @BsonId
  public String id;

  public String name;

  public String email;

  public String kindOfInstitution;

  public String message;

  public Instant createdAt;
}
