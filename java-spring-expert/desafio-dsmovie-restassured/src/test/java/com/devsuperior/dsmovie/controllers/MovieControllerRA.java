package com.devsuperior.dsmovie.controllers;

import com.devsuperior.dsmovie.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class MovieControllerRA {

    private String movieTitle;
    private Long existingMovieId;
    private Long nonExistingMovieId;
    private String clientUsername;
    private String clientPassword;
    private String adminUsername;
    private String adminPassword;
    private String clientToken;
    private String adminToken;
    private String invalidToken;
    private Map<String, Object> postMovieInstance;

    @BeforeEach
    public void setUp() throws JSONException {
        baseURI = "http://localhost:8080";

        movieTitle = "Django Livre";

        existingMovieId = 1L;
        nonExistingMovieId = 100L;

        clientUsername = "alex@gmail.com";
        clientPassword = "123456";
        adminUsername = "maria@gmail.com";
        adminPassword = "123456";

        clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
        adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
        invalidToken = adminToken + "xpto";

        postMovieInstance = new HashMap<>();
        postMovieInstance.put("title", "Test Movie");
        postMovieInstance.put("score", 0.0);
        postMovieInstance.put("count", 0.0);
        postMovieInstance.put("image", "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");
    }

    @Test
    public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {
        given()
                .get("/movies")
                .then()
                .statusCode(200)
                .body("content.title", hasItems("The Witcher", "Venom: Tempo de Carnificina"));
    }

    @Test
    public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {
        given()
                .get("/movies?title={movieTitle}", movieTitle)
                .then()
                .statusCode(200)
                .body("content.id[0]", is(6))
                .body("content.title[0]", equalTo(movieTitle));
    }

    @Test
    public void findByIdShouldReturnMovieWhenIdExists() {
        given()
                .get("/movies/{id}", existingMovieId)
                .then()
                .statusCode(200)
                .body("title", equalTo("The Witcher"))
                .body("id", is(1))
                .body("count", is(2));
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {
        given()
                .get("/movies/{id}", nonExistingMovieId)
                .then()
                .statusCode(404);
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {
        postMovieInstance.put("title", " ");
        JSONObject newMovie = new JSONObject(postMovieInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newMovie.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/movies")
                .then()
                .statusCode(422)
                .body("errors.message[0]",equalTo("Campo requerido"));
    }

    @Test
    public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
        JSONObject newMovie = new JSONObject(postMovieInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .body(newMovie.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/movies")
                .then()
                .statusCode(403);
    }

    @Test
    public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
        JSONObject newMovie = new JSONObject(postMovieInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + invalidToken)
                .body(newMovie.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/movies")
                .then()
                .statusCode(401);
    }
}
