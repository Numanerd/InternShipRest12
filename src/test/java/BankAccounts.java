
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

public class BankAccounts {

    Faker faker = new Faker();
    RequestSpecification recSpec;
    String bankAccountID;
    String bankAccountIban;

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
    public void CreateBankAccount() {

        Map<String, String> accounts = new HashMap<>();
        accounts.put("name", faker.name().fullName());
        bankAccountIban = faker.number().randomDigit() + faker.idNumber().valid();
        accounts.put("iban", bankAccountIban);
        accounts.put("currency", "EUR");
        accounts.put("schoolId", "6390f3207a3bcb6a7ac977f9");

        bankAccountID =
                given()
                        .spec(recSpec)
                        .body(accounts)
                        .log().body()

                        .when()
                        .post("/school-service/api/bank-accounts")

                        .then()
                        .log().all()
                        .statusCode(201)
                        .extract().path("id")
        ;
    }

    @Test(dependsOnMethods = "CreateBankAccount")
    public void CreateBankAccountNegative() {

        Map<String, String> accounts = new HashMap<>();
        accounts.put("name", faker.name().fullName());
        accounts.put("iban", bankAccountIban);
        accounts.put("currency", "EUR");
        accounts.put("schoolId", "6390f3207a3bcb6a7ac977f9");

        given()
                .spec(recSpec)
                .body(accounts)
                .log().body()

                .when()
                .post("/school-service/api/bank-accounts")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already exists"))
        ;
    }

    @Test(dependsOnMethods = "CreateBankAccountNegative")
    public void UpdateBankAccount() {

        Map<String, String> accounts = new HashMap<>();
        accounts.put("id", bankAccountID);

        accounts.put("name", faker.name().fullName());
        accounts.put("iban", bankAccountIban);
        accounts.put("currency", "TRY");
        accounts.put("schoolId", "6390f3207a3bcb6a7ac977f9");

        given()
                .spec(recSpec)
                .body(accounts)

                .when()
                .put("/school-service/api/bank-accounts")

                .then().log().body()
                .statusCode(200)
                .body("iban", equalTo(bankAccountIban))
        ;
    }

    @Test(dependsOnMethods = "UpdateBankAccount")
    public void DeleteBankAccount() {

        given()
                .spec(recSpec)
                .log().uri()

                .when()
                .delete("/school-service/api/bank-accounts/" + bankAccountID)

                .then()
                .log().body()
                .statusCode(200)
        ;
    }

    @Test(dependsOnMethods = "DeleteBankAccount")
    public void DeleteBankAccountNegative() {

        given()
                .spec(recSpec)

                .when()
                .delete("/school-service/api/bank-accounts/" + bankAccountID)

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("Please, bank account must be exist"))
        ;
    }

}
