package at.ac.tuwien.fundify.adapters.out.persistence.mongo.user;

import at.ac.tuwien.fundify.domain.common.UserRole;
import io.quarkus.mongodb.panache.PanacheMongoEntityBase;
import io.quarkus.mongodb.panache.common.MongoEntity;
import java.util.List;
import org.bson.codecs.pojo.annotations.BsonId;

@MongoEntity(database = "refop", collection = "userPermissions")
public class UserPermissionMongoEntity extends PanacheMongoEntityBase {

  @BsonId
  public String userId;

  public List<UserRole> roles;

  public String affiliationId;
}
