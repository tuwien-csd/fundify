package at.ac.tuwien.refop.adapters.in.rest.resources;

import at.ac.tuwien.refop.adapters.in.rest.dto.UserPermissionsUpdateWebModel;
import at.ac.tuwien.refop.adapters.in.rest.mapper.UserPermissionsWebModelMapper;
import at.ac.tuwien.refop.application.port.common.UserService;
import at.ac.tuwien.refop.application.port.in.users.UserPermissionManagementUseCase;
import at.ac.tuwien.refop.domain.common.UserPermissionHolder;
import at.ac.tuwien.refop.domain.common.UserRole;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;


@JBossLog
@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
@RequiredArgsConstructor
public class UserResource {

    private final UserService userService;
    private final UserPermissionManagementUseCase userPermissionManagementUseCase;

    @GET
    @Path("/me")
    public UserPermissionHolder getUserInfo() {
      return userService.getCurrentUserPermissionHolder();
    }

    @PUT
    @Path("/{id}/permissions")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed(UserRole.Names.ADMIN)
    public UserPermissionHolder updateUser(@PathParam("id") String id, UserPermissionsUpdateWebModel updateInput) {
      var updateObject = UserPermissionsWebModelMapper.INSTANCE.toDomain(updateInput, id);
      return userPermissionManagementUseCase.updatePermissions(updateObject);
    }

}