package ru.roon.diplom2.step;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.roon.diplom2.model.Order;

import static io.restassured.RestAssured.given;
import static ru.roon.diplom2.constants.Url.ORDERS_PATH;

public class OrderSteps {
    @Step("Создание заказа")
    public Response createOrder(Order order, String accessToken) {
        return given()
            .header("Authorization", accessToken)
            .body(order)
            .when()
            .post(ORDERS_PATH);
    }

    @Step("Получение заказов авторизованного пользователя")
    public Response getOrdersAuthUser(String accessToken) {
        return given()
            .header("Authorization", accessToken)
            .get(ORDERS_PATH);
    }

    @Step("Получение заказов неавторизованного пользователя")
    public Response getOrdersNotAuthUser() {
        return given()
            .get(ORDERS_PATH);
    }
}
