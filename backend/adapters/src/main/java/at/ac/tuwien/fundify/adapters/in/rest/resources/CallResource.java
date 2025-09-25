package at.ac.tuwien.fundify.adapters.in.rest.resources;

import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.ADD_ENTITY;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.DELETE_ENTITY_BY_ID_REPLACE_PARAMTER;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.ENTITY_BY_ID_REPLACE_PARAMETER;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.ENTITY_LIST;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.ENTITY_UPDATE_SUBSCRIPTIONS;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.PATH_PARAM_ID;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.QUERY_PARAM_STATUS;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.UPDATE_ENTITY;

import at.ac.tuwien.fundify.adapters.in.rest.dto.CallCreateWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.CallUpdateWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.ESubscriptionStatusWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.CallWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.exceptions.CallExceptionMapper;
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
@Path("/api/call")
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
  @Path(ADD_ENTITY)
  @RolesAllowed({UserRole.Names.FUNDER, UserRole.Names.ADMIN, UserRole.Names.ANNOTATOR})
  public CallWebModel add(CallCreateWebModel callCreateWebModel)
      throws FundifyException {
      CallId callId = callUseCase.addCall(CallWebModelMapper.INSTANCE.toDomain(callCreateWebModel));
      if (callId == null) {
        return null;
      }
      String userId = userService.getCurrentUserId();
      return CallWebModelMapper.INSTANCE.fromDomain(callUseCase.getCall(callId), userId);
  }

  @PUT
  @Path(UPDATE_ENTITY)
  @RolesAllowed({UserRole.Names.FUNDER, UserRole.Names.ADMIN, UserRole.Names.ANNOTATOR})
  public CallWebModel update(CallUpdateWebModel callUpdateWebModel) throws FundifyException {
    String userId = userService.getCurrentUserId();
    CallId callId = callUseCase.updateCall(CallWebModelMapper.INSTANCE.toDomain(callUpdateWebModel));
      return CallWebModelMapper.INSTANCE.fromDomain(callUseCase.getCall(callId), userId);
  }

  @DELETE
  @Path((DELETE_ENTITY_BY_ID_REPLACE_PARAMTER))
  @RolesAllowed({UserRole.Names.FUNDER, UserRole.Names.ADMIN, UserRole.Names.ANNOTATOR})
  public void delete(@PathParam(PATH_PARAM_ID) String callId)
      throws FundifyException {
    callUseCase.deleteCall(new CallId(callId));
  }

  @GET
  @Path(ENTITY_LIST)
  public List<CallWebModel> list(@QueryParam(QUERY_PARAM_STATUS) EPublicationStatus status) {
    String userId = userService.getCurrentUserId();
    return CallWebModelMapper.INSTANCE.fromDomain(
          callAccessor.getByStatus(EPublicationStatus.defaultToPublished(status)), userId);
  }

  @GET
  public List<CallWebModel> getAll() {
    String userId = userService.getCurrentUserId();
      return CallWebModelMapper.INSTANCE.fromDomain(
          callAccessor.getAll(), userId
    );
  }

  @GET
  @Path(ENTITY_BY_ID_REPLACE_PARAMETER)
  public CallWebModel getById(@PathParam(PATH_PARAM_ID) String callId)
      throws EntityNotFoundException {
      String userId = userService.getCurrentUserId();
      return CallWebModelMapper.INSTANCE.fromDomain(callAccessor.getById(new CallId(callId)), userId);
  }

  @PATCH
  @Path(ENTITY_UPDATE_SUBSCRIPTIONS)
  @RolesAllowed(UserRole.Names.ANNOTATOR)
  public CallWebModel setSubscriptionForUser(@PathParam(PATH_PARAM_ID) String callId, ESubscriptionStatusWebModel newSubscriptionStatus)
      throws FundifyException {
      FundifyUser currentUser = userService.getCurrentUser();
      return CallWebModelMapper.INSTANCE.fromDomain(callUseCase.setSubscriptionForCurrentUser(new CallId(callId),
          CallWebModelMapper.INSTANCE.fromDto(newSubscriptionStatus), currentUser), currentUser.id());
  }
}