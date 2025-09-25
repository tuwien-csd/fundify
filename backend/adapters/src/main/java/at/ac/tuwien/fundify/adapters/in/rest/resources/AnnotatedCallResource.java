package at.ac.tuwien.fundify.adapters.in.rest.resources;


import at.ac.tuwien.fundify.adapters.in.rest.dto.AnnotateRequest;
import at.ac.tuwien.fundify.adapters.in.rest.dto.AnnotatedCallWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.CallAnnotationWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.CallPreviewWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.mapper.AnnotatedCallWebModelMapper;
import at.ac.tuwien.fundify.adapters.in.rest.mapper.CallAnnotationWebModelMapper;
import at.ac.tuwien.fundify.adapters.in.rest.mapper.CallPreviewWebModelMapper;
import at.ac.tuwien.fundify.application.port.in.calls.AnnotatedCallAccessor;
import at.ac.tuwien.fundify.application.port.in.calls.AnnotatedCallUseCase;
import at.ac.tuwien.fundify.domain.annotating.AnnotatedCallId;
import at.ac.tuwien.fundify.domain.annotating.UniversityId;
import at.ac.tuwien.fundify.domain.common.CallId;
import at.ac.tuwien.fundify.domain.common.UserRole;
import at.ac.tuwien.fundify.domain.common.exceptions.EntityNotFoundException;
import at.ac.tuwien.fundify.domain.common.exceptions.FundifyException;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
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

@Path("/api/annotated-calls")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@JBossLog
@Authenticated
@ApplicationScoped
@RequiredArgsConstructor
public class AnnotatedCallResource {

  private final AnnotatedCallUseCase annotatedCallUseCase;
  private final AnnotatedCallAccessor annotatedCallAccessor;

  @POST
  @RolesAllowed(UserRole.Names.ANNOTATOR)
  public AnnotatedCallId annotate(AnnotateRequest requestBody) throws FundifyException {
      return annotatedCallUseCase.addAnnotation(
          new CallId(requestBody.callId()),
          new UniversityId(requestBody.universityId()),
          CallAnnotationWebModelMapper.INSTANCE.toDomain(requestBody.annotation())
      );
  }

  @PUT
  @Path("/{id}")
  @RolesAllowed(UserRole.Names.ANNOTATOR)
  public AnnotatedCallId updateAnnotation(@PathParam("id") String id,
      CallAnnotationWebModel annotation) throws FundifyException {
      return annotatedCallUseCase.updateAnnotation(
          new AnnotatedCallId(id),
          CallAnnotationWebModelMapper.INSTANCE.toDomain(annotation)
      );
  }

  @DELETE
  @Path("/{id}")
  @RolesAllowed(UserRole.Names.ANNOTATOR)
  public boolean deleteAnnotation(@PathParam("id") String id) throws FundifyException {
      return annotatedCallUseCase.deleteAnnotation(new AnnotatedCallId(id));
  }

  @PUT
  @Path("/{id}/publish")
  @RolesAllowed(UserRole.Names.ANNOTATOR)
  public AnnotatedCallId publish(@PathParam("id") String id) throws FundifyException {
      return annotatedCallUseCase.publish(new AnnotatedCallId(id));
  }

  @GET
  @Path("/{id}")
  @RolesAllowed({UserRole.Names.ANNOTATOR, UserRole.Names.ADMIN})
  public AnnotatedCallWebModel getAnnotatedCall(@PathParam("id") String id)
      throws EntityNotFoundException {
    return AnnotatedCallWebModelMapper.INSTANCE
        .toWebModel(annotatedCallAccessor.getById(new AnnotatedCallId(id)));
  }

  @GET
  @RolesAllowed({UserRole.Names.ANNOTATOR, UserRole.Names.ADMIN})
  public List<AnnotatedCallWebModel> getAll() {
      return AnnotatedCallWebModelMapper.INSTANCE
          .toWebModels(annotatedCallAccessor.getAll());
  }

  @GET
  @Path("/by-id")
  @RolesAllowed({UserRole.Names.ANNOTATOR, UserRole.Names.ADMIN})
  public AnnotatedCallWebModel findByCallIdAndUniversityId(@QueryParam("callId") String callId,
      @QueryParam("universityId") String universityId) throws EntityNotFoundException {
    return AnnotatedCallWebModelMapper.INSTANCE
        .toWebModel(annotatedCallAccessor
            .getByCallIdAndUniversityId(new CallId(callId), new UniversityId(universityId)));
  }

  @GET
  @Path("/preview/{callId}")
  @RolesAllowed({UserRole.Names.ANNOTATOR, UserRole.Names.ADMIN})
  public CallPreviewWebModel getCallPreview(@PathParam("callId") String callId)
      throws EntityNotFoundException {
    return CallPreviewWebModelMapper.INSTANCE
        .toWebModel(annotatedCallAccessor.getCallPreviewByCallId(new CallId(callId)));
  }
}