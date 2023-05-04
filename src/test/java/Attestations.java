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
public class Attestations {
    RequestSpecification requestSpec;
    Faker faker=new Faker();
    String attesName;
    String forAttes;
    @BeforeClass
    public void LoginPage(){
        baseURI="https://test.mersys.io";
        Map<String,String> login =new HashMap<>();
        login.put("username","turkeyts");
        login.put("password","TechnoStudy123");
        login.put("rememberMe","true");

        Cookies cookies =
                given()
                        .contentType(ContentType.JSON)
                        .body(login)
                        .when()
                        .post("/auth/login")
                        .then()
                        .statusCode(200)
                        .extract().response().detailedCookies();

        requestSpec=new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();


    }

    @Test
    public void createAttestations(){

        Map<String,String> attes=new HashMap<>();
        attesName=faker.name().firstName();
        attes.put("name",attesName);

        forAttes=
                given()
                        .spec(requestSpec)
                        .log().body()
                        .body(attes)


                        .when()


                        .post("/school-service/api/attestation")


                        .then()
                        .statusCode(201)
                        .log().body()
                        .extract().path("id");



    }

    @Test(dependsOnMethods = "createAttestations")
    public void createAttestationsNegative(){
        Map<String,String>attesNeg=new HashMap<>();
        attesNeg.put("name",attesName);


        given()
                .spec(requestSpec)
                .log().body()
                .body(attesNeg)


                .when()
                .post("/school-service/api/attestation")


                .then()
                .log().body()
                .statusCode(400)
                .body("message",containsString("already exists."));

    }

    @Test(dependsOnMethods = "createAttestationsNegative")
    public void updateAttestations(){

        Map<String,String>upAttes=new HashMap<>();
        upAttes.put("id",forAttes);
        attesName=faker.name().fullName();
        upAttes.put("name",attesName);



        given()
                .spec(requestSpec)
                .body(upAttes)
                .when()


                .put("/school-service/api/attestation")

                .then()
                .statusCode(200)
                .log().body()
                .body("name",equalTo(attesName));

    }


    @Test(dependsOnMethods = "updateAttestations")
    public void deleteAttestations(){

        given()
                .spec(requestSpec)

                .when()
                .delete("/school-service/api/attestation/"+forAttes)
                .then()
                .statusCode(204)
                .log().body();

    }

    @Test(dependsOnMethods = "deleteAttestations" )
    public void deleteAttestationsNegative(){

        given()
                .spec(requestSpec)



                .when()
                .delete("/school-service/api/attestation/"+forAttes)
                .then()
                .log().body()
                .statusCode(400)
                .body("message",equalTo("attestation not found"));

    }



}
