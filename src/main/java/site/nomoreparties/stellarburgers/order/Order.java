package site.nomoreparties.stellarburgers.order;

import io.qameta.allure.Step;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Order {
    private List<String> ingredients; //Имя заказчика, записывается в поле firstName таблицы Orders
    private String accessToken;

    public Order(List<String> ingredients, String accessToken) {
        this.ingredients = ingredients;
        this.accessToken = accessToken;
    }

    @Step("Order - Действие, создаем заказ")
    public static Order createOrder(List<String> ingredients, String accessToken) {
        return new Order(ingredients, accessToken);
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String setPasswordToNull() {
        return new String();
    }

    public Map<String, List<String>> getIngredientsBody() {
        Map<String, List<String>> body = new HashMap<>();
        body.put("ingredients", this.ingredients);
        System.out.println(ingredients);
        return body;
    }
}
