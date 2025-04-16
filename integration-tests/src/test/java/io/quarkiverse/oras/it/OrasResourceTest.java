package io.quarkiverse.oras.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class OrasResourceTest {

    @Test
    public void testRegistryInjection() {
        given().when().get("/oras-registry/not-null").then().statusCode(200).body(is("Injection success"));
    }
}
