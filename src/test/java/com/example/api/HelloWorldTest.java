package com.example.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class HelloWorldTest {

    private final String BASE_URL = "http://your-api-url.com"; // Replace with your API URL
    private final String AUTH_TOKEN = "your-auth-token"; // Replace with your authentication token

    @Test
    public void testCreateUser() {
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + AUTH_TOKEN)
            .body("{\"name\": \"John Doe\", \"email\": \"john@example.com\"}")
        .when()
            .post(BASE_URL + "/users")
        .then()
            .statusCode(201);
    }

    @Test
    public void testReadUser() {
        given()
            .header("Authorization", "Bearer " + AUTH_TOKEN)
        .when()
            .get(BASE_URL + "/users/1") // Replace with a valid user ID
        .then()
            .statusCode(200)
            .body("name", equalTo("John Doe"));
    }

    @Test
    public void testUpdateUser() {
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + AUTH_TOKEN)
            .body("{\"name\": \"John Doe Updated\"}")
        .when()
            .put(BASE_URL + "/users/1") // Replace with a valid user ID
        .then()
            .statusCode(200);
    }

    @Test
    public void testDeleteUser() {
        given()
            .header("Authorization", "Bearer " + AUTH_TOKEN)
        .when()
            .delete(BASE_URL + "/users/1") // Replace with a valid user ID
        .then()
            .statusCode(204);
    }
}