package site.nomoreparties.stellarburgers.user;

import io.qameta.allure.Step;
import java.time.LocalDateTime;

public class UserData {
    private String email;
    private String name;

    public UserData(String email, String name) {
        this.email = email;
        this.name = name;
    }

    @Step("UserData - действие, сохраняем информацию о пользователе для созданного пользователя")
    public static UserData extractUserData(User user) {
        return new UserData(user.getEmail(), user.getName());
    }

    @Step("UserData - Действие, стираем имя пользователя из сохраненной информации о пользователе")
    public void setNameToNull() {
        this.name = null;
    }

    @Step("UserData - Действие, стираем email пользователя из сохраненной информации о пользователе")
    public void setEmailToNull() {
        this.email = null;
    }

    @Step("UserData - Действие, меняем имя в информации о пользователе")
    public void setNewName() {
        this.name = this.name + "_" +  LocalDateTime.now();
    }

    @Step("UserData - Действие, меняем email в информации о пользователе")
    public void setNewEmail() {
        this.email = this.email + "_" + LocalDateTime.now();
    }

    @Step("UserData - Действие, меняем email в информации о пользователе на конкретный")
    public void setNewEmail(String email) {
        this.email = email;
    }

    @Step("UserData - Действие, получаем email из информации о пользователе")
    public String getEmail() {
        return email;
    }

    @Step("UserData - Действие, получаем name из информации о пользователе")
    public String getName() {
        return name;
    }
}