package at.ac.tuwien.fundify.adapters.in.rest.mapper;

import at.ac.tuwien.fundify.adapters.in.rest.dto.UserPermissionsUpdateWebModel;
import at.ac.tuwien.fundify.domain.common.UserPermissionHolder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserPermissionsWebModelMapper {

    UserPermissionsWebModelMapper INSTANCE = Mappers.getMapper(UserPermissionsWebModelMapper.class);

    UserPermissionHolder toDomain(UserPermissionsUpdateWebModel source, String userId);
}



