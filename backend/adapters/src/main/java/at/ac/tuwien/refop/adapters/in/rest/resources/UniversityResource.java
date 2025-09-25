package at.ac.tuwien.refop.adapters.in.rest.resources;

import static at.ac.tuwien.refop.adapters.in.rest.constants.FundingEntityMethodPath.ADD_ENTITY;
import static at.ac.tuwien.refop.adapters.in.rest.constants.FundingEntityMethodPath.DELETE_ENTITY_BY_ID_REPLACE_PARAMTER;
import static at.ac.tuwien.refop.adapters.in.rest.constants.FundingEntityMethodPath.ENTITY_BY_ID_REPLACE_PARAMETER;
import static at.ac.tuwien.refop.adapters.in.rest.constants.FundingEntityMethodPath.ENTITY_LIST;
import static at.ac.tuwien.refop.adapters.in.rest.constants.FundingEntityMethodPath.PATH_PARAM_ID;
import static at.ac.tuwien.refop.adapters.in.rest.constants.FundingEntityMethodPath.UPDATE_ENTITY;

import at.ac.tuwien.refop.adapters.in.rest.dto.UniversityCreateWebModel;
import at.ac.tuwien.refop.adapters.in.rest.dto.UniversityWebModel;
import at.ac.tuwien.refop.adapters.in.rest.mapper.UniversityWebModelMapper;
import at.ac.tuwien.refop.application.port.in.institutions.UniversityUseCase;
import at.ac.tuwien.refop.domain.annotating.UniversityId;
import at.ac.tuwien.refop.domain.common.UserRole;
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
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@Path("/api/university")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
@RequiredArgsConstructor
public class UniversityResource {

    private final UniversityUseCase universityUseCase;

    @POST
    @Path(ADD_ENTITY)
    @RolesAllowed({UserRole.Names.ADMIN,})
    public UniversityWebModel add(UniversityCreateWebModel universityCreateWebModel) {
      return UniversityWebModelMapper.INSTANCE.fromDomain(
          universityUseCase.addUniversity(
              UniversityWebModelMapper.INSTANCE.toDomain(universityCreateWebModel)
          )
      );
    }

    @PUT
    @Path(UPDATE_ENTITY)
    @RolesAllowed({UserRole.Names.ADMIN})
    public UniversityWebModel update(UniversityWebModel universityWebModel) throws FundifyException {
      return UniversityWebModelMapper.INSTANCE.fromDomain(
          universityUseCase.updateUniversity(UniversityWebModelMapper.INSTANCE.toDomain(universityWebModel))
      );
    }

    @DELETE
    @RolesAllowed(UserRole.Names.ADMIN)
    @Path(DELETE_ENTITY_BY_ID_REPLACE_PARAMTER)
    public void delete(@PathParam(PATH_PARAM_ID) String universityId) throws FundifyException {
      universityUseCase.deleteUniversity(new UniversityId(universityId));
    }

    @GET
    @Path(ENTITY_LIST)
    public List<UniversityWebModel> list() {
      return UniversityWebModelMapper.INSTANCE.fromDomain(universityUseCase.getAllUniversity());
    }

    @GET
    @Path(ENTITY_BY_ID_REPLACE_PARAMETER)
    public UniversityWebModel getById(@PathParam(PATH_PARAM_ID) String universityId) throws FundifyException {
      return UniversityWebModelMapper.INSTANCE.fromDomain(universityUseCase.getUniversity(new UniversityId(universityId)));
    }


}