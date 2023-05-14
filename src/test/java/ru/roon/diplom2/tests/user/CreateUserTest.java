package ru.roon.diplom2.tests.user;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.roon.diplom2.model.User;
import ru.roon.diplom2.step.UserSteps;

import java.util.UUID;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.is;
import static ru.roon.diplom2.constants.Url.STELLAR_BURGERS_NO_MORE_PARTIES_SITE_URL;

public class CreateUserTest {
    private static final String TEST_EMAIL = UUID.randomUUID() + "@yandex.ru";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_NAME = "firstName";
    private final UserSteps userSteps = new UserSteps();

    @BeforeClass
    public static void setUp() {
        RestAssured.requestSpecification = new RequestSpecBuilder()
            .setBaseUri(STELLAR_BURGERS_NO_MORE_PARTIES_SITE_URL)
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();
    }

    @Test
    @DisplayName("Проверка создание пользователя")
    @Description("Проверка успешного создания пользователя")
    public void testCreateNewUser() {
        userSteps.createUser(new User()
                .setName(TEST_NAME)
                .setEmail(TEST_EMAIL)
                .setPassword(TEST_PASSWORD))
            .then()
            .assertThat()
            .statusCode(HTTP_OK)
            .and()
            .assertThat()
            .body("success", is(true));
    }

    @Test
    @DisplayName("Регистрация ранее зарегестрированного пользователя")
    @Description("Проверка регистрации уже зарегситрированного пользователя")
    public void testCreateDuplicateUser() {
        User user = new User()
            .setName(TEST_NAME)
            .setEmail(TEST_EMAIL)
            .setPassword(TEST_PASSWORD);
        userSteps.createUser(user);
        userSteps.createUser(user)
            .then()
            .assertThat()
            .statusCode(HTTP_FORBIDDEN)
            .and()
            .assertThat()
            .body("message", is("User already exists"));
    }

    @Test
    @DisplayName("Регистрация пользователя без email")
    @Description("Проверка регистрации пользователя без email")
    public void testCreateUserWithoutEmail() {
        User user = new User()
            .setName(TEST_NAME)
            .setPassword(TEST_PASSWORD);
        userSteps.createUser(user)
            .then()
            .assertThat()
            .statusCode(HTTP_FORBIDDEN)
            .and()
            .assertThat()
            .body("message", is("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Регистрация пользователя без пароля")
    @Description("Проверка регистрации пользователя без пароля")
    public void testCreateUserWithoutPassword() {
        User user = new User()
            .setName(TEST_NAME)
            .setEmail(TEST_EMAIL);
        userSteps.createUser(user)
            .then()
            .assertThat()
            .statusCode(HTTP_FORBIDDEN)
            .and()
            .assertThat()
            .body("message", is("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Регистрация пользователя без имени")
    @Description("Проверка регистрации пользователя без имени")
    public void testCreateUserWithoutName() {
        User user = new User()
            .setEmail(TEST_EMAIL)
            .setPassword(TEST_PASSWORD);
        userSteps.createUser(user)
            .then()
            .assertThat()
            .statusCode(HTTP_FORBIDDEN)
            .and()
            .assertThat()
            .body("message", is("Email, password and name are required fields"));
    }

    @After
    public void deleteUser() {
        User user = new User()
            .setName(TEST_NAME)
            .setEmail(TEST_EMAIL)
            .setPassword(TEST_PASSWORD);
        String accessToken = userSteps.loginUser(user).then().extract().path("accessToken");
        if (accessToken != null) {
            userSteps.deleteUser(accessToken);
        }
    }
}
