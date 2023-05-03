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

public class PositionCategoriesUnderHuman {


    Faker faker= new Faker();

    RequestSpecification requestSpec;

    String positionName;
    String positionID;




    @BeforeClass
    public void loginAndStup(){

        baseURI="https://test.mersys.io";
        Map<String,String> loginBody=new HashMap<>();

        loginBody.put("username","turkeyts");
        loginBody.put("password","TechnoStudy123");
        loginBody.put("rememberMe","true");

        Cookies cookies=

        given()
                .contentType(ContentType.JSON)
                .body(loginBody)


                .when()

                .post("/auth/login")


                .then()
                .statusCode(200)
                .extract().response().getDetailedCookies();


                requestSpec=new RequestSpecBuilder()
                        .setContentType(ContentType.JSON)
                        .addCookies(cookies)
                        .build();

    }
    @Test
    public void createPosition(){

        Map<String,String>position=new HashMap<>();

        positionName=faker.name().fullName()+faker.number().digits(3);
        position.put("name",positionName);
        position.put("shortName",faker.name().firstName()+faker.number().digits(3));
        position.put("tenantId",faker.number().digits(24));



    positionID=
        given()
                .spec(requestSpec)
                .log().body()
                .body(position)


                .when()


                .post("/school-service/api/employee-position")



                .then()
                .statusCode(201)
                .log().body()
                .extract().path("id");


    }


}

