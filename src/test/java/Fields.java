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

public class Fields {

    RequestSpecification reqSpec;

    Faker faker = new Faker();

    Map<String, String> fields = new HashMap<>();

    String id;

    String name;


    @BeforeClass
    public void Login() {

        baseURI = "https://test.mersys.io";

        Map<String, String> userCredentials = new HashMap<>();
        userCredentials.put("username", "turkeyts");
        userCredentials.put("password", "TechnoStudy123");
        userCredentials.put("rememberMe", "true");

        Cookies cookies =

                given()

                        .contentType(ContentType.JSON)
                        .body(userCredentials)

                        .when()
                        .post("/auth/login")

                        .then()
                        .log().body()
                        .statusCode(200)
                        .extract().response().getDetailedCookies();


        reqSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build()

        ;

    }

    @Test
    public void createFields() {

        name = faker.name().firstName() + " - " + faker.number().digits(3);

        fields.put("name", name);
        fields.put("code", faker.number().digits(3));
        fields.put("type", "STRING");
        fields.put("schoolId", "6390f3207a3bcb6a7ac977f9");

        id =

                given()
                        .spec(reqSpec)
                        .body(fields)


                        .when()
                        .post("/school-service/api/entity-field")


                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id")

        ;


    }

    @Test(dependsOnMethods = "createFields")
    public void createFieldsNegative() {


        fields.put("name", name);
        fields.put("code", faker.number().digits(4));
        fields.put("type", "INTEGER");
        fields.put("schoolId", "6390f3207a3bcb6a7ac977f9");


        given()
                .spec(reqSpec)
                .body(fields)


                .when()
                .post("/school-service/api/entity-field")


                .then()
                .log().body()
                .statusCode(400)

        ;

        fields.clear();
    }


    @Test(dependsOnMethods = "createFieldsNegative")
    public void updateFields() {

        fields.put("id", id);
        fields.put("name", name = faker.name().firstName() + " - " + faker.number().digits(3));
        fields.put("code", faker.number().digits(3));
        fields.put("type", "DECIMAL");
        fields.put("schoolId", "6390f3207a3bcb6a7ac977f9");

        given()
                .spec(reqSpec)
                .body(fields)


                .when()
                .put("/school-service/api/entity-field")


                .then()
                .log().body()
                .statusCode(200)


        ;


    }

    @Test(dependsOnMethods = "updateFields")
    public void deleteFields() {

        given()

                .spec(reqSpec)


                .when()
                .delete("/school-service/api/entity-field/" + id)


                .then()

                .log().body()
                .statusCode(204)

        ;


    }

    @Test(dependsOnMethods = "deleteFields")
    public void deleteFieldsNegative() {

        given()

                .spec(reqSpec)


                .when()
                .delete("/school-service/api/entity-field/" + id)


                .then()
                .log().body()
                .statusCode(400)

        ;

    }
}