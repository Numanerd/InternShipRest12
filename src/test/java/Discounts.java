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
public class Discounts {
    Faker faker = new Faker();
    RequestSpecification reqSpec;
    String DcId;
    String Description;
    String IntegrationCode;
    String Priority;

    @BeforeClass
    public void Setup() {
        baseURI = "https://test.mersys.io";

        Map<String,String> userInfo= new HashMap<>();
        userInfo.put("username","turkeyts");
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
    public void createDiscounts() {
        Map<String,String> Dc = new HashMap<>();
        Description = faker.lorem().word();
        IntegrationCode = faker.number().digits(8);
        Priority = faker.number().digits(4);
        Dc.put("description",Description);
        Dc.put("code", IntegrationCode);
        Dc.put("priority",Priority);

        DcId =
                given()
                        .spec(reqSpec)
                        .body(Dc)
                        .log().body()

                        .when()
                        .post("/school-service/api/discounts")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id")
                ;
        System.out.println("Dc = " + Dc);
    }
    @Test(dependsOnMethods = "createDiscounts")
    public void createDiscountsNegative() {
        Map<String,String> Dc = new HashMap<>();
        Dc.put("description",Description);
        Dc.put("code", IntegrationCode);
        Dc.put("priority",Priority);

        given()
                .spec(reqSpec)
                .body(Dc)
                .log().body()

                .when()
                .post("/school-service/api/discounts")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already exist"))
                ;


    }
    @Test(dependsOnMethods = "createDiscountsNegative")
    public void updateDiscounts() {
        Map<String,String> Dc = new HashMap<>();
        Dc.put("code", IntegrationCode);
        Dc.put("id",DcId);

        Description = "Gizem";
        Dc.put("description", Description);

        given()
                .spec(reqSpec)
                .body(Dc)

                .when()
                .put("/school-service/api/discounts")

                .then()
                .log().body()
                .statusCode(200)
                .body("description",equalTo(Description))
                ;

    }
    @Test(dependsOnMethods = "updateDiscounts")
    public void deleteDiscounts() {
        given()
                .spec(reqSpec)
                .pathParam("DcId",DcId)
                .log().uri()

                .when()
                .delete("/school-service/api/discounts/{DcId}")

                .then()
                .log().body()
                .statusCode(200)

                ;
    }
@Test(dependsOnMethods = "deleteDiscounts")
    public void deleteDiscountsNegative() {
    given()
            .spec(reqSpec)
            .pathParam("DcId",DcId)
            .log().uri()

            .when()
            .delete("/school-service/api/discounts/{DcId}")

            .then()
            .log().body()
            .statusCode(400)
            .body("message",equalTo("Discount not found"))
            ;

}
}
