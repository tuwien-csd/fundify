package at.ac.tuwien.fundify.adapters.in.rest.resources;

import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.BY_ID;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.ENTITY_UPDATE_SUBSCRIPTIONS;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.ENTITY_VERSIONS;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.PATH_PARAM_ID;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.QUERY_PARAM_STATUS;

import at.ac.tuwien.fundify.adapters.in.rest.dto.CallCreateWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.CallUpdateWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.CallVersionWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.ESubscriptionStatusWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.CallWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.exceptions.CallExceptionMapper;
import at.ac.tuwien.fundify.adapters.in.rest.mapper.CallVersionWebModelMapper;
import at.ac.tuwien.fundify.adapters.in.rest.mapper.CallWebModelMapper;
import at.ac.tuwien.fundify.application.port.common.UserService;
import at.ac.tuwien.fundify.application.port.in.calls.CallAccessor;
import at.ac.tuwien.fundify.application.port.in.calls.CallUseCase;
import at.ac.tuwien.fundify.domain.common.CallId;
import at.ac.tuwien.fundify.domain.common.EPublicationStatus;
import at.ac.tuwien.fundify.domain.common.FundifyUser;
import at.ac.tuwien.fundify.domain.common.UserRole;
import at.ac.tuwien.fundify.domain.common.exceptions.EntityNotFoundException;
import at.ac.tuwien.fundify.domain.common.exceptions.FundifyException;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;

/**
 * REST controller for managing calls.
 * Provides endpoints for adding, updating, deleting, and retrieving calls.
 * Error handling is done using {@link CallExceptionMapper}.
 */
@Path("/api/calls")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@JBossLog
@Authenticated
@ApplicationScoped
@RequiredArgsConstructor
public class CallResource {

  private final CallUseCase callUseCase;
  private final CallAccessor callAccessor;
  private final UserService userService;

  @POST
  @RolesAllowed({UserRole.Names.FUNDER, UserRole.Names.ADMIN, UserRole.Names.ANNOTATOR})
  public CallWebModel add(@Valid CallCreateWebModel callCreateWebModel)
      throws FundifyException {
    CallId callId = callUseCase.addCall(CallWebModelMapper.INSTANCE.toDomain(callCreateWebModel));
    String userId = userService.getCurrentUserId();
    return CallWebModelMapper.INSTANCE.fromDomain(callUseCase.getCall(callId), userId);
  }

  @PUT
  @Path(BY_ID)
  @RolesAllowed({UserRole.Names.FUNDER, UserRole.Names.ADMIN, UserRole.Names.ANNOTATOR})
  public CallWebModel update(@PathParam(PATH_PARAM_ID) String pathId,
                             @Valid CallUpdateWebModel callUpdateWebModel) throws FundifyException {
    if (!pathId.equals(callUpdateWebModel.id())) {
      throw new BadRequestException("Path ID does not match body ID");
    }
    String userId = userService.getCurrentUserId();
    CallId callId = callUseCase.updateCall(CallWebModelMapper.INSTANCE.toDomain(callUpdateWebModel));
    return CallWebModelMapper.INSTANCE.fromDomain(callUseCase.getCall(callId), userId);
  }

  @DELETE
  @Path(BY_ID)
  @RolesAllowed({UserRole.Names.FUNDER, UserRole.Names.ADMIN, UserRole.Names.ANNOTATOR})
  public void delete(@PathParam(PATH_PARAM_ID) String callId)
      throws FundifyException {
    callUseCase.deleteCall(new CallId(callId));
  }

  @GET
  public List<CallWebModel> list(@QueryParam(QUERY_PARAM_STATUS) EPublicationStatus status) {
    String userId = userService.getCurrentUserId();
    if (status == null) {
      return CallWebModelMapper.INSTANCE.fromDomain(callAccessor.getAll(), userId);
    }
    return CallWebModelMapper.INSTANCE.fromDomain(callAccessor.getByStatus(status), userId);
  }

  @GET
  @Path(BY_ID)
  public CallWebModel getById(@PathParam(PATH_PARAM_ID) String callId)
      throws EntityNotFoundException {
    String userId = userService.getCurrentUserId();
    return CallWebModelMapper.INSTANCE.fromDomain(callAccessor.getById(new CallId(callId)), userId);
  }

  @GET
  @Path(ENTITY_VERSIONS)
  public List<CallVersionWebModel> getVersions(@PathParam(PATH_PARAM_ID) String callId) {
    return CallVersionWebModelMapper.fromDomain(callAccessor.getVersionsByCallId(new CallId(callId)));
  }

  @PATCH
  @Path(ENTITY_UPDATE_SUBSCRIPTIONS)
  @RolesAllowed(UserRole.Names.ANNOTATOR)
  public CallWebModel setSubscriptionForUser(@PathParam(PATH_PARAM_ID) String callId,
      ESubscriptionStatusWebModel newSubscriptionStatus)
      throws FundifyException {
    FundifyUser currentUser = userService.getCurrentUser();
    return CallWebModelMapper.INSTANCE.fromDomain(callUseCase.setSubscriptionForCurrentUser(new CallId(callId),
        CallWebModelMapper.INSTANCE.fromDto(newSubscriptionStatus), currentUser), currentUser.id());
  }
}
