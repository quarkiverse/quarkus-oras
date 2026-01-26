package io.quarkiverse.oras.it;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.quarkiverse.oras.runtime.OCILayouts;
import io.quarkiverse.oras.runtime.OrasRegistry;
import land.oras.ContainerRef;
import land.oras.LocalPath;
import land.oras.Registry;
import land.oras.utils.ArchiveUtils;
import land.oras.utils.Const;

@Path("/oras")
@ApplicationScoped
public class OrasResource {

    @OrasRegistry("foo")
    Registry foo;

    @OrasRegistry("bar")
    Registry bar;

    @OrasRegistry("samples")
    Registry samples;

    @OrasRegistry("docker")
    Registry docker;

    @OrasRegistry("devs1")
    Registry devService1Registry;

    @OrasRegistry("devs2")
    Registry devService2Registry;

    @Inject
    OCILayouts ociLayouts;

    @Path("/injection")
    @GET
    @Produces("text/plain")
    public Response injectionWorks() {
        String layoutName = "test";
        if (docker == null || foo == null || bar == null || samples == null) {
            return Response.serverError().entity("Registry Injection failed").build();
        }
        if (ociLayouts == null) {
            return Response.serverError().entity("OCI Layouts Recorder failed").build();
        }
        return Response.ok("Injection works").build();
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

    @GET
    @Path("/get-tags")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getTags() {
        return docker.getTags(ContainerRef.parse("library/alpine:latest")).tags();
    }

    @GET
    @Path("/devservice/get-repositories1")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getDevserviceRepositories1() {
        return devService1Registry.getRepositories().repositories();
    }

    @GET
    @Path("/devservice/get-repositories2")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getDevserviceRepositories2() {
        return devService2Registry.getRepositories().repositories();
    }

}
