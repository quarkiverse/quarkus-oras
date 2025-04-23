package io.quarkiverse.oras.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class OrasResourceTest {

    @Test
    public void testRegistryInjection() {
        given().when().get("/oras-registry/injection").then().statusCode(200).body(is("Injection works"));
    }

    @Test
    public void testPullIndex() {
        given().when().get("/oras-registry/pull-index").then().statusCode(200);
    }

    @Test
    public void testGetTags() {
        given().when().get("/oras-registry/get-tags").then().statusCode(200);
    }

    @Test
    public void testCompressGzip() {
        given().when().get("/oras-registry/compress-gzip").then().statusCode(200).body(is("ok"));
    }

    @Test
    @Disabled("zstd compression is not supported in native mode for now")
    public void testCompressZstd() {
        given().when().get("/oras-registry/compress-zstd").then().statusCode(200).body(is("ok"));
    }

}
