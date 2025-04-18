package io.quarkiverse.oras.it;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import land.oras.ContainerRef;
import land.oras.LocalPath;
import land.oras.Registry;
import land.oras.utils.ArchiveUtils;
import land.oras.utils.Const;

@Path("/oras-registry")
@ApplicationScoped
public class OrasRegistryResource {

    @Inject
    @Named("foo")
    Registry fooRegistry;

    @Inject
    @Named("bar")
    Registry barRegistry;

    @Inject
    @Named("docker")
    Registry docker;

    @Path("/injection")
    @GET
    @Produces("text/plain")
    public Response checkNotNot() {
        return Response.ok("ok").build();
    }

    @GET
    @Path("/compress-gzip")
    @Produces(MediaType.TEXT_PLAIN)
    public String compressGzip() throws Exception {
        LocalPath tar = ArchiveUtils.tar(LocalPath.of("src"));
        ArchiveUtils.compress(tar, Const.DEFAULT_BLOB_DIR_MEDIA_TYPE); // gzip
        return "ok";
    }

    @GET
    @Path("/compress-zstd")
    @Produces(MediaType.TEXT_PLAIN)
    public String compressZstd() throws Exception {
        LocalPath tar = ArchiveUtils.tar(LocalPath.of("src"));
        ArchiveUtils.compress(tar, Const.BLOB_DIR_ZSTD_MEDIA_TYPE); // zstd
        return "ok";
    }

    @GET
    @Path("/pull-index")
    @Produces(MediaType.APPLICATION_JSON)
    public String manifest() {
        return docker.getIndex(ContainerRef.parse("library/alpine:latest")).getJson();
    }

}
