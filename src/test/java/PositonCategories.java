import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class PositonCategories {

    Faker faker = new Faker();

    RequestSpecification reqSpec;

    String PsId;

    String PsName;


    @BeforeClass
    public void Setup() {
        baseURI = "https://test.mersys.io";

        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("username", "turkeyts");
        userInfo.put("password", "TechnoStudy123");
        userInfo.put("rememberMe", "true");

        Cookies cookies =

                given()
                        .contentType(ContentType.JSON)
                        .body(userInfo)

                        .when()
                        .post("/auth/login")

                        .then()
                        .statusCode(200)
                        .extract().response().getDetailedCookies();

        reqSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();
    }

    @Test
    public void createPs() {

        Map<String, String> Ps = new HashMap<>();
        PsName = faker.name().fullName();
        Ps.put("name", PsName);

        PsId =
                given()

                        .spec(reqSpec)
                        .body(Ps)
                        .log().body()

                        .when()
                        .post("/school-service/api/position-category")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id")
        ;
        System.out.println("Ps = " + Ps);

    }

    @Test(dependsOnMethods = "createPs")
    public void createPsNegative() {

        Map<String, String> Ps = new HashMap<>();
        Ps.put("name", PsName);

        given()

                .spec(reqSpec)
                .body(Ps)
                .log().body()

                .when()
                .post("/school-service/api/position-category")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already exist"))

        ;
    }

    @Test(dependsOnMethods = "createPsNegative")
    public void updatePs() {

        Map<String, String> Ps = new HashMap<>();
        Ps.put("id", PsId);

        PsName = "Numanerd";
        Ps.put("name", PsName);

        given()
                .spec(reqSpec)
                .body(Ps)

                .when()
                .put("/school-service/api/position-category")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(PsName))

        ;
    }

    @Test(dependsOnMethods = "updatePs")
    public void deletePs() {

        given()

                .spec(reqSpec)
                .pathParam("PsId", PsId)
                .log().uri()

                .when()
                .delete("/school-service/api/position-category/{PsId}")

                .then()
                .log().body()
                .statusCode(204)


        ;
    }

    @Test(dependsOnMethods = "deletePs")
    public void deletePsNegative() {

        given()

                .spec(reqSpec)
                .pathParam("PsId", PsId)
                .log().uri()

                .when()
                .delete("/school-service/api/position-category/{PsId}")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("PositionCategory not  found"))

        ;
    }
}
















