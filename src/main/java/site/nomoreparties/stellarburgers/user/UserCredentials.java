package site.nomoreparties.stellarburgers.user;

import io.qameta.allure.Step;
import java.time.LocalDateTime;

public class UserCredentials {
    private String login;
    private String password;

    public UserCredentials(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Step("UserCredentials - действие, сохраняем креды (пароль и логин) для созданного пользователя")
    public static UserCredentials extractUserCredentials(User user) {
        return new UserCredentials(user.getEmail(), user.getPassword());
    }

    @Step("UserCredentials - Действие, стираем пароль пользователя из сохраненных кредов")
    public void setPasswordToNull() {
        this.password = null;
    }

    @Step("UserCredentials - Действие, стираем логин пользователя из сохраненных кредов")
    public void setLoginToNull() {
        this.login = null;
    }

    @Step("UserCredentials - Действие, меняем в кредах пользователя пароль")
    public void setBreakPassword() {
        this.password = this.password + LocalDateTime.now();
    }

    @Step("UserCredentials - Действие, меняем в кредах пользователя логин")
    public void setBreakLogin() {
        this.login = this.login + LocalDateTime.now();
    }
}