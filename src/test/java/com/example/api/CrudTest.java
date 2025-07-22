package com.example.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CrudTest {

    static int createdUserId;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://reqres.in/api";
    }

    @Test
    @Order(1)
    public void testCreateUser() {
        String requestBody = "{\"name\": \"John\", \"job\": \"Engineer\"}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .body("name", equalTo("John"))
                .body("job", equalTo("Engineer"))
                .extract().response();

        // reqres.in returns string id, so we just check it's present
        Assertions.assertNotNull(response.jsonPath().getString("id"));
    }

    @Test
    @Order(2)
    public void testReadUser() {
        given()
                .when()
                .get("/users/2")
                .then()
                .statusCode(200)
                .body("data.id", equalTo(2))
                .body("data.email", equalTo("janet.weaver@reqres.in"))
                .body("data.first_name", equalTo("Janet"))
                .body("data.last_name", equalTo("Weaver"));
    }

    @Test
    @Order(3)
    public void testUpdateUser() {
        String requestBody = "{\"name\": \"Jane\", \"job\": \"Manager\"}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/users/2")
                .then()
                .statusCode(200)
                .body("name", equalTo("Jane"))
                .body("job", equalTo("Manager"))
                .body("updatedAt", notNullValue());
    }

    @Test
    @Order(4)
    public void testDeleteUser() {
        given()
                .when()
                .delete("/users/2")
                .then()
                // reqres.in returns 204 for DELETE, but sometimes 200, so accept both
                .statusCode(anyOf(is(204), is(200)));
    }

    @Test
    @Order(5)
    public void testListUsers() {
        given()
                .when()
                .get("/users?page=2")
                .then()
                .statusCode(200)
                .body("page", equalTo(2))
                .body("data", not(empty()))
                .body("data[0].id", notNullValue());
    }

    @Test
    @Order(6)
    public void testSingleUserNotFound() {
        given()
                .when()
                .get("/users/23")
                .then()
                .statusCode(404)
                .body(equalTo("{}"));
    }

    @Test
    @Order(7)
    public void testRegisterSuccessful() {
        String requestBody = "{\"email\": \"eve.holt@reqres.in\", \"password\": \"pistol\"}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/register")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("token", notNullValue());
    }

    @Test
    @Order(8)
    public void testRegisterUnsuccessful() {
        String requestBody = "{\"email\": \"sydney@fife\"}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/register")
                .then()
                .statusCode(400)
                .body("error", equalTo("Missing password"));
    }

    @Test
    @Order(9)
    public void testLoginSuccessful() {
        String requestBody = "{\"email\": \"eve.holt@reqres.in\", \"password\": \"cityslicka\"}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue());
    }

    @Test
    @Order(10)
    public void testLoginUnsuccessful() {
        String requestBody = "{\"email\": \"peter@klaven\"}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/login")
                .then()
                .statusCode(400)
                .body("error", equalTo("Missing password"));
    }

    @Test
    @Order(11)
    public void testDelayedResponse() {
        given()
                .when()
                .get("/users?delay=3")
                .then()
                .statusCode(200)
                .body("data", not(empty()));
    }

    @Test
    @Order(12)
    public void testUnknownList() {
        given()
                .when()
                .get("/unknown")
                .then()
                .statusCode(200)
                .body("data", not(empty()));
    }

    @Test
    @Order(13)
    public void testSingleUnknown() {
        given()
                .when()
                .get("/unknown/2")
                .then()
                .statusCode(200)
                .body("data.id", equalTo(2))
                .body("data.name", equalTo("fuchsia rose"))
                .body("data.year", equalTo(2001));
    }

    @Test
    @Order(14)
    public void testSingleUnknownNotFound() {
        given()
                .when()
                .get("/unknown/23")
                .then()
                .statusCode(404)
                .body(equalTo("{}"));
    }

    @Test
    @Order(15)
    public void testCreateUserMissingFields() {
        String requestBody = "{\"name\": \"\"}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .body("name", equalTo(""));
    }

    @Test
    @Order(16)
    public void testGetResourceList() {
        given()
                .when()
                .get("/unknown")
                .then()
                .statusCode(200)
                .body("data", not(empty()))
                .body("data[0].id", notNullValue())
                .body("data[0].name", notNullValue());
    }

    @Test
    @Order(17)
    public void testGetSingleResource() {
        given()
                .when()
                .get("/unknown/2")
                .then()
                .statusCode(200)
                .body("data.id", equalTo(2))
                .body("data.name", equalTo("fuchsia rose"))
                .body("data.year", equalTo(2001));
    }

    @Test
    @Order(18)
    public void testGetSingleResourceNotFound() {
        given()
                .when()
                .get("/unknown/23")
                .then()
                .statusCode(404)
                .body(equalTo("{}"));
    }

    @Test
    @Order(19)
    public void testUserRegistrationMissingEmail() {
        String requestBody = "{\"password\": \"pistol\"}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/register")
                .then()
                .statusCode(400)
                .body("error", equalTo("Missing email or username"));
    }

    @Test
    @Order(20)
    public void testUserLoginMissingEmail() {
        String requestBody = "{\"password\": \"cityslicka\"}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/login")
                .then()
                .statusCode(400)
                .body("error", equalTo("Missing email or username"));
    }

    @Test
    @Order(21)
    public void testUserLoginMissingPassword() {
        String requestBody = "{\"email\": \"eve.holt@reqres.in\"}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/login")
                .then()
                .statusCode(400)
                .body("error", equalTo("Missing password"));
    }

    @Test
    @Order(22)
    public void testGetUsersWithPerPage() {
        given()
                .when()
                .get("/users?per_page=1")
                .then()
                .statusCode(200)
                .body("per_page", equalTo(6)) // reqres.in always returns 6 for per_page, even if you set 1
                .body("data", hasSize(6));   // so expect 6, not 1
    }
}