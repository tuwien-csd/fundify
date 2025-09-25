package at.ac.tuwien.fundify.adapters.in.rest.resources;

import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.ADD_ENTITY;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.DELETE_ENTITY_BY_ID_REPLACE_PARAMTER;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.ENTITY_BY_ID_REPLACE_PARAMETER;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.ENTITY_LIST;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.PATH_PARAM_ID;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.REFERENCE_BY_ID_REPLACE_PARAMETER;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.REFERENCE_LIST;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.REFERENCE_LIST_SEARCH_ADD_QUERY_PARAM;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.UPDATE_ENTITY;

import at.ac.tuwien.fundify.adapters.in.rest.dto.FunderCreateWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.FunderRefWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.FunderWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.mapper.FunderRefWebModelMapper;
import at.ac.tuwien.fundify.adapters.in.rest.mapper.FunderWebModelMapper;
import at.ac.tuwien.fundify.application.port.in.institutions.FunderUseCase;
import at.ac.tuwien.fundify.domain.common.FunderId;
import at.ac.tuwien.fundify.domain.common.UserRole;
import at.ac.tuwien.fundify.domain.common.exceptions.FundifyException;
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


@JBossLog
@Path("/api/funder")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
@RequiredArgsConstructor
public class FunderResource {

    private final FunderUseCase funderUseCase;

    @POST
    @Path(ADD_ENTITY)
    @RolesAllowed({UserRole.Names.ADMIN, UserRole.Names.FUNDER})
    public FunderWebModel add(FunderCreateWebModel funderCreateWebModel) {
        return FunderWebModelMapper.INSTANCE.fromDomain(
                funderUseCase.addFunder(
                        FunderWebModelMapper.INSTANCE.toDomain(funderCreateWebModel)
                )
        );
    }

    @PUT
    @Path(UPDATE_ENTITY)
    @RolesAllowed({UserRole.Names.ADMIN, UserRole.Names.FUNDER})
    public FunderWebModel update(FunderWebModel funderWebModel) throws FundifyException {
        return FunderWebModelMapper.INSTANCE.fromDomain(
            funderUseCase.updateFunder(FunderWebModelMapper.INSTANCE.toDomain(funderWebModel))
        );
    }

    @DELETE
    @RolesAllowed(UserRole.Names.ADMIN)
    @Path(DELETE_ENTITY_BY_ID_REPLACE_PARAMTER)
    public void delete(@PathParam(PATH_PARAM_ID) String funderId) throws FundifyException {
      funderUseCase.deleteFunder(new FunderId(funderId));
    }

    @GET
    @Path(ENTITY_LIST)
    public List<FunderWebModel> list() {
        return FunderWebModelMapper.INSTANCE.fromDomain(funderUseCase.getAllFunders());
    }

    @GET
    @Path(REFERENCE_LIST)
    public List<FunderRefWebModel> referencesList() {
        return funderUseCase.getAllFunderReferences()
                .stream()
                .map(FunderRefWebModelMapper.INSTANCE::fromDomain)
                .toList();
    }

    @GET
    @Path(ENTITY_BY_ID_REPLACE_PARAMETER)
    public FunderWebModel getById(@PathParam(PATH_PARAM_ID) String funderId) throws FundifyException {
        return FunderWebModelMapper.INSTANCE.fromDomain(funderUseCase.getFunder(new FunderId(funderId)));
    }

    @GET
    @Path(REFERENCE_BY_ID_REPLACE_PARAMETER)
    public FunderRefWebModel getReferenceById(@PathParam(PATH_PARAM_ID) String funderId) throws FundifyException {
        return FunderRefWebModelMapper.INSTANCE.fromDomain(
                funderUseCase.getFunderReference(new FunderId(funderId))
        );
    }

    @GET
    @Path(REFERENCE_LIST_SEARCH_ADD_QUERY_PARAM)
    public List<FunderRefWebModel> search(@QueryParam("term") String searchTerm) {
        return FunderRefWebModelMapper.INSTANCE.fromDomain(
                funderUseCase.searchFunderReferences(searchTerm)
        );
    }
}
