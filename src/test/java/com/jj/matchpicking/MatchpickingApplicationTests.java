package com.jj.matchpicking;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MatchpickingApplicationTests {

    private static final String GROOVY_PATH_SELF_LINK = "_links.self.href";

    private static final String GROOVY_PATH_MATCH_COUNT = "matchCount";

    private static final String GROOVY_PATH_LAST_OPPONENT_DRAW = "lastOpponentDraw";

    @LocalServerPort
    int port;

    private static Response createNewGame() {
        String payload = new JSONObject().toString();

        return given()
                .contentType(ContentType.JSON)
                .body(payload)
                .post("/games")
                .then()
                .statusCode(201)
                .extract()
                .response();
    }

    private static Response patchGame(String gameLink, String payload) {
        return given()
                .contentType(ContentType.JSON)
                .body(payload)
                .patch(gameLink)
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    private static <T> T extractFromResponse(Response response, String groovyPath) {
        return response
                .jsonPath()
                .get(groovyPath);
    }

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void shouldAccessTheGamesEndpoint() {
        when()
                .get("/games")
                .then()
                .statusCode(200);
    }

    @Test
    public void shouldCreateANewGame() {
        createNewGame();
    }

    @Test
    public void shouldAccessANewGame() {
        String gameLink = extractFromResponse(createNewGame(), GROOVY_PATH_SELF_LINK);

        when()
                .get(gameLink)
                .then()
                .statusCode(200);
    }

    @Test
    public void shouldDrawOneMatch() {
        //given
        Response gameResponse = createNewGame();
        String gameLink = extractFromResponse(gameResponse, GROOVY_PATH_SELF_LINK);
        int matchCount = extractFromResponse(gameResponse, GROOVY_PATH_MATCH_COUNT);
        String payload;
        try {
            payload = new JSONObject()
                    .put("playerDraw", 1)
                    .toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        //when
        Response patchedGameResponse = patchGame(gameLink, payload);
        int newMatchCount = extractFromResponse(patchedGameResponse, GROOVY_PATH_MATCH_COUNT);
        int lastOpponentDraw = extractFromResponse(patchedGameResponse, GROOVY_PATH_LAST_OPPONENT_DRAW);

        //then
        assertThat(matchCount).isEqualTo(newMatchCount + lastOpponentDraw + 1);
    }

    @Test
    public void shouldNotDrawAnyMatches() {
        //given
        Response gameResponse = createNewGame();
        String gameLink = extractFromResponse(gameResponse, GROOVY_PATH_SELF_LINK);
        int matchCount = extractFromResponse(gameResponse, GROOVY_PATH_MATCH_COUNT);
        String payload;
        try {
            payload = new JSONObject()
                    .put("playerDraw", 0)
                    .toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        //when
        Response patchedGameResponse = patchGame(gameLink, payload);
        int newMatchCount = extractFromResponse(patchedGameResponse, GROOVY_PATH_MATCH_COUNT);

        //then
        assertThat(matchCount).isEqualTo(newMatchCount);
    }
}
