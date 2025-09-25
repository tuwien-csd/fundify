package at.ac.tuwien.refop.bootstrap;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;

@ApplicationPath("/")
@OpenAPIDefinition(
        info = @Info(
                title = "FUNDify - Public API",
                version = "v1"
        )
)
public class RefopApplication extends Application {}