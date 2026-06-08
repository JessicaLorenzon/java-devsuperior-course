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
import static org.hamcrest.Matchers.equalTo;

public class ScoreControllerRA {

    private Long existingMovieId;
    private Long nonExistingMovieId;
    private String adminUsername;
    private String adminPassword;
    private String adminToken;
    private Map<String, Object> putScoreInstance;

    @BeforeEach
    public void setUp() throws JSONException {
        baseURI = "http://localhost:8080";

        existingMovieId = 1L;
        nonExistingMovieId = 100L;

        adminUsername = "maria@gmail.com";
        adminPassword = "123456";

        adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);

        putScoreInstance = new HashMap<>();
        putScoreInstance.put("movieId", 1);
        putScoreInstance.put("score", 4);
    }

    @Test
    public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {
        putScoreInstance.put("movieId", nonExistingMovieId);
        JSONObject putScore = new JSONObject(putScoreInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(putScore.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/scores")
                .then()
                .statusCode(404);
    }

    @Test
    public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {
        putScoreInstance.put("movieId", null);
        JSONObject putScore = new JSONObject(putScoreInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(putScore.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/scores")
                .then()
                .statusCode(422);
    }

    @Test
    public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {
        putScoreInstance.put("score", -4);
        JSONObject putScore = new JSONObject(putScoreInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(putScore.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/scores")
                .then()
                .statusCode(422)
                .body("errors.message[0]", equalTo("Valor mínimo 0"));
    }
}
