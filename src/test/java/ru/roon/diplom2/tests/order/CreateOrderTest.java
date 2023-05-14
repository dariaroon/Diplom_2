package ru.roon.diplom2.tests.order;

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
import ru.roon.diplom2.model.Ingredient;
import ru.roon.diplom2.model.Order;
import ru.roon.diplom2.model.User;
import ru.roon.diplom2.step.IngredientsSteps;
import ru.roon.diplom2.step.OrderSteps;
import ru.roon.diplom2.step.UserSteps;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;
import static ru.roon.diplom2.constants.Url.STELLAR_BURGERS_NO_MORE_PARTIES_SITE_URL;

public class CreateOrderTest {
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
        User user = new User(TEST_EMAIL, TEST_PASSWORD, TEST_NAME);
        userSteps.createUser(user);
        accessToken = userSteps.loginUser(user).then()
            .statusCode(200)
            .extract()
            .path("accessToken");

    }

    @Test
    @DisplayName("Создание заказа авторизованным пользователем")
    @Description("Проверка создания заказа авторизованным пользователем")
    public void testCreateOrderAuthUser() {
        List<Ingredient> ingredients = ingredientsSteps.getIngredients();
        List<String> ingredientsIds = ingredients.stream()
            .map(Ingredient::get_id)
            .limit(5)
            .collect(Collectors.toList());
        Order order = new Order(ingredientsIds);
        orderSteps.createOrder(order, accessToken)
            .then()
            .assertThat()
            .statusCode(HTTP_OK);
    }

    @Test
    @DisplayName("Создание заказа неавторизованным пользователем")
    @Description("Проверка создания заказа неавторизованным пользователем")
    public void testCreateOrderNotAuthUser() {
        List<String> ingredientsIds = ingredientsSteps.getIngredients()
            .stream()
            .map(Ingredient::get_id)
            .limit(5)
            .collect(Collectors.toList());
        Order order = new Order(ingredientsIds);
        orderSteps.createOrder(order, StringUtils.EMPTY)
            .then().assertThat().statusCode(HTTP_OK);
    }

    @Test
    @DisplayName("Создание заказа авторизованным пользователем без ингредиентов")
    @Description("Проверка создания заказа авторизованным пользователем без ингридиентов")
    public void testCreateOrderNoIngredients() {
        Order order = new Order();
        orderSteps.createOrder(order, accessToken)
            .then()
            .assertThat()
            .statusCode(HTTP_BAD_REQUEST);
    }

    @Test
    @DisplayName("Создание заказа c неверным хешем ингредиентов")
    @Description("Проверка создания заказа c неверным хешем ингредиентов")
    public void testCreateOrderWithWrongHash() {
        List<String> ingredientsIds = List.of("incorrect");
        Order order = new Order(ingredientsIds);
        orderSteps.createOrder(order, accessToken)
            .then()
            .assertThat()
            .statusCode(HTTP_INTERNAL_ERROR);
    }

    @After
    public void deleteData() {
        if (accessToken != null) {
            userSteps.deleteUser(accessToken);
        }
    }
}
