//Изменение данных пользователя:
//с авторизацией,
//без авторизации,
//Для обеих ситуаций нужно проверить, что любое поле можно изменить. Для неавторизованного пользователя — ещё и то, что система вернёт ошибку.

package site.nomoreparties.stellarburgers.user;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import static java.net.HttpURLConnection.HTTP_OK;

@DisplayName("Тесты смены информации о пользователе")
public class ChangeDataUserTest {
        private UserClient userClient;
        private User user;
        private UserCredentials userCredentials;
        private UserData userData;
        private ValidatableResponse responseCreateUser;
        private UserChecks check;
        private String accessToken;

        @Before
        public void set() {
            user = User.createRandomUser();
            userCredentials = UserCredentials.extractUserCredentials(user);
            userData = UserData.extractUserData(user);
            userClient = new UserClient();
            responseCreateUser = userClient.createUser(user);
            Assume.assumeTrue(responseCreateUser.extract().statusCode() == HTTP_OK); //200
            accessToken = responseCreateUser.extract().path("accessToken").toString().replace("Bearer ", "");
            check = new UserChecks();
        }

        @After
        public void cleanUp() {
            if (accessToken != null) {
                userClient.deleteUser(accessToken);
            }
        }

    @Test
    @DisplayName("Тест - авторизированный пользователь может поменять все поля (email и name);")
    public void authorizationUserChangeEmailAndNameHttpOk() {
        ValidatableResponse logIn = userClient.logIn(userCredentials);
        accessToken = logIn.extract().path("accessToken").toString().replace("Bearer ", "");
        userData.setNewEmail();
        userData.setNewName();
        ValidatableResponse changeUserData = userClient.changeDataUser(accessToken, userData);
        check.checkChangeUserDataHttpOk(changeUserData, userData);
        ValidatableResponse getDataUser = userClient.getDataUser(accessToken);
        check.checkUserDataChanged(getDataUser, userData);
    }

    @Test
    @DisplayName("Тест - авторизированный пользователь отправил те же данные, что и были;")
    public void authorizationUserNoChangeEmailAndNameHttpOk() {
        ValidatableResponse logIn = userClient.logIn(userCredentials);
        accessToken = logIn.extract().path("accessToken").toString().replace("Bearer ", "");
        ValidatableResponse changeUserData = userClient.changeDataUser(accessToken, userData);
        check.checkChangeUserDataHttpOk(changeUserData, userData);
        ValidatableResponse getDataUser = userClient.getDataUser(accessToken);
        check.checkUserDataChanged(getDataUser, userData);
    }

    @Test
    @DisplayName("Тест - авторизированный пользователь не может сделать email пустым. Ответ не определен. Проверяем только БД;")
    public void authorizationUserChangeEmailEmptyNoChangeDB() {
        ValidatableResponse logIn = userClient.logIn(userCredentials);
        accessToken = logIn.extract().path("accessToken").toString().replace("Bearer ", "");
        userData.setEmailToNull();
        ValidatableResponse changeUserData = userClient.changeDataUser(accessToken, userData);
        ValidatableResponse getDataUser = userClient.getDataUser(accessToken);
        check.checkUserDataNoChanged(getDataUser, user);
    }

    @Test
    @DisplayName("Тест - авторизированный пользователь не может сделать name пустым. Ответ не определен. Проверяем только БД;")
    public void authorizationUserChangeNameEmptyNoChangeDB() {
        ValidatableResponse logIn = userClient.logIn(userCredentials);
        accessToken = logIn.extract().path("accessToken").toString().replace("Bearer ", "");
        userData.setNameToNull();
        ValidatableResponse changeUserData = userClient.changeDataUser(accessToken, userData);
        ValidatableResponse getDataUser = userClient.getDataUser(accessToken);
        check.checkUserDataNoChanged(getDataUser, user);
    }

    @Test
    @DisplayName("Тест - авторизированный пользователь отправил занятый email;")
    public void authorizationUserChangeEmailOccupiedHttpForbidden() {
        User userFirstUser = user; //прихраним данные уже созданного в Before пользака.
        String accessTokenFirstUser = accessToken;

        User userSecondUser = User.createRandomUser();//Заведем второго юзера
        userSecondUser.setNewName();
        userCredentials = UserCredentials.extractUserCredentials(userSecondUser);
        userData = UserData.extractUserData(userSecondUser);

        responseCreateUser = userClient.createUser(userSecondUser);//Создадим ему учетку и авторизируемся
        ValidatableResponse logIn = userClient.logIn(userCredentials);
        accessToken = logIn.extract().path("accessToken").toString().replace("Bearer ", "");
        userData.setNewEmail(userFirstUser.getEmail());
        ValidatableResponse changeUserData = userClient.changeDataUser(accessToken, userData);
        check.checkChangeUserEmailOccupiedHttpForb(changeUserData);
        ValidatableResponse getDataFirstUser = userClient.getDataUser(accessTokenFirstUser); //На всякий случай проверим что в БД данные первого пользователя не перетерлись.
        check.checkUserDataNoChanged(getDataFirstUser, userFirstUser);
        ValidatableResponse getDataSecondUser = userClient.getDataUser(accessToken); //Проверим что в БД данные второго пользователя действительно не поменялись
        check.checkUserDataNoChanged(getDataSecondUser, userSecondUser);
        userClient.deleteUser(accessTokenFirstUser); //удалим первого пользователя
    }

    //Тесты с изменение данных пользователя без регистрации
    //Тут возможно есть баг. Пользователь не авторизован, но у нас есть токен после регистрации. И он работает для изменения данных
    @Test
    @DisplayName("Тест - не авторизированный пользователь (токен после регистрации) не может изменить email")
    public void noAuthorizationUserChangeEmailHttpForbidden() {
        userData.setNewEmail();
        ValidatableResponse changeUserData = userClient.changeDataUser(accessToken, userData);
        check.checkChangeUserDataWithoutAuthHttpUnauth(changeUserData);
        ValidatableResponse getDataUser = userClient.getDataUser(accessToken);
        check.checkUserDataNoChanged(getDataUser, user);
    }

    @Test
    @DisplayName("Тест - не авторизированный пользователь (токен после регистрации) не может изменить name")
    public void noAuthorizationUserChangeNameHttpForbidden() {
        userData.setNewName();
        ValidatableResponse changeUserData = userClient.changeDataUser(accessToken, userData);
        check.checkChangeUserDataWithoutAuthHttpUnauth(changeUserData);
        ValidatableResponse getDataUser = userClient.getDataUser(accessToken);
        check.checkUserDataNoChanged(getDataUser, user);
    }

    @Test
    @DisplayName("Тест - не авторизированный пользователь (токен отсутствует) не может изменить email")
    public void NoAuthorizationWithoutTokenUserChangeEmailHttpForbidden() {
        userData.setNewEmail();
        ValidatableResponse changeUserData = userClient.changeDataUser(new String(), userData);
        check.checkChangeUserDataWithoutAuthHttpUnauth(changeUserData);
        ValidatableResponse getDataUser = userClient.getDataUser(accessToken);
        check.checkUserDataNoChanged(getDataUser, user);
    }

    @Test
    @DisplayName("Тест - не авторизированный пользователь не может изменить name")
    public void NoAuthorizationWithoutTokenUserChangeNameHttpForbidden() {
        userData.setNewName();
        ValidatableResponse changeUserData = userClient.changeDataUser(new String(), userData);
        check.checkChangeUserDataWithoutAuthHttpUnauth(changeUserData);
        ValidatableResponse getDataUser = userClient.getDataUser(accessToken);
        check.checkUserDataNoChanged(getDataUser, user);
    }

    //Отсутвует документация. По факту 403 jwt expired
    @Test
    @DisplayName("Тест - не авторизированный пользователь (просроченный токен) не может изменить email")
    public void NoAuthorizationOldTokenUserChangeEmailHttpForbidden() {
        userData.setNewEmail();
        ValidatableResponse changeUserData = userClient.changeDataUser("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY3ZGM5MGVkOWVkMjgwMDAxYjVhYjFlYSIsImlhdCI6MTc0MjUwODI2OSwiZXhwIjoxNzQyNTA5NDY5fQ.cVVY7PA41YZ-zwt1lTK1AN4sxBBo8dtGR64bl_Y58w8", userData);
        check.checkChangeUserDataWithoutAuthHttpUnauth(changeUserData);
    }

    //Отсутвует документация. По факту 403 jwt expired
    @Test
    @DisplayName("Тест - не авторизированный пользователь (просроченный токен)  не может изменить name")
    public void NoAuthorizationOldTokenUserChangeNameHttpForbidden() {
        userData.setNewName();
        ValidatableResponse changeUserData = userClient.changeDataUser("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY3ZGM5MGVkOWVkMjgwMDAxYjVhYjFlYSIsImlhdCI6MTc0MjUwODI2OSwiZXhwIjoxNzQyNTA5NDY5fQ.cVVY7PA41YZ-zwt1lTK1AN4sxBBo8dtGR64bl_Y58w8", userData);
        check.checkChangeUserDataWithoutAuthHttpUnauth(changeUserData);
    }
    //Еще можно ломать токен руками в разных частях и получать разные сообщения. Например - 403 "Unexpected token ; in JSON at position 5" или 403 "invalid signature"

}


