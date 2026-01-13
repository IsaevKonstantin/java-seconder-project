package ru.otus.java.basic.core.validator;

import org.springframework.stereotype.Component;

@Component
public class PasswordValidator {
    public void validate(String password) {
        if (password == null || password.isBlank())
            throw new IllegalArgumentException("Password is required");
        if (password.length() < 6)
            throw new IllegalArgumentException("Password must be at least 6 characters");
        if (password.matches(".*\\s.*"))
            throw new IllegalArgumentException("Password must not contain spaces");
        if (password.matches(".*[а-яА-ЯёЁ].*"))
            throw new IllegalArgumentException("Password must not contain Cyrillic characters");
        if (!password.matches(".*[A-Z].*"))
            throw new IllegalArgumentException("Password must contain uppercase letter");
        if (!password.matches(".*[a-z].*"))
            throw new IllegalArgumentException("Password must contain lowercase letter");
        if (!password.matches(".*\\d.*"))
            throw new IllegalArgumentException("Password must contain digit");
        if (!password.matches(".*[^\\w\\s].*"))
            throw new IllegalArgumentException("Password must contain special character");
    }
}