package at.ac.tuwien.fundify.adapters.in.rest.resources;

import at.ac.tuwien.fundify.adapters.in.rest.constants.SubjectStore;
import at.ac.tuwien.fundify.adapters.in.rest.dto.StandardizedSubjectWebModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Collection;

@ApplicationScoped
@Path("/api/oefos")
@Produces(MediaType.APPLICATION_JSON)
public class SubjectResource {

    @GET
    public Collection<StandardizedSubjectWebModel> getAllOefos() {
        return SubjectStore.getAllSubjects();
    }
}