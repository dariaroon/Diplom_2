package ru.roon.diplom2.tests.order;

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
import ru.roon.diplom2.model.Ingredient;
import ru.roon.diplom2.model.Order;
import ru.roon.diplom2.model.User;
import ru.roon.diplom2.step.IngredientsSteps;
import ru.roon.diplom2.step.OrderSteps;
import ru.roon.diplom2.step.UserSteps;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static ru.roon.diplom2.constants.Url.STELLAR_BURGERS_NO_MORE_PARTIES_SITE_URL;

public class GetOrdersTest {
    private static final String TEST_EMAIL = UUID.randomUUID() + "@yandex.ru";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_NAME = "firstName";
    private final UserSteps userSteps = new UserSteps();
    private final OrderSteps orderSteps = new OrderSteps();
    private final IngredientsSteps ingredientsSteps = new IngredientsSteps();

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
    public void prepareData() {
        List<String> ingredientIds = ingredientsSteps.getIngredients().stream().map(Ingredient::get_id).limit(3).collect(Collectors.toList());
        User user = new User(TEST_EMAIL, TEST_PASSWORD, TEST_NAME);
        userSteps.createUser(user);
        accessToken = userSteps.loginUser(user).then().extract().path("accessToken");
        Order order = new Order(ingredientIds);
        orderSteps.createOrder(order, accessToken);
    }

    @Test
    @DisplayName("Получение списка заказов авторизованного пользователя")
    @Description("Проверка получения списка заказов авторизованного пользователя")
    public void testGetOrderAuthUser() {
        orderSteps.getOrdersAuthUser(accessToken)
            .then()
            .assertThat()
            .statusCode(HTTP_OK);
    }

    @Test
    @DisplayName("Получение списка заказов неавторизованного пользователя")
    @Description("Проверка получение списка заказов неавторизованного пользователя")

    public void testGetOrderNotAuthUser() {
        orderSteps.getOrdersNotAuthUser()
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
