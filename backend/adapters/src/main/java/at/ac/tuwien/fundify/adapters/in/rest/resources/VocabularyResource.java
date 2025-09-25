package at.ac.tuwien.fundify.adapters.in.rest.resources;

import at.ac.tuwien.fundify.adapters.in.rest.dto.VocabularyWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.mapper.VocabularyWebModelMapper;
import at.ac.tuwien.fundify.application.port.in.vocabularies.VocabularyAccessor;
import at.ac.tuwien.fundify.application.port.in.vocabularies.VocabularyUseCase;
import at.ac.tuwien.fundify.domain.annotating.UniversityId;
import at.ac.tuwien.fundify.domain.common.UserRole;
import at.ac.tuwien.fundify.domain.common.VocabularyId;
import at.ac.tuwien.fundify.domain.common.exceptions.FundifyException;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@Path("/api/vocabularies")
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
@RequiredArgsConstructor
public class VocabularyResource {

    private final VocabularyAccessor vocabularyAccessor;
    private final VocabularyUseCase vocabularyUseCase;

    @PUT
    @Path("/{id}/entries/{value}")
    @RolesAllowed(UserRole.Names.ANNOTATOR)
    public Response addEntry(@PathParam("id") String id, @PathParam("value") String value)
        throws FundifyException {
            vocabularyUseCase.addEntry(new VocabularyId(id), value);
            return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}/entries/{value}")
    @RolesAllowed(UserRole.Names.ANNOTATOR)
    public Response deleteEntry(@PathParam("id") String id, @PathParam("value") String value)
        throws FundifyException {
            vocabularyUseCase.deleteEntry(new VocabularyId(id), value);
            return Response.noContent().build();
    }

    @GET
    @Path("/{id}")
    public VocabularyWebModel getVocabularyById(@PathParam("id") String id)
        throws FundifyException {
            return VocabularyWebModelMapper.INSTANCE.fromDTO(vocabularyAccessor.getById(new VocabularyId(id)));
    }

    @GET
    public List<VocabularyWebModel> getAll() {
      return VocabularyWebModelMapper.INSTANCE.fromDTO(vocabularyAccessor.getAll());
    }

    @GET
    public List<VocabularyWebModel> getVocabulariesByUniversityId(@QueryParam("universityId") String universityIdRaw)
        throws FundifyException {
            UniversityId universityId = new UniversityId(universityIdRaw);
            vocabularyUseCase.ensureInitialized(universityId);
            return VocabularyWebModelMapper.INSTANCE.fromDTO(vocabularyAccessor.getByUniversityId(universityId));
    }
}