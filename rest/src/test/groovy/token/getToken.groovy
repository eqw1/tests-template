package token

import config.TestConfig
import config.TestConfigProvider
import dao.DbDAO
import modelRest.token.ApiToken
import module.Dbi
import module.RestAssuredInit
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Guice
import org.testng.annotations.Test
import ru.yandex.qatools.allure.annotations.Features
import ru.yandex.qatools.allure.annotations.Stories
import ru.yandex.qatools.allure.annotations.Title
import testrail.TestRailListener
import javax.inject.Inject

import static io.restassured.RestAssured.given
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo


@Guice(modules = [TestConfigProvider.class, Dbi.class])
@Features("OAPI")
@Stories("Получение токена")
@TestRailListener.SectionNames(sectionNames = ["Получение токена", "getToken /api/token"])
class getToken {

    @Inject
    DbDAO dbDao

    @Inject
    TestConfig cfg

    private static final String basePath = "/token"

    @BeforeMethod(alwaysRun = true)
    void beforeMethod() {
        RestAssuredInit.jsonRestAssured()
    }

    @TestRailListener.StepsDescription(
            step = [@TestRailListener.Step(content = "шаг 1", expected = "результат 1"),
                    @TestRailListener.Step(content = "шаг 2", expected = "результат 2")])
    @Title("Получение токена")
    @Test(groups = ["regress", "smoke", "rest"])
    void getTokenTest() {
//        Map<String, String> body = new HashMap<>()
//        body.put("login", cfg.login())
//        body.put("password", cfg.password())
        ApiToken response = given()
            .queryParam("login", cfg.login())
            .queryParam("password", cfg.login())
            .when()
//            .body(body)
            .get(basePath)
            .then().log().all()
            .statusCode(200).extract().as(ApiToken.class)

        ApiToken db = dbDao.getUserTokens(cfg.login()).get(0)

        assertThat("Вернувшийся токен отличается от последнего токена в базе"
                , response.authtoken
                , equalTo(db.authtoken))
    }
}
