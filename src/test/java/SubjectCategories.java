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

public class SubjectCategories {

    Faker faker =new Faker();
    RequestSpecification requestSpec;
    String subjectName;
    String subjectID;



    @BeforeClass
    public void Login(){
        baseURI="https://test.mersys.io";
        Map<String,String> login=new HashMap<>();
        login.put("username","turkeyts");
        login.put("password","TechnoStudy123");
        login.put("rememberMe","true");

        Cookies cookies=
                given()
                        .contentType(ContentType.JSON)
                        .body(login)
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
    public void createSubject(){

        Map<String,String>subject=new HashMap<>();
        subjectName=faker.name().firstName()+faker.number().digits(2);
        subject.put("name",subjectName);
        subject.put("code",faker.number().digits(4));


        subjectID=
                given()
                        .spec(requestSpec)
                        .log().body()
                        .body(subject)
                        .when()
                        .post("/school-service/api/subject-categories")
                        .then()
                        .statusCode(201)
                        .log().body()
                        .extract().path("id");



    }
    @Test(dependsOnMethods = "createSubject")
    public void createSubjectNegative(){

        Map<String,String>subjectNeg=new HashMap<>();
        subjectNeg.put("name",subjectName);
        subjectNeg.put("code",faker.number().digits(4));

        given()
                .spec(requestSpec)
                .log().body()
                .body(subjectNeg)
                .when()
                .post("/school-service/api/subject-categories")
                .then()
                .log().body()
                .statusCode(400)
                .body("message",containsString("already exists"));

    }
    @Test(dependsOnMethods = "createSubjectNegative")
    public void updateSubject(){

        Map<String,String>updatesub=new HashMap<>();
        updatesub.put("id",subjectID);
        subjectName=faker.name().fullName()+faker.number().digits(2);
        updatesub.put("name",subjectName);
        updatesub.put("code",faker.number().digits(4));

        given()
                .spec(requestSpec)
                .body(updatesub)
                .when()
                .put("/school-service/api/subject-categories")
                .then()
                .statusCode(200)
                .log().body()
                .body("name",equalTo(subjectName));

    }

    @Test(dependsOnMethods = "updateSubject")
    public void deleteSubject(){

        given()
                .spec(requestSpec)
                .when()
                .delete("/school-service/api/subject-categories/"+subjectID)
                .then()
                .statusCode(200)
                .log().body();

    }

    @Test(dependsOnMethods = "deleteSubject")
    public void deleteSubjectNegative(){

        given()
                .spec(requestSpec)
                .when()
                .delete("/school-service/api/subject-categories/"+subjectID)
                .then()
                .log().body()
                .statusCode(400)
                .body("message",equalTo("SubjectCategory not  found"));
    }

}
