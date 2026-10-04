package com.fmarket.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyString;

@QuarkusTest
class VerificationDocumentsResourceTest {

    private static final byte[] IMG = {1, 2, 3};

    private static String createAndLogin() {
        String id = UUID.randomUUID().toString().substring(0, 8);
        given().contentType("application/json")
                .body("""
                        {"name":"Ana","email":"%s@x.com","profile":"%s","password":"secret"}
                        """.formatted(id, id))
                .when().post("/api/accounts").then().statusCode(204);
        return given().contentType("application/json")
                .body("""
                        {"email":"%s@x.com","password":"secret"}
                        """.formatted(id))
                .when().post("/api/login").then().statusCode(200)
                .extract().path("token");
    }

    private static RequestSpecification authed() {
        return given().header("Authorization", "Bearer " + createAndLogin());
    }

    @Test
    void acceptsThreeImages() {
        authed()
                .multiPart("rg_front", "a.png", IMG, "image/png")
                .multiPart("rg_back", "b.jpg", IMG, "image/jpeg")
                .multiPart("selfie_with_rg", "c.webp", IMG, "image/webp")
                .when().post("/api/accounts/me/verification")
                .then().statusCode(204).body(emptyString());
    }

    @Test
    void rejectsWhenAnyImageMissing() {
        authed()
                .multiPart("rg_front", "a.png", IMG, "image/png")
                .multiPart("rg_back", "b.png", IMG, "image/png")
                .when().post("/api/accounts/me/verification")
                .then().statusCode(400);
    }

    @Test
    void rejectsInvalidFormat() {
        authed()
                .multiPart("rg_front", "a.png", IMG, "image/png")
                .multiPart("rg_back", "b.png", IMG, "image/png")
                .multiPart("selfie_with_rg", "c.pdf", IMG, "application/pdf")
                .when().post("/api/accounts/me/verification")
                .then().statusCode(400);
    }

    @Test
    void requiresAuthentication() {
        given().multiPart("rg_front", "a.png", IMG, "image/png")
                .when().post("/api/accounts/me/verification")
                .then().statusCode(401);
    }
}
