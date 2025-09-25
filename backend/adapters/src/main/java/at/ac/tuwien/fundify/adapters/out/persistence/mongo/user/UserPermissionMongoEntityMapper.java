package at.ac.tuwien.fundify.adapters.out.persistence.mongo.user;

import at.ac.tuwien.fundify.domain.common.UserPermissionHolder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserPermissionMongoEntityMapper {
    UserPermissionMongoEntityMapper INSTANCE = Mappers.getMapper(UserPermissionMongoEntityMapper.class);

    UserPermissionHolder toDomain(UserPermissionMongoEntity userPermission);

    UserPermissionMongoEntity fromDomain(UserPermissionHolder userPermission);
}
