package com.fmarket.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class LoginResourceTest {

    private static void createAccount(String email, String profile, String password) {
        given().contentType("application/json")
                .body("""
                        {"name":"Ana","email":"%s","profile":"%s","password":"%s"}
                        """.formatted(email, profile, password))
                .when().post("/api/accounts")
                .then().statusCode(204);
    }

    private static io.restassured.response.ValidatableResponse login(String email, String password) {
        return given().contentType("application/json")
                .body("""
                        {"email":"%s","password":"%s"}
                        """.formatted(email, password))
                .when().post("/api/login")
                .then();
    }

    @Test
    void returnsTokenWithUserIdAndTwoHoursExpiration() {
        String id = UUID.randomUUID().toString().substring(0, 8);
        createAccount(id + "@x.com", id, "secret");

        String token = login(id + "@x.com", "secret")
                .statusCode(200)
                .body("token", notNullValue())
                .extract().path("token");

        String payload = new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]), StandardCharsets.UTF_8);
        io.restassured.path.json.JsonPath json = io.restassured.path.json.JsonPath.from(payload);
        org.junit.jupiter.api.Assertions.assertNotNull(json.getString("sub"));
        org.junit.jupiter.api.Assertions.assertEquals(2 * 60 * 60, json.getLong("exp") - json.getLong("iat"));
    }

    @Test
    void rejectsUnknownUser() {
        login("nobody-" + UUID.randomUUID() + "@x.com", "secret")
                .statusCode(404)
                .body("message", equalTo("Usuário inexistente"));
    }

    @Test
    void rejectsWrongPassword() {
        String id = UUID.randomUUID().toString().substring(0, 8);
        createAccount(id + "@x.com", id, "secret");

        login(id + "@x.com", "wrong")
                .statusCode(401)
                .body("message", equalTo("Senha inválida"));
    }

    @Test
    void rejectsInvalidInput() {
        login("not-an-email", "secret").statusCode(400);
        login("a@x.com", "").statusCode(400);
    }
}
