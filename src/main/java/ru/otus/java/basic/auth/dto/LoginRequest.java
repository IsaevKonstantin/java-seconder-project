package ru.otus.java.basic.auth.dto;

import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank
    private String login;
    @NotBlank
    private String password;

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
