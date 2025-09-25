package at.ac.tuwien.fundify.adapters.out.persistence.mongo.common;

import lombok.experimental.UtilityClass;
import org.bson.types.ObjectId;

@UtilityClass
public class ObjectIdUtils {

  /**
   * Converts a String to an ObjectId.
   * @param id
   * @return Returns null if the String is null, empty, or not a valid ObjectId.
   */
  public static ObjectId toObjectId(String id) {
    if (id == null || id.isEmpty()) {
      return null;
    }
    try {
      return new ObjectId(id);
    } catch (IllegalArgumentException e) {
      // Invalid ObjectId format
      return null;
    }
  }

}
