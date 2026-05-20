package at.ac.tuwien.fundify.adapters.in.rest.resources;

import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.BY_ID;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.PATH_PARAM_ID;

import at.ac.tuwien.fundify.adapters.in.rest.dto.UniversityCreateWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.UniversityWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.mapper.UniversityWebModelMapper;
import at.ac.tuwien.fundify.application.port.in.institutions.UniversityUseCase;
import at.ac.tuwien.fundify.domain.annotating.UniversityId;
import at.ac.tuwien.fundify.domain.common.UserRole;
import at.ac.tuwien.fundify.domain.common.exceptions.FundifyException;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
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

@JBossLog
@Path("/api/universities")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
@RequiredArgsConstructor
public class UniversityResource {

    private final UniversityUseCase universityUseCase;

    @POST
    @RolesAllowed({UserRole.Names.ADMIN,})
    public UniversityWebModel add(@Valid UniversityCreateWebModel universityCreateWebModel) {
        return UniversityWebModelMapper.INSTANCE.fromDomain(
            universityUseCase.addUniversity(
                UniversityWebModelMapper.INSTANCE.toDomain(universityCreateWebModel)
            )
        );
    }

    @PUT
    @Path(BY_ID)
    @RolesAllowed({UserRole.Names.ADMIN})
    public UniversityWebModel update(@PathParam(PATH_PARAM_ID) String pathId,
                                     @Valid UniversityWebModel universityWebModel) throws FundifyException {
        if (!pathId.equals(universityWebModel.id())) {
            throw new BadRequestException("Path ID does not match body ID");
        }
        return UniversityWebModelMapper.INSTANCE.fromDomain(
            universityUseCase.updateUniversity(UniversityWebModelMapper.INSTANCE.toDomain(universityWebModel))
        );
    }

    @DELETE
    @RolesAllowed(UserRole.Names.ADMIN)
    @Path(BY_ID)
    public void delete(@PathParam(PATH_PARAM_ID) String universityId) throws FundifyException {
        universityUseCase.deleteUniversity(new UniversityId(universityId));
    }

    @GET
    public List<UniversityWebModel> list() {
        return UniversityWebModelMapper.INSTANCE.fromDomain(universityUseCase.getAllUniversity());
    }

    @GET
    @Path(BY_ID)
    public UniversityWebModel getById(@PathParam(PATH_PARAM_ID) String universityId) throws FundifyException {
        return UniversityWebModelMapper.INSTANCE.fromDomain(universityUseCase.getUniversity(new UniversityId(universityId)));
    }
}
