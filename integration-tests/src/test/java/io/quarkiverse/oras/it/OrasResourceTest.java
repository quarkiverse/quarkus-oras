package io.quarkiverse.oras.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

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
    public void testPullDefaultsIndex() {
        given().when().get("/oras/pull-defaults-index").then().statusCode(200);
    }

    @Test
    public void testGetTags() {
        given().when().get("/oras/get-tags").then().statusCode(200);
    }

    @Test
    public void testGetDefaultsTags() {
        given().when().get("/oras/get-defaults-tags").then().statusCode(200);
    }

    @Test
    public void testCompressGzip() {
        given().when().get("/oras/compress-gzip").then().statusCode(200).body(is("ok"));
    }

    @Test
    public void testCompressZstd() {
        given().when().get("/oras/compress-zstd").then().statusCode(200).body(is("ok"));
    }

    @Test
    void testRegistryWithDevservice1() {
        given().when().get("/oras/devservice/get-repositories1").then().statusCode(200);
    }

    @Test
    void testRegistryWithDevservice2() {
        given().when().get("/oras/devservice/get-repositories2").then().statusCode(200);
    }

}
