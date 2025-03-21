package site.nomoreparties.stellarburgers.order;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import site.nomoreparties.stellarburgers.Client;

public class OrderClient  extends Client{
    private static final String ORDER_API_PATH = "/orders";

    @Step("OrderClient - действие, создаем заказ")
    public ValidatableResponse CreateOrderResponse(Order order) {
        return spec()
                .auth().oauth2(order.getAccessToken())
                .body(order.getIngredientsBody())
                .when()
                .post(ORDER_API_PATH)
                .then().log().all();
    }

    @Step("OrderClient - действие, получить список заказов клиента")
    public ValidatableResponse GetOrdersUserResponse(String accessToken) {
        return spec()
                .auth().oauth2(accessToken)
                .when()
                .get(ORDER_API_PATH)
                .then().log().all();
    }
}


