package site.nomoreparties.stellarburgers.user;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import java.net.HttpURLConnection;

import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class UserChecks {


//Проверки на создание пользака
    @Step("UserChecks - проверка что ответ сервера соответствует успешному созданию пользователя")
    public void checkCreateUserOk(ValidatableResponse createResponse, User user) {
        createResponse.statusCode(HTTP_OK); // 200
        createResponse
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", startsWith("Bearer "))
                .body("refreshToken", notNullValue());
    }

    //В реальном проекте тесты будут написано единообразно. Возможно с проверкой типов DTO. Но тут учебный проект и можно пошалить.
    @Step("UserChecks - проверка если логин, пароль или имя неверные или нет одного из полей")
    public void checkCreateUserBadRequest(ValidatableResponse createResponse) {
        String created = createResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_FORBIDDEN) //403
                .extract()
                .path("message");
        assertEquals("Email, password and name are required fields", created);

        createResponse.body("success", equalTo(false));
    }

    @Step("UserChecks - проверка если такой пользователь уже есть")
    public void checkCreateUserSameUser(ValidatableResponse createResponse) {
        String created = createResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_FORBIDDEN) //403
                .extract()
                .path("message");
        assertEquals("User already exists", created);

        createResponse.body("success", equalTo(false));
    }

//Проверки на логин пользака
    @Step("UserChecks - проверка что ответ сервера соответствует успешному входу (авторизации) пользователя")
    public void checkLogInUserOk(ValidatableResponse logInResponse, User user) {
        logInResponse.statusCode(HTTP_OK); // 200
        logInResponse
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail().toLowerCase())) //Вот тут не понято, что именно должно вернуться. То ли email принадлежащий пользователю, толи логин. Сейчас это одно и тоже, но потом скорее всего логин разрастется.
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", startsWith("Bearer "))
                .body("refreshToken", notNullValue());
    }

    @Step("UserChecks - проверка что ответ сервера соответствует попытке входа с отсутствующими/неверными данными")
    public void checkLogInUserBadRequest(ValidatableResponse logInResponse) {
        logInResponse.statusCode(HTTP_UNAUTHORIZED); // 401
        logInResponse
                .body("success", equalTo(false))
                .body("message", equalTo( "email or password are incorrect"));
    }
}
