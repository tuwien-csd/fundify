package at.ac.tuwien.fundify.adapters.in.rest.resources;

import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.ENTITY_VERSIONS;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.PATH_PARAM_ID;

import at.ac.tuwien.fundify.adapters.in.rest.dto.ProgramVersionWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.ProgramWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.mapper.ProgramVersionWebModelMapper;
import at.ac.tuwien.fundify.adapters.in.rest.dto.ValidationGroups;
import at.ac.tuwien.fundify.adapters.in.rest.mapper.ProgramWebModelMapper;
import at.ac.tuwien.fundify.application.port.in.programs.ProgramAccessor;
import at.ac.tuwien.fundify.application.port.in.programs.ProgramUseCase;
import at.ac.tuwien.fundify.domain.common.ProgramId;
import at.ac.tuwien.fundify.domain.common.UserRole;
import at.ac.tuwien.fundify.domain.common.exceptions.EntityNotFoundException;
import at.ac.tuwien.fundify.domain.common.exceptions.FundifyException;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.groups.ConvertGroup;
import jakarta.ws.rs.BadRequestException;
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
import jakarta.validation.Valid;

@Path("/api/programs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
@JBossLog
@RequiredArgsConstructor
public class ProgramResource {

  private final ProgramUseCase programUseCase;
  private final ProgramAccessor programAccessor;

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed({UserRole.Names.FUNDER, UserRole.Names.ADMIN})
  public ProgramWebModel add(@Valid @ConvertGroup(to = ValidationGroups.Post.class) ProgramWebModel programWebModel) throws FundifyException {
    return ProgramWebModelMapper.INSTANCE.fromDomain(
        programUseCase.addProgram(ProgramWebModelMapper.INSTANCE.toDomain(programWebModel))
    );
  }

  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed({UserRole.Names.FUNDER, UserRole.Names.ADMIN})
  @Path("/{id}")
  public ProgramWebModel update(@PathParam(PATH_PARAM_ID) String programId, @Valid @ConvertGroup(to = ValidationGroups.Put.class) ProgramWebModel programWebModel) throws FundifyException {
    if (!programId.equals(programWebModel.id())) {
      throw new BadRequestException("Path ID does not match body ID");
    }
    return ProgramWebModelMapper.INSTANCE.fromDomain(
        programUseCase.updateProgram(ProgramWebModelMapper.INSTANCE.toDomain(programWebModel))
    );
  }

  @DELETE
  @Path("/{id}")
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
  @Path("/{id}")
  public ProgramWebModel getById(@PathParam(PATH_PARAM_ID) String programId)
      throws EntityNotFoundException {
    return ProgramWebModelMapper.INSTANCE.fromDomain(
        programAccessor.getById(new ProgramId(programId)));
  }

  @GET
  @Path(ENTITY_VERSIONS)
  public List<ProgramVersionWebModel> getVersions(@PathParam(PATH_PARAM_ID) String programId) {
    return ProgramVersionWebModelMapper.fromDomain(
        programAccessor.getVersionsByProgramId(new ProgramId(programId)));
  }
}
