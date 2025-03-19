package site.nomoreparties.stellarburgers.user;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import java.net.HttpURLConnection;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class UserChecks {

    @Step("CourierChecks - проверка что ответ сервера соответствует успешному созданию пользователя")
    public void checkCreateUserOk(ValidatableResponse createResponse, User user) {
        // Проверяем статус-код
        createResponse.statusCode(HTTP_OK); // Ожидаем статус-код 200 (OK)

        // Проверяем поля JSON
        createResponse.body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", startsWith("Bearer "))
                .body("refreshToken", notNullValue());
    }

    //В реальном проекте тесты будут написано единообразно. Возможно с проверкой типов DTO. Но тут учебный проект и можно пошалить.
    @Step("CourierChecks - проверка если логин, пароль или имя неверные или нет одного из полей")
    public void checkCreateUserBadRequest(ValidatableResponse createResponse) {
        String created = createResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_FORBIDDEN)
                .extract()
                .path("message");
        assertEquals("Email, password and name are required fields", created);

        createResponse.body("success", equalTo(false));
    }

    @Step("CourierChecks - проверка если такой пользователь уже есть")
    public void checkCreateUserSameUser(ValidatableResponse createResponse) {
        String created = createResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_FORBIDDEN)
                .extract()
                .path("message");
        assertEquals("User already exists", created);

        createResponse.body("success", equalTo(false));
    }
}
