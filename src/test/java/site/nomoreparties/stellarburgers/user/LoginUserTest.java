//Логин пользователя:
//логин под существующим пользователем,
//логин с неверным логином и паролем.

package site.nomoreparties.stellarburgers.user;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import static java.net.HttpURLConnection.HTTP_OK;

@DisplayName("Тесты LogIn пользователя")
public class LoginUserTest {
    private UserClient userClient;
    private User user;
    private UserCredentials userCredentials;
    private ValidatableResponse responseCreateUser;
    private UserChecks check;
    private String accessTokenFirstUser;

    @Before
    public void set() {
        user = User.createRandomUser();
        userCredentials = UserCredentials.extractUserCredentials(user);
        userClient = new UserClient();
        responseCreateUser = userClient.createUser(user);
        Assume.assumeTrue(responseCreateUser.extract().statusCode() == HTTP_OK); //200
        accessTokenFirstUser = responseCreateUser.extract().path("accessToken");
        check = new UserChecks();
    }

    @After
    public void cleanUp() {
        if (accessTokenFirstUser != null) {
            userClient.deleteUser(accessTokenFirstUser);
        }
    }

    @Test
    @DisplayName("Тест - пользователь может авторизоваться;")
    public void AuthorizationUserHttpOk() {
        ValidatableResponse logIn = userClient.logIn(userCredentials);
        check.checkLogInUserOk(logIn, user);
        accessTokenFirstUser = logIn.extract().path("accessToken");
    }

    @Test
    @DisplayName("Тест - пользователь не может авторизоваться без логина;")
    public void AuthorizationUserWithoutLoginHttpBadRequest() {
        userCredentials.setLoginToNull();
        ValidatableResponse logIn = userClient.logIn(userCredentials);
        check.checkLogInUserBadRequest(logIn);
    }

    @Test
    @DisplayName("Тест - пользователь не может авторизоваться без пароля;")
    public void AuthorizationUserWithoutPasswordHttpBadRequest() {
        userCredentials.setPasswordToNull();
        ValidatableResponse logIn = userClient.logIn(userCredentials);
        check.checkLogInUserBadRequest(logIn);
    }

    @Test
    @DisplayName("Тест - система вернёт ошибку, если неправильно указать логин (или пользователя не существует);")
    public void AuthorizationUserFailLoginHttpNotFound() {
        userCredentials.setBreakLogin();
        ValidatableResponse logIn = userClient.logIn(userCredentials);
        check.checkLogInUserBadRequest(logIn);
    }

    @Test
    @DisplayName("Тест - система вернёт ошибку, если неправильно указать пароль;")
    public void AuthorizationUserFailPasswordHttpNotFound() {
        userCredentials.setBreakPassword();
        ValidatableResponse logIn = userClient.logIn(userCredentials);
        check.checkLogInUserBadRequest(logIn);
    }

    @Test
    @DisplayName("Тест - система вернёт ошибку, если изменим регистр на low;")
    public void AuthorizationUserLowerPasswordHttpNotFound() {
        userCredentials.setLowerPassword();
        ValidatableResponse logIn = userClient.logIn(userCredentials);
        check.checkLogInUserBadRequest(logIn);
    }

    @Test
    @DisplayName("Тест - система вернёт ошибку, если изменим регистр на Upper;")
    public void AuthorizationUserUpperPasswordHttpNotFound() {
        userCredentials.setLowerPassword();
        ValidatableResponse logIn = userClient.logIn(userCredentials);
        check.checkLogInUserBadRequest(logIn);
    }
}
