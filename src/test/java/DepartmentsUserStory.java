import com.github.javafaker.Faker;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import io.restassured.builder.RequestSpecBuilder;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class DepartmentsUserStory {
    Faker faker =new Faker();

    RequestSpecification reqSpec;

    String DpId;

    String DpName;

    String DpCode;


    @BeforeClass
    public void Setup(){
        baseURI="https://test.mersys.io";

        Map<String, String> userInfo=new HashMap<>();
        userInfo.put("username", "turkeyts");
        userInfo.put("password","TechnoStudy123");
        userInfo.put("rememberMe","true");

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
    public void createDp(){

        Map<String,String> Dp=new HashMap<>();
        DpName=faker.name().firstName();
        DpCode=faker.number().digits(8);
        Dp.put("school", "6390f3207a3bcb6a7ac977f9");
        Dp.put("name",DpName);
        Dp.put("code",DpCode);

        DpId =
                given()
                        .spec(reqSpec)
                        .body(Dp)
                        .log().body()
                        .when()
                        .post("/school-service/api/department")
                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id")
        ;
        System.out.println("Dp = " + Dp);
    }
    @Test(dependsOnMethods = "createDp")
    public void createDpNegative(){

        Map<String,String> Dp = new HashMap<>();
        Dp.put("name",DpName);
        Dp.put("code",DpCode);

        given()
                .spec(reqSpec)
                .body(Dp)
                .log().body()

                .when()
                .post("/school-service/api/department")

                .then()
                .log().body()
                .statusCode(400)
                .body("message",containsString("School cannot empty"))
        ;

    }
    @Test(dependsOnMethods = "createDpNegative")
    public void updateDp(){
        Map<String,String> Dp=new HashMap<>();
        Dp.put("school", "6390f3207a3bcb6a7ac977f9");
        Dp.put("code",DpCode);
        Dp.put("id",DpId);

        DpName="Selintp";
        Dp.put("name",DpName);

        given()
                .spec(reqSpec)
                .body(Dp)

                .when()
                .put("/school-service/api/department")

                .then()
                .log().body()
                .statusCode(200)
                .body("name",equalTo(DpName))

        ;
    }

    @Test(dependsOnMethods ="updateDp")
    public void deleteDp(){
        given()
                .spec(reqSpec)
                .pathParam("DpId",DpId)
                .log().uri()

                .when()
                .delete("/school-service/api/department/{DpId}")

                .then()
                .log().body()
                .statusCode(204)

        ;
    }

    @Test(dependsOnMethods = "deleteDp")
    public void deleteDpNegative(){
        given()
                .spec(reqSpec)
                .pathParam("DpId","id")
                .log().uri()

                .when()
                .delete("/school-service/api/department/{DpId}")

                .then()
                .log().body()
                .statusCode(400)
                .body("message",equalTo("Please, provide valid department id"))
        ;
    }


}
