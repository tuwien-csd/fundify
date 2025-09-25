package at.ac.tuwien.refop.adapters.out.persistence.mongo.common;

import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MongoEntityIdMapper {

    MongoEntityIdMapper INSTANCE = Mappers.getMapper(MongoEntityIdMapper.class);

    default String toString(ObjectId id) {
        return id != null ? id.toHexString() : null;
    }

    default ObjectId stringToObjectId(String id) {
        return id != null ? ObjectIdUtils.toObjectId(id) : null;
    }

}
