package ru.roon.diplom2.step;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.roon.diplom2.model.User;

import static io.restassured.RestAssured.given;
import static ru.roon.diplom2.constants.Url.USER_DELETE_PATH;
import static ru.roon.diplom2.constants.Url.USER_LOGIN_PATH;
import static ru.roon.diplom2.constants.Url.USER_REGISTER_PATH;
import static ru.roon.diplom2.constants.Url.USER_UPDATE_PATH;

public class UserSteps {

    @Step("Создание пользователя")
    public Response createUser(User request) {
        return given()
            .body(request)
            .when()
            .post(USER_REGISTER_PATH);
    }

    @Step("Логин пользователя")
    public Response loginUser(User user) {
        return given()
            .body(user)
            .when()
            .post(USER_LOGIN_PATH);
    }

    @Step("Удаление пользователя")
    public void deleteUser(String accessToken) {
        given()
            .header("Authorization", accessToken)
            .when()
            .delete(USER_DELETE_PATH);
    }

    @Step("Удаление пользователя")
    public Response updateUser(User user, String accessToken) {
        return given()
            .header("Authorization", accessToken)
            .when()
            .body(user)
            .patch(USER_UPDATE_PATH);
    }
}
