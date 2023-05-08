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

public class GradeLevels {

    String gradeID;

    String gradeName;

    Faker faker = new Faker();

    RequestSpecification recSpec;

    @BeforeClass
    public void Login() {
        baseURI = "https://test.mersys.io";

        Map<String, String> userCredential = new HashMap<>();
        userCredential.put("username", "turkeyts");
        userCredential.put("password", "TechnoStudy123");
        userCredential.put("rememberMe", "true");

        Cookies cookies =
                given()
                        .contentType(ContentType.JSON)
                        .body(userCredential)

                        .when()
                        .post("/auth/login")

                        .then()
                        .statusCode(200)
                        .extract().response().getDetailedCookies();

        recSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();

    }

    @Test
    public void CreateGradeLevels() {

        Map<String, String> grade = new HashMap<>();
        gradeName = faker.artist().name() + faker.name().firstName();
        grade.put("name", gradeName);
        grade.put("shortName", faker.name().lastName());
        grade.put("order", faker.number().digit() + faker.number().randomDigitNotZero());

        gradeID =
                given()
                        .spec(recSpec)
                        .body(grade)
                        .log().body()

                        .when()
                        .post("school-service/api/grade-levels")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id")
        ;

    }

    @Test(dependsOnMethods = "CreateGradeLevels")
    public void CreateGradeLevelsNegative() {

        Map<String, String> grade = new HashMap<>();
        grade.put("name", gradeName);
        grade.put("shortName", faker.name().firstName());
        grade.put("order", faker.number().digit() + faker.number().randomDigitNotZero());

        given()
                .spec(recSpec)
                .body(grade)
                .log().body()

                .when()
                .post("school-service/api/grade-levels")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))
        ;
    }

    @Test(dependsOnMethods = "CreateGradeLevelsNegative")
    public void UpdateGradeLevel() {

        Map<String, String> grade = new HashMap<>();
        grade.put("id", gradeID);

        gradeName = faker.name().fullName() + faker.number().digits(4);
        grade.put("name", gradeName);
        grade.put("shortName", faker.name().lastName());
        grade.put("order", faker.number().digit() + faker.number().randomDigitNotZero());

        given()
                .spec(recSpec)
                .body(grade)

                .when()
                .put("school-service/api/grade-levels")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(gradeName))
        ;
    }

    @Test(dependsOnMethods = "UpdateGradeLevel")
    public void DeleteGradeLevel() {

        given()
                .spec(recSpec)
                .pathParam("gradeID", gradeID)
                .log().uri()

                .when()
                .delete("school-service/api/grade-levels/{gradeID}")

                .then()
                .log().body()
                .statusCode(200)
        ;
    }

    @Test(dependsOnMethods = "DeleteGradeLevel")
    public void DeleteGradeLevelNegative() {

        given()
                .spec(recSpec)
                .pathParam("gradeID",gradeID)
                .log().uri()

                .when()
                .delete("school-service/api/grade-levels/{gradeID}")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("Grade Level not found."))
                ;


    }

}
