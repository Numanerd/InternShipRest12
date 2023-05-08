

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
public class DocumentType {

    Faker faker = new Faker();
    RequestSpecification recSpec;
    String name = "Marketing";
    String documentTypeId;
    String documentTypeName;
    String[] attachmentStages = {"STUDENT_REGISTRATION"};
    String schoolId = "6390f3207a3bcb6a7ac977f9";
    Map<String, Object> documentType = new HashMap<>();

    @BeforeClass

    public void Loginpg() {
        baseURI = "https://test.mersys.io";
        Map<String, String> user = new HashMap<>(); //user=userCredential
        user.put("username", "turkeyts");
        user.put("password", "TechnoStudy123");
        user.put("rememberMe", "true");

        Cookies cookies =
                given()
                        .contentType(ContentType.JSON)
                        .body(user)

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

    public void createDocumentType() {


        documentType.put("name", name);
        documentType.put("attachmentStages", attachmentStages);
        documentType.put("schoolId", schoolId);

        documentTypeId =
                given()
                        .spec(recSpec)
                        .body(documentType)
                        //   .log().body()

                        .when()
                        .post("/school-service/api/attachments/create")

                        .then()
                        //.log().body()
                        .statusCode(201)
                        .extract().path("id")
        ;
        System.out.println("documentTypeId= " + documentTypeId);
    }


    @Test(dependsOnMethods = "createDocumentType")
    public void documentTypeNegative() {

        documentType.put("name", name);
        documentType.put("attachmentStages", attachmentStages);
        documentType.put("schoolId", schoolId);

        given()
                .spec(recSpec)
                .body(documentType)
                //  .log().body()

                .when()
                .post("/school-service/api/attachments/create")

                .then()
                //   .log().body()
                .statusCode(201) //bug

        ;

    }

    @Test(dependsOnMethods = "createDocumentType")
    public void updateDocumentType() {

        name = "Finance";
        documentType.put("id", documentTypeId);
        documentType.put("name", name);
        documentType.put("schoolId", schoolId);
        documentType.put("attachmentStages", attachmentStages);

        given()
                .spec(recSpec)
                .body(documentType)
                //.log().body()

                .when()
                .put("/school-service/api/attachments")

                .then()
                //.log().body()
                .statusCode(200)
                .body("name", equalTo(name))
        ;
    }


    @Test(dependsOnMethods = "updateDocumentType")
    public void deleteDocumentType() {

        given()

                .spec(recSpec)
                .pathParam("documentTypeId", documentTypeId)
                //.log().body()

                .when()
                .delete("/school-service/api/attachments/{documentTypeId}")

                .then()
                //.log().body()
                .statusCode(200)
        ;
    }


    @Test(dependsOnMethods = "deleteDocumentType")
    public void deleteDocumentTypeNeg() {

        given()

                .spec(recSpec)
                .pathParam("documentTypeId", documentTypeId)
                //.log().uri()

                .when()
                .delete("/school-service/api/attachments/{documentTypeId}")

                .then()
                //.log().body()
                .statusCode(400)
                .body("message", equalTo("Attachment Type not found"))
        ;
    }
}
