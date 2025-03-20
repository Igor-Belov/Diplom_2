//Создание пользователя:
//создать уникального пользователя;
//создать пользователя, который уже зарегистрирован;
//создать пользователя и не заполнить одно из обязательных полей.

package site.nomoreparties.stellarburgers.user;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@DisplayName("Тесты создания пользователя")
public class CreateUserTest {
    private UserClient userClient;
    private User user;
    private UserCredentials userCredentials;
    private UserChecks check;
    private String accessTokenFirstUser;
    private String accessTokenSecondUser;

    @Before
    public void setUp() {
        user = User.createRandomUser();
        userCredentials = UserCredentials.extractUserCredentials(user);
        check = new UserChecks();
        userClient = new UserClient();
    }

    @After
    public void cleanUp() {
        if (accessTokenFirstUser != null) {
            userClient.deleteUser(accessTokenFirstUser);
        }
        if (accessTokenSecondUser != null) {
            userClient.deleteUser(accessTokenSecondUser);
        }
    }

    @Test
    @DisplayName("Тест - пользователя можно создать, все поля (email, пароль, имя) заполнены")
    public void CreateUserAllFieldsHttpCreated() {
        ValidatableResponse responseCreateUser = userClient.createUser(user);
        accessTokenFirstUser = responseCreateUser.extract().path("accessToken").toString().replace("Bearer ", "");
        check.checkCreateUserOk(responseCreateUser, user);
    }

    @Test
    @DisplayName("Тест - создать пользователя, который уже зарегистрирован (email есть в бд)")
    public void CreateTwoIdenticalUserHttpForbidden() {
        ValidatableResponse responseCreateUser = userClient.createUser(user);
        accessTokenFirstUser = responseCreateUser.extract().path("accessToken").toString().replace("Bearer ", "");
        user.setNewPassword();
        user.setNewName();
        ValidatableResponse responseCreateSameUser = userClient.createUser(user);
        check.checkCreateUserSameUser(responseCreateSameUser);
    }

    @Test
    @DisplayName("Тест - создать пользователя, пароль и имя уже есть у другого пользователя, уникальный только email")
    public void CreateTwoDifferentEmailUserHttpForbidden() {
        ValidatableResponse responseCreateUser = userClient.createUser(user);
        accessTokenFirstUser = responseCreateUser.extract().path("accessToken").toString().replace("Bearer ", "");
        user.setNewEmail();
        ValidatableResponse responseCreateSameUser = userClient.createUser(user);
        accessTokenSecondUser = responseCreateSameUser.extract().path("accessToken").toString().replace("Bearer ", "");
        check.checkCreateUserOk(responseCreateSameUser, user);
    }

    //тесты где не хватает одного из обязательных полей при создании
    @Test
    @DisplayName("Тест - пользователя нельзя создать, не заполнив обязательное поле email")
    public void CreateUserNullEmailHttpForbidden() {
        user.setEmailToNull();
        ValidatableResponse responseCreateUser = userClient.createUser(user);
        check.checkCreateUserBadRequest(responseCreateUser);
    }

    @Test
    @DisplayName("Тест - пользователя нельзя создать, не заполнив обязательное поле password")
    public void CreateUserNullPasswordHttpForbidden() {
        user.setPasswordToNull();
        ValidatableResponse responseCreateUser = userClient.createUser(user);
        check.checkCreateUserBadRequest(responseCreateUser);
    }

    @Test
    @DisplayName("Тест - пользователя нельзя создать, не заполнив обязательное поле name")
    public void CreateUserNullNameHttpForbidden() {
        user.setNameToNull();
        ValidatableResponse responseCreateUser = userClient.createUser(user);
        check.checkCreateUserBadRequest(responseCreateUser);
    }
}