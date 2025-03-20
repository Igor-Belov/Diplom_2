package site.nomoreparties.stellarburgers.user;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import site.nomoreparties.stellarburgers.Client;

import java.util.Map;

public class UserClient extends Client {
    private static final String USER_API_PATH = "/auth";

    @Step("UserClient - действие, заходим пользователем в личный кабинет")
    public ValidatableResponse logIn(UserCredentials creds) {
        return spec()
                .body(creds)
                .when()
                .post(USER_API_PATH + "/login")
                .then().log().all();
    }

    @Step("UserClient - действие, выходим пользователем из личный кабинет")
    public ValidatableResponse logOut(String creds) {
        return spec()
                .body(creds)
                .when()
                .post(USER_API_PATH + "/login")
                .then().log().all();
    }

    @Step("UserClient - действие, запрос на создание пользователя")
    public ValidatableResponse createUser(User user) {
        return spec()
                .body(user)
                .when()
                .post(USER_API_PATH + "/register")
                .then().log().all();
    }

    @Step("UserClient - действие, запрос на удаление пользователя")
    public ValidatableResponse deleteUser(String accessToken) {
        return spec()
                .auth().oauth2(accessToken)
                .when()
                .delete(USER_API_PATH + "/user")
                .then().log().all();
    }

    @Step("UserClient - действие, запрос на изменение информации о пользователе")
    public ValidatableResponse changeDataUser(String AccessToken, UserData userData) {
        return spec()
                .auth().oauth2(AccessToken)
                .body(userData)
                .when()
                .patch(USER_API_PATH + "/user")
                .then().log().all();
    }

    @Step("UserClient - действие, получение информации о пользователе")
    public ValidatableResponse getDataUser(String AccessToken) {
        return spec()
                .auth().oauth2(AccessToken)
                .when()
                .get(USER_API_PATH + "/user")
                .then().log().all();
    }
}
