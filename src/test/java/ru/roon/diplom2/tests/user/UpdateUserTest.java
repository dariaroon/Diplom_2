package ru.roon.diplom2.tests.user;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.roon.diplom2.model.User;
import ru.roon.diplom2.step.UserSteps;

import java.util.UUID;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static ru.roon.diplom2.constants.Url.STELLAR_BURGERS_NO_MORE_PARTIES_SITE_URL;

public class UpdateUserTest {
    private static final String TEST_EMAIL = UUID.randomUUID() + "@yandex.ru";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_NAME = "firstName";
    private final UserSteps userSteps = new UserSteps();
    private User user;
    private String accessToken;

    @BeforeClass
    public static void setUp() {
        RestAssured.requestSpecification = new RequestSpecBuilder()
            .setBaseUri(STELLAR_BURGERS_NO_MORE_PARTIES_SITE_URL)
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();
    }

    @Before
    public void createUser() {
        user = new User()
            .setEmail(TEST_EMAIL)
            .setPassword(TEST_PASSWORD)
            .setName(TEST_NAME);
        userSteps.createUser(user);
        accessToken = userSteps.loginUser(user).then()
            .statusCode(200)
            .extract()
            .path("accessToken");
    }

    @Test
    @DisplayName("Изменение email авторизованного пользователя")
    @Description("Проверка изменения email авторизованного пользователя")
    public void testUpdateEmailAuthUser() {
        user.setEmail(UUID.randomUUID() + "@yandex.ru");
        userSteps.updateUser(user, accessToken)
            .then().assertThat()
            .body("success", equalTo(true))
            .and().statusCode(HTTP_OK);
    }

    @Test
    @DisplayName("Изменение имени авторизованного пользователя")
    @Description("Проверка изменения имени авторизованного пользователя")
    public void testUpdateNameAuthUser() {
        user.setName("newName");
        userSteps.updateUser(user, accessToken)
            .then().assertThat()
            .body("success", equalTo(true))
            .and().statusCode(HTTP_OK);
    }

    @Test
    @DisplayName("Изменение email неавторизованного пользователя")
    @Description("Проверка изменения email неавторизованного пользователя")
    public void testUpdateEmailNotAuthUser() {
        user.setEmail(UUID.randomUUID() + "@yandex.ru");
        userSteps.updateUser(user, StringUtils.EMPTY)
            .then()
            .assertThat()
            .statusCode(HTTP_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Изменение имени неавторизованного пользователя")
    @Description("Проверка изменения имени неавторизованного пользователя")
    public void testUpdateNameNotAuthUser() {
        user.setName("newName");
        userSteps.updateUser(user, StringUtils.EMPTY)
            .then()
            .assertThat()
            .statusCode(HTTP_UNAUTHORIZED);
    }

    @After
    public void deleteUser() {
        if (accessToken != null) {
            userSteps.deleteUser(accessToken);
        }
    }
}
