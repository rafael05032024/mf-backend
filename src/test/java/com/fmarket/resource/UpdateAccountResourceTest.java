package com.fmarket.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
class UpdateAccountResourceTest {

    private static String createAndLogin(String id) {
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

    private static io.restassured.response.ValidatableResponse patch(String token, String body) {
        var req = given().contentType("application/json").body(body);
        if (token != null) {
            req = req.header("Authorization", "Bearer " + token);
        }
        return req.when().patch("/api/accounts/me").then();
    }

    private static String id() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    void updatesSentFields() {
        String token = createAndLogin(id());
        patch(token, """
                {"real_name":"Ana Souza","birthdate":"1990-05-10","instagram":"@ana"}
                """)
                .statusCode(200)
                .body("message", equalTo("Dados atualizados com sucesso"));
    }

    @Test
    void rejectsWhenAllFieldsEmpty() {
        String token = createAndLogin(id());
        patch(token, """
                {"name":"  ","description":""}
                """)
                .statusCode(400)
                .body("message", equalTo("Não há dados a serem atualizados"));
        patch(token, "{}").statusCode(400);
    }

    @Test
    void rejectsProfileUsedByAnotherUser() {
        String other = id();
        createAndLogin(other);
        String token = createAndLogin(id());
        patch(token, """
                {"profile":"%s"}
                """.formatted(other.toUpperCase())).statusCode(409)
                .body("message", equalTo("Já existe um usuário com o mesmo perfil"));
    }

    @Test
    void allowsKeepingOwnProfile() {
        String id = id();
        String token = createAndLogin(id);
        patch(token, """
                {"profile":"%s"}
                """.formatted(id)).statusCode(200);
    }

    @Test
    void requiresValidToken() {
        patch(null, "{\"name\":\"x\"}").statusCode(401);
        patch("abc.def.ghi", "{\"name\":\"x\"}").statusCode(401);
    }
}
