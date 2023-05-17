package ru.roon.diplom2.step;

import io.qameta.allure.Step;
import ru.roon.diplom2.model.Ingredient;

import java.util.List;

import static io.restassured.RestAssured.given;
import static ru.roon.diplom2.constants.Url.INGREDIENTS_PATH;

public class IngredientsSteps {
    @Step("Получение информации об ингредиентах")
    public List<Ingredient> getIngredients() {
        return given()
            .get(INGREDIENTS_PATH)
            .jsonPath()
            .getList("data", Ingredient.class);
    }
}
