package site.nomoreparties.stellarburgers.user;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import java.net.HttpURLConnection;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;

public class UserChecks {

    @Step("CourierChecks - проверка что ответ сервера соответствует успешному созданию курьера")
    public void checkCreateUser(ValidatableResponse createResponse, User user) {
        // Проверяем статус-код
        createResponse.statusCode(HTTP_OK); // Ожидаем статус-код 200 (OK)

        // Проверяем поля JSON
        createResponse.body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", startsWith("Bearer "))
                .body("refreshToken", notNullValue());
    }
}
