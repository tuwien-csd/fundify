package at.ac.tuwien.refop.adapters.in.rest.resources;

import static at.ac.tuwien.refop.adapters.in.rest.constants.FundingEntityMethodPath.ADD_ENTITY;
import static at.ac.tuwien.refop.adapters.in.rest.constants.FundingEntityMethodPath.DELETE_ENTITY_BY_ID_REPLACE_PARAMTER;
import static at.ac.tuwien.refop.adapters.in.rest.constants.FundingEntityMethodPath.ENTITY_BY_ID_REPLACE_PARAMETER;
import static at.ac.tuwien.refop.adapters.in.rest.constants.FundingEntityMethodPath.ENTITY_LIST;
import static at.ac.tuwien.refop.adapters.in.rest.constants.FundingEntityMethodPath.PATH_PARAM_ID;
import static at.ac.tuwien.refop.adapters.in.rest.constants.FundingEntityMethodPath.QUERY_PARAM_STATUS;
import static at.ac.tuwien.refop.adapters.in.rest.constants.FundingEntityMethodPath.UPDATE_ENTITY;

import at.ac.tuwien.refop.adapters.in.rest.dto.ProgramWebModel;
import at.ac.tuwien.refop.adapters.in.rest.mapper.ProgramWebModelMapper;
import at.ac.tuwien.refop.application.port.in.programs.ProgramAccessor;
import at.ac.tuwien.refop.application.port.in.programs.ProgramUseCase;
import at.ac.tuwien.refop.domain.common.EPublicationStatus;
import at.ac.tuwien.refop.domain.common.ProgramId;
import at.ac.tuwien.refop.domain.common.UserRole;
import at.ac.tuwien.refop.domain.common.exceptions.EntityNotFoundException;
import at.ac.tuwien.refop.domain.common.exceptions.FundifyException;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
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

@Path("/api/program")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
@JBossLog
@RequiredArgsConstructor
public class ProgramResource {

  private final ProgramUseCase programUseCase;
  private final ProgramAccessor programAccessor;

  @POST
  @Path(ADD_ENTITY)
  @RolesAllowed({UserRole.Names.FUNDER, UserRole.Names.ADMIN})
  public ProgramWebModel add(ProgramWebModel programWebModel) throws FundifyException {
    return ProgramWebModelMapper.INSTANCE.fromDomain(
        programUseCase.addProgram(ProgramWebModelMapper.INSTANCE.toDomain(programWebModel))
    );
  }

  @PUT
  @Path(UPDATE_ENTITY)
  @RolesAllowed({UserRole.Names.FUNDER, UserRole.Names.ADMIN})
  public ProgramWebModel update(ProgramWebModel programWebModel) throws FundifyException {
    return ProgramWebModelMapper.INSTANCE.fromDomain(
        programUseCase.updateProgram(ProgramWebModelMapper.INSTANCE.toDomain(programWebModel))
    );
  }

  @DELETE
  @Path((DELETE_ENTITY_BY_ID_REPLACE_PARAMTER))
  @RolesAllowed({UserRole.Names.FUNDER, UserRole.Names.ADMIN})
  public void delete(@PathParam(PATH_PARAM_ID) String programId) throws FundifyException {
    programUseCase.deleteProgram(new ProgramId(programId));
  }

  @GET
  public List<ProgramWebModel> getAll() {
    return ProgramWebModelMapper.INSTANCE.fromDomain(
        programAccessor.getAll()
    );
  }

  @GET
  @Path(ENTITY_LIST)
  public List<ProgramWebModel> list(@QueryParam(QUERY_PARAM_STATUS) EPublicationStatus status) {
    return ProgramWebModelMapper.INSTANCE.fromDomain(
        programAccessor.getByStatus(EPublicationStatus.defaultToPublished(status)));
  }

  @GET
  @Path(ENTITY_BY_ID_REPLACE_PARAMETER)
  public ProgramWebModel getById(@PathParam(PATH_PARAM_ID) String programId)
      throws EntityNotFoundException {
    return ProgramWebModelMapper.INSTANCE.fromDomain(
        programAccessor.getById(new ProgramId(programId)));
  }
}
