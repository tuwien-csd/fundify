package at.ac.tuwien.refop.adapters.in.rest.mapper;

import at.ac.tuwien.refop.adapters.in.rest.dto.UserPermissionsUpdateWebModel;
import at.ac.tuwien.refop.domain.common.UserPermissionHolder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserPermissionsWebModelMapper {

    UserPermissionsWebModelMapper INSTANCE = Mappers.getMapper(UserPermissionsWebModelMapper.class);

    UserPermissionHolder toDomain(UserPermissionsUpdateWebModel source, String userId);
}



