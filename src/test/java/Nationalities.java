import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

public class Nationalities {
    RequestSpecification reqSpec;
    Faker faker = new Faker();
    Map<String, String> name1 = new HashMap<>();
    String name;
    String id;

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
    public void createNationalities() {

        name = faker.name().firstName() + " - " + faker.number().digits(3);

        name1.put("name", name);

        id =

                given()
                        .spec(reqSpec)
                        .body(name1)


                        .when()
                        .post("/school-service/api/nationality")


                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id")

        ;


    }

@Test(dependsOnMethods = "createNationalities")
    public void createNationalitiesNegative() {

    name1.put("name", name);


            given()
                    .spec(reqSpec)
                    .body(name1)


                    .when()
                    .post("/school-service/api/nationality")


                    .then()
                    .log().body()
                    .statusCode(400)


    ;


}
@Test(dependsOnMethods = "createNationalitiesNegative")
    public void updateNationalities() {

        name=faker.name().firstName()+"-"+faker.number().digits(3);
        name1.put("id", id);
    name1.put("name", name);


    given()
            .spec(reqSpec)
            .body(name1)


            .when()
            .put("/school-service/api/nationality")


            .then()
            .log().body()
            .statusCode(200)


    ;


}
@Test(dependsOnMethods = "updateNationalities")
    public void deleteNationalities() {
    given()
            .spec(reqSpec)
            .body(name1)


            .when()
            .delete("/school-service/api/nationality/"+id)


            .then()
            .log().body()
            .statusCode(200)


    ;


}
@Test(dependsOnMethods = "deleteNationalities")
    public void deleteNationalitiesNegative() {
    given()
            .spec(reqSpec)
            .body(name1)


            .when()
            .delete("/school-service/api/nationality/"+id)


            .then()
            .log().body()
            .statusCode(400)

            ;

}

}
