package io.quarkiverse.oras.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import land.oras.ContainerRef;
import land.oras.Registry;

@QuarkusTest
public class OrasResourceTest {

    @Test
    public void testPushBlob() {
        Registry registry = Registry.builder().insecure().withRegistry("localhost:8081").build();
        registry.pushBlob(ContainerRef.parse("test"), new byte[0]);
        registry.pushBlob(ContainerRef.parse("test"), new byte[0]);
    }

    @Test
    public void testRegistryInjection() {
        given().when().get("/oras/injection").then().statusCode(200).body(is("Injection works"));
    }

    @Test
    public void testPullIndex() {
        given().when().get("/oras/pull-index").then().statusCode(200);
    }

    @Test
    public void testGetTags() {
        given().when().get("/oras/get-tags").then().statusCode(200);
    }

    @Test
    public void testCompressGzip() {
        given().when().get("/oras/compress-gzip").then().statusCode(200).body(is("ok"));
    }

    @Test
    @Disabled("zstd compression is not supported in native mode for now")
    public void testCompressZstd() {
        given().when().get("/oras/compress-zstd").then().statusCode(200).body(is("ok"));
    }

    @Test
    void testRegistryWithDevservice() {
        given().when().get("/oras/devservice/get-repositories").then().statusCode(200);
    }

}
