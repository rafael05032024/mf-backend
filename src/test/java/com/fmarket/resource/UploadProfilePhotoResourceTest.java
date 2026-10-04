package com.fmarket.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyString;

@QuarkusTest
class UploadProfilePhotoResourceTest {

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

    @Test
    void uploadsPhotoAndReturnsUrl() {
        given().header("Authorization", "Bearer " + createAndLogin())
                .multiPart("file", "me.png", new byte[] {1, 2, 3}, "image/png")
                .when().post("/api/accounts/me/photo")
                .then().statusCode(204)
                .body(emptyString());
    }

    @Test
    void rejectsNonImage() {
        given().header("Authorization", "Bearer " + createAndLogin())
                .multiPart("file", "me.txt", new byte[] {1, 2, 3}, "text/plain")
                .when().post("/api/accounts/me/photo")
                .then().statusCode(400);
    }

    @Test
    void rejectsMissingFile() {
        given().header("Authorization", "Bearer " + createAndLogin())
                .multiPart("other", "x")
                .when().post("/api/accounts/me/photo")
                .then().statusCode(400);
    }

    @Test
    void requiresAuthentication() {
        given().multiPart("file", "me.png", new byte[] {1}, "image/png")
                .when().post("/api/accounts/me/photo")
                .then().statusCode(401);
    }
}
