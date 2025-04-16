package io.quarkiverse.oras.it;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

import land.oras.Registry;

@Path("/oras-registry")
@ApplicationScoped
public class OrasRegistryResource {

    @Inject
    @Named("foo")
    Registry fooRegistry;

    @Inject
    @Named("bar")
    Registry barRegistry;

    @Path("/not-null")
    @GET
    @Produces("text/plain")
    public Response checkNotNot() {
        return Response.ok("Injection success").build();
    }
}
