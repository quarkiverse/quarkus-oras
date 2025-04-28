package io.quarkiverse.oras.it;

import java.net.URI;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;

import io.quarkiverse.oras.runtime.OCILayouts;
import land.oras.Layer;
import land.oras.LayoutRef;
import land.oras.OCILayout;
import land.oras.exception.OrasException;
import land.oras.utils.Const;

@Path("/v2")
@ApplicationScoped
public class V2Resource {

    @Inject
    OCILayouts ociLayouts;

    @HEAD
    @Path("{name}/blobs/{digest}")
    public Response headBlob(@RestPath("name") String name, @RestPath String digest) {
        OCILayout ociLayout = ociLayouts.getLayout(name);
        try {
            byte[] blob = ociLayout.getBlob(LayoutRef.of(ociLayout).withDigest(digest));
            return Response.ok()
                    .header(Const.CONTENT_LENGTH_HEADER, blob.length)
                    .build();
        } catch (OrasException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        }

    }

    @POST
    @Path("{name}/blobs/uploads")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response uploadMonolitic(@RestPath("name") String name, @RestQuery("digest") String digest, byte[] body) {

        // Return location header if digest is null
        if (digest == null) {
            return Response.accepted()
                    .header(
                            Const.LOCATION_HEADER,
                            "/v2/%s/blobs/uploads/%s"
                                    .formatted(name, UUID.randomUUID().toString()))
                    .build();
        }

        try {
            OCILayout ociLayout = ociLayouts.getLayout(name);
            LayoutRef layoutRef = LayoutRef.of(ociLayout).withDigest(digest);
            Layer layer = ociLayout.pushBlob(layoutRef, body);
            return Response.created(URI.create("/v2/%s/blobs/%s".formatted(name, layer.getDigest())))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("{name}/blobs/{digest}")
    public Response getBlob(@RestPath("name") String name, @RestPath String digest) {
        OCILayout ociLayout = ociLayouts.getLayout(name);
        LayoutRef layoutRef = LayoutRef.of(ociLayout).withDigest(digest);
        byte[] blob = ociLayout.getBlob(layoutRef);
        return Response.ok(blob)
                .build();
    }

}
