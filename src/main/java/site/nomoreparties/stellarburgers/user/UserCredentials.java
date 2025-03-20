package site.nomoreparties.stellarburgers.user;

import io.qameta.allure.Step;
import java.time.LocalDateTime;

public class UserCredentials {
    private String email;
    private String password;

    public UserCredentials(String email, String password) {
        this.email = email;
        this.password = password;
    }


    @Step("UserCredentials - действие, сохраняем креды (пароль и логин) для созданного пользователя")
    public static UserCredentials extractUserCredentials(User user) {
        return new UserCredentials(user.getEmail(), user.getPassword());
    }

    @Step("UserCredentials - действие, возвращаем логин пользователя")
    public String getEmail() {
        return email;
    }

    @Step("UserCredentials - действие, возвращаем пароль пользователя")
    public String getPassword() {
        return password;
    }

    @Step("UserCredentials - Действие, стираем пароль пользователя из сохраненных кредов")
    public void setPasswordToNull() {
        this.password = null;
    }

    @Step("UserCredentials - Действие, стираем логин пользователя из сохраненных кредов")
    public void setLoginToNull() {
        this.email = null;
    }

    @Step("UserCredentials - Действие, меняем в кредах пользователя пароль")
    public void setBreakPassword() {
        this.password = this.password + LocalDateTime.now();
    }

    @Step("UserCredentials - Действие, меняем регистр пароля на low")
    public void setLowerPassword() {
        this.password = this.password.toLowerCase();
    }

    @Step("UserCredentials - Действие, меняем регистр пароля на Upper")
    public void setUpperPassword() {
        this.password = this.password.toUpperCase();
    }

    @Step("UserCredentials - Действие, меняем в кредах пользователя логин")
    public void setBreakLogin() {
        this.email = this.email + LocalDateTime.now();
    }
}