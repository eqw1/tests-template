package module

import config.TestConfig
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.config.DecoderConfig
import io.restassured.config.EncoderConfig
import io.restassured.config.ObjectMapperConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.filter.log.LogDetail
import io.restassured.http.ContentType
import io.restassured.mapper.ObjectMapperType
import io.restassured.specification.RequestSpecification
import modelRest.token.ApiToken
import org.aeonbits.owner.ConfigFactory

import static io.restassured.RestAssured.given

class RestAssuredInit {

    private static TestConfig cfg = ConfigFactory.create(TestConfig.class)

    static RequestSpecification tokenRestAssured() {
        RestAssured.reset()

        RequestSpecification requestSpec = new RequestSpecBuilder()
                .setBaseUri(cfg.restUrl())
                .setAccept(ContentType.XML)
                .setContentType(ContentType.XML)
                .log(LogDetail.ALL)
                .build()
        RestAssured.requestSpecification = requestSpec
        RestAssured.config = RestAssuredConfig.config()
                .decoderConfig(new DecoderConfig("UTF-8"))
                .encoderConfig(new EncoderConfig("UTF-8", "UTF-8"))
                .objectMapperConfig(new ObjectMapperConfig(ObjectMapperType.JAXB))
        return  requestSpec
    }

    static void jsonRestAssured() {
        RestAssured.reset()
//        ApiToken response = given().spec(tokenRestAssured())
//                .queryParam("login", cfg.login())
//                .queryParam("password", cfg.password())
//                .when()
//                .get("/tokens-stub") // Url set in TestConfig - restUrl
//                .then().log().all()
//                .statusCode(200).extract().body().as(ApiToken.class)
        RestAssured.reset()
        RequestSpecification requestSpec = new RequestSpecBuilder()
                .setBaseUri(cfg.restUrl() + "/api")
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
//                .addHeader("authtoken", response.authtoken)
                .build()

        RestAssured.requestSpecification = requestSpec
        RestAssured.config = RestAssuredConfig.config()
                .decoderConfig(new DecoderConfig("UTF-8"))
                .encoderConfig(new EncoderConfig("UTF-8", "UTF-8"))
                .objectMapperConfig(new ObjectMapperConfig(ObjectMapperType.JACKSON_2))
    }
}
