package API;

import io.qameta.allure.restassured.AllureRestAssured;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class ApiTest {
    @Test (enabled = true)
    public void testApirecal() {
        given()
                .filter(new AllureRestAssured())
                .baseUri("https://pr-cy.ru")
                .when()
                .get("/v1/user/1")
                .then()
                .statusCode(404);
    }
}
