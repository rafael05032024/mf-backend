package com.fmarket.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
class CreateAccountResourceTest {

    private static String body(String name, String email, String profile, String password) {
        return """
                {"name":"%s","email":"%s","profile":"%s","password":"%s"}
                """.formatted(name, email, profile, password);
    }

    private void post(String json, int status) {
        given().contentType("application/json").body(json)
                .when().post("/api/accounts")
                .then().statusCode(status);
    }

    @Test
    void createsAccountAndRejectsDuplicates() {
        String id = java.util.UUID.randomUUID().toString().substring(0, 8);
        post(body("Ana", id + "@x.com", id, "secret"), 204);
        post(body("Ana2", id.toUpperCase() + "@x.com", id + "b", "secret"), 409);
        post(body("Ana3", id + "c@x.com", id, "secret"), 409);
    }

    @Test
    void rejectsInvalidInput() {
        post(body("", "b@x.com", "b", "secret"), 400);
        post(body("B", "not-an-email", "b", "secret"), 400);
        post(body("B", "b@x.com", "b", ""), 400);
        post("{}", 400);
    }
}
