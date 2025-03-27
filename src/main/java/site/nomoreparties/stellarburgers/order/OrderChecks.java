package site.nomoreparties.stellarburgers.order;


import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.*;


public class OrderChecks {
    @Step("OrderChecks - проверка что ответ сервера соответствует успешному созданию заказа")
    public void checkCreateOrderHttpOk(ValidatableResponse CreateOrder) {
        CreateOrder.statusCode(HTTP_OK); // 200
        CreateOrder
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    //Нет описания для данного случая.
    @Step("OrderChecks - проверка что ответ сервера соответствует не авторизированному пользователю")
    public void checkCreateOrderNoAuthHttp(ValidatableResponse CreateOrder) {
        CreateOrder.statusCode(HTTP_FORBIDDEN); // 403
    }

    @Step("OrderChecks - проверка что ответ сервера соответствует невалидный хеш ингредиента")
    public void checkCreateOrderBadHashHttpError(ValidatableResponse CreateOrder) {
        CreateOrder.statusCode(HTTP_INTERNAL_ERROR); // 500
    }


    @Step("OrderChecks - проверка что ответ сервера соответствует пустой список ингридиентов")
    public void checkCreateOrderEmptyListIngridientsHttpBad(ValidatableResponse CreateOrder) {
        CreateOrder.statusCode(HTTP_BAD_REQUEST); // 400
        CreateOrder
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Step("OrderChecks - проверка что ответ сервера соответствует запросу на получение списка заказов клиента, когда заказов не было")
    public void checkEmptyListOrderHttpOk(ValidatableResponse getOrdersListUser) {
        getOrdersListUser.statusCode(HTTP_OK); // 200
        getOrdersListUser
                .body("success", equalTo(true))
                .body("orders", empty())
                .body("total", greaterThanOrEqualTo(0)) // Проверяем, что total >= 0
                .body("totalToday", greaterThanOrEqualTo(0)); // Проверяем, что totalToday >= 0
    }

    @Step("OrderChecks - проверка что ответ сервера соответствует запросу на получение списка заказов клиента, когда заказы были")
    public void checkNoEmptyListOrderHttpOk(ValidatableResponse getOrdersListUser, int countOrders) {
        int maxNumberOfOrdersInResponse = 50;
        getOrdersListUser.statusCode(HTTP_OK); // 200
        getOrdersListUser
                .body("success", equalTo(true))
                .body("orders", everyItem(
                        allOf(
                                hasEntry(equalTo("ingredients"), notNullValue()),
                                hasEntry(equalTo("_id"), notNullValue()),
                                hasEntry(equalTo("status"), notNullValue()),
                                hasEntry(equalTo("name"), notNullValue()),
                                hasEntry(equalTo("number"), notNullValue()),
                                hasEntry(equalTo("createdAt"), notNullValue()),
                                hasEntry(equalTo("updatedAt"), notNullValue())
                        )))
                .body("total", greaterThanOrEqualTo(0)) // Проверяем, что total >= 0
                .body("totalToday", greaterThanOrEqualTo(0)) // Проверяем, что totalToday >= 0
                .body("orders.size()", equalTo((countOrders > maxNumberOfOrdersInResponse) ? maxNumberOfOrdersInResponse : countOrders));
    }

    @Step("OrderChecks - проверка что ответ сервера соответствует запросу на получение списка заказов не авторизованного клиента")
    public void checkListOrderNoAuthUserHttpUnauth(ValidatableResponse getOrdersListUser) {
        getOrdersListUser.statusCode(HTTP_UNAUTHORIZED); // 401
        getOrdersListUser
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
