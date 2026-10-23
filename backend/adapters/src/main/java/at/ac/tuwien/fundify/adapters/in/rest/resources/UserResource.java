package at.ac.tuwien.fundify.adapters.in.rest.resources;

import at.ac.tuwien.fundify.adapters.in.rest.dto.UserCreationWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.UserPermissionsUpdateWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.mapper.UserPermissionsWebModelMapper;
import at.ac.tuwien.fundify.application.port.common.UserService;
import at.ac.tuwien.fundify.application.port.in.users.KeycloakUserLookupUseCase;
import at.ac.tuwien.fundify.application.port.in.users.UserPermissionManagementUseCase;
import at.ac.tuwien.fundify.domain.common.KeycloakUser;
import at.ac.tuwien.fundify.domain.common.UserPermissionHolder;
import at.ac.tuwien.fundify.domain.common.UserRole;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
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
    private final KeycloakUserLookupUseCase keycloakUserLookupUseCase;

    @GET
    @RolesAllowed(UserRole.Names.ADMIN)
    public List<KeycloakUser> getAllUsers() {
      return keycloakUserLookupUseCase.getAllUsers();
    }

    @GET
    @Path("/permissions")
    @RolesAllowed(UserRole.Names.ADMIN)
    public List<UserPermissionHolder> getAllPermissions() {
      return userPermissionManagementUseCase.getAllPermissions();
    }

    @GET
    @Path("/me")
    public UserPermissionHolder getUserInfo() {
      return userService.getCurrentUserPermissionHolder();
    }

    @PUT
    @Path("/{id}/permissions")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed(UserRole.Names.ADMIN)
    public UserPermissionHolder updateUser(@PathParam("id") String id, @Valid UserPermissionsUpdateWebModel updateInput) {
      var updateObject = UserPermissionsWebModelMapper.INSTANCE.toDomain(updateInput, id);
      return userPermissionManagementUseCase.updatePermissions(updateObject);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed(UserRole.Names.ADMIN)
    public UserPermissionHolder createUser(@Valid UserCreationWebModel createInput) {
      var provisioning = UserPermissionsWebModelMapper.INSTANCE.toDomain(createInput);
      return userPermissionManagementUseCase.createUserWithPermissions(provisioning);
    }

    @DELETE
    @Path("/{id}/permissions")
    @RolesAllowed(UserRole.Names.ADMIN)
    public void deletePermissions(@PathParam("id") String id) {
      userPermissionManagementUseCase.deletePermissions(id);
    }

}