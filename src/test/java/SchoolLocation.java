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
public class SchoolLocation{

    Faker faker=new Faker();
    String schoolId;

    String schoolCap;
    String schoolLocName;
    String shortName;
    RequestSpecification recSpec;
    @BeforeClass
    public void Setup()  {
        baseURI="https://test.mersys.io";

        Map<String,String> userCredential=new HashMap<>();
        userCredential.put("username","turkeyts");
        userCredential.put("password","TechnoStudy123");
        userCredential.put("rememberMe","true");

        Cookies cookies=
                given()
                        .contentType(ContentType.JSON)
                        .body(userCredential)

                        .when()
                        .post("/auth/login")

                        .then()
                        //.log().all()
                        .statusCode(200)
                        .extract().response().getDetailedCookies()
                ;

        recSpec= new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();
    }

    @Test
    public void createSchoolLocation()  {

        System.out.println("baseURI = " + baseURI);

        Map<String,String> schoolLocation=new HashMap<>();

        schoolLocName=faker.address().streetName()+" "+faker.number().digits(5);
        schoolCap=faker.number().digits(2);
        shortName=faker.number().digit();
        schoolLocation.put("active", "true");
        schoolLocation.put("capacity", schoolCap);
        schoolLocation.put("name", schoolLocName);
        schoolLocation.put("school", "6390f3207a3bcb6a7ac977f9");
        schoolLocation.put("shortName",shortName);
        schoolLocation.put("type", "CLASS");


        schoolId=
                given()
                        .spec(recSpec)
                        .body(schoolLocation)
                        .log().body()

                        .when()
                        .post("/school-service/api/location")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");
        ;

        System.out.println("schoolLocId= "+schoolId);

    }
    @Test(dependsOnMethods = "createSchoolLocation")
    public void createSchoolLocationNegative(){


        Map<String,String> schoolLocation=new HashMap<>();

        schoolLocation.put("active", "true");
        schoolLocation.put("capacity", schoolCap);
        schoolLocation.put("name", schoolLocName);
        schoolLocation.put("school", "6390f3207a3bcb6a7ac977f9");
        schoolLocation.put("shortName",shortName);
        schoolLocation.put("type", "CLASS");

        given()
                .spec(recSpec)
                .body(schoolLocation)
                .log().body()

                .when()
                .post("/school-service/api/location")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))
        ;

    }

    @Test(dependsOnMethods = "createSchoolLocationNegative")
    public void updateSchoolLocation(){

        Map<String,String>schoolLocation=new HashMap<>();

        schoolLocation.put("id", schoolId);
        schoolLocName="new school name"+faker.address().streetName()+" "+faker.number().digits(4);

        schoolLocation.put("active", "true");
        schoolLocation.put("capacity", schoolCap);
        schoolLocation.put("name", schoolLocName);
        schoolLocation.put("school", "6390f3207a3bcb6a7ac977f9");
        schoolLocation.put("shortName",shortName);
        schoolLocation.put("type", "CLASS");

        given()
                .spec(recSpec)
                .body(schoolLocation)

                .when()
                .put("/school-service/api/location")

                .then()
                .log().body()
                .statusCode(200)
                .body("name",equalTo(schoolLocName));

        System.out.println(schoolLocName);
    }

    @Test (dependsOnMethods = "updateSchoolLocation")
    public void deleteSchoolLoc(){

        given()
                .spec(recSpec)
                .pathParam("schoolId",schoolId)
                .log().uri()

                .when()
                .delete("/school-service/api/location/{schoolId}")

                .then()
                .log().body()
                .statusCode(200)
        ;

    }
    @Test(dependsOnMethods = "deleteSchoolLoc")
    public void deleteSchoolLocDelete(){

        given()
                .spec(recSpec)
                .pathParam("schoolId", schoolId)
                .log().uri()

                .when()
                .delete("/school-service/api/location/{schoolId}")

                .then()
                .log().body()
                .body("message", equalTo("School Location not found"));
    }
}

