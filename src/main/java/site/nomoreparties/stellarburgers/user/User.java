package site.nomoreparties.stellarburgers.user;

import io.qameta.allure.Step;

import java.time.LocalDateTime;

public class User {
    private String email;
    private String password;
    private String name;

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Step("User - Действие, создаем пользователя со случайными данными")
    static User createRandomUser() {
        return new User(LocalDateTime.now() + "@autoUser.test", "P@ssword123", "AutoUser");
    }

    @Step("User - Действие, получаем логин(почту) пользователя")
    public String getEmail() {
        return email;
    }

    @Step("User - Действие, получаем пароль пользователя")
    public String getPassword() {
        return password;
    }

    @Step("User - Действие, получаем имя пользователя")
    public String getName() {
        return name;
    }

    @Step("User - Действие, сбрасывает логин(почту) пользователя в null")
    public void setEmailToNull() {
        this.email = null;
    }

    @Step("User - Действие, сбрасываем пароль пользователя в null")
    public void setPasswordToNull() {
        this.password = null;
    }

    @Step("User - Действие, сбрасываем имя пользователя в null")
    public void setNameToNull() {
        this.name = null;
    }

    @Step("User - Действие, устанавливаем пользователю новый пароль")
    public void setNewPassword() {
        this.password = this.password + "_"+  LocalDateTime.now();
    }

    @Step("User - Действие, устанавливаем пользователю новый email")
    public void setNewEmail() {
        this.email = this.email + "_" + LocalDateTime.now();
    }

    @Step("User - Действие, устанавливаем пользователю новое имя")
    public void setNewName() {
        this.name = this.name + "_"+ LocalDateTime.now();
    }
}
