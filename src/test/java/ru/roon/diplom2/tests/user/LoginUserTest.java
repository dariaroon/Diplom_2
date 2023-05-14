package ru.roon.diplom2.tests.user;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.roon.diplom2.model.User;
import ru.roon.diplom2.step.UserSteps;

import java.util.UUID;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static ru.roon.diplom2.constants.Url.STELLAR_BURGERS_NO_MORE_PARTIES_SITE_URL;

public class LoginUserTest {

    private static final String TEST_EMAIL = UUID.randomUUID() + "@yandex.ru";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_NAME = "firstName";
    private final UserSteps userSteps = new UserSteps();
    private User user;

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
    }

    @Test
    @DisplayName("Проверка авторизации пользователя")
    @Description("Проверка успешной авторизации пользователя")
    public void testLoginUser() {
        userSteps.loginUser(user)
            .then()
            .assertThat()
            .statusCode(HTTP_OK);
    }

    @Test
    @DisplayName("Авторизация с некорректным email")
    @Description("Проверка авторизации пользователя c некорректным email")
    public void testLoginUserIncorrectEmail() {
        User user = new User()
            .setEmail("incorrect@yandex")
            .setPassword(TEST_PASSWORD)
            .setName(TEST_NAME);
        userSteps.loginUser(user)
            .then()
            .assertThat()
            .statusCode(HTTP_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Авторизация с некорректным паролем")
    @Description("Проверка авторизации пользователя c некорректным паролем")
    public void testLoginUserIncorrectPassword() {
        User user = new User()
            .setEmail(TEST_EMAIL)
            .setPassword("incorrect")
            .setName(TEST_NAME);
        userSteps.loginUser(user)
            .then()
            .assertThat()
            .statusCode(HTTP_UNAUTHORIZED);
    }

    @After
    public void deleteUser() {
        String accessToken = userSteps.loginUser(user).then()
            .statusCode(200)
            .extract()
            .path("accessToken");
        if (accessToken != null) {
            userSteps.deleteUser(accessToken);
        }
    }
}
