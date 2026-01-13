package ru.otus.java.basic.auth.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.otus.java.basic.auth.dao.AuthDao;
import ru.otus.java.basic.auth.dto.TokenResponse;
import ru.otus.java.basic.auth.dto.LoginRequest;
import ru.otus.java.basic.auth.dto.RegisterRequest;
import ru.otus.java.basic.core.validator.PasswordValidator;
import ru.otus.java.basic.core.security.JwtService;
import ru.otus.java.basic.core.exceptions.InvalidCredentialsException;
import ru.otus.java.basic.core.exceptions.LoginAlreadyExistsException;
import ru.otus.java.basic.user.model.User;


@Service
public class AuthService {
    private final AuthDao authDao = new AuthDao();
    private final PasswordValidator passwordValidator = new PasswordValidator();
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtService jwtService;

    public AuthService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public TokenResponse register(RegisterRequest request) {
        passwordValidator.validate(request.getPassword());

        if (authDao.existsByLogin(request.getLogin()))
            throw new LoginAlreadyExistsException("Login already exists");

        User user = new User();

        user.setLogin(request.getLogin());
        user.setPasswordHash(encoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPatronymic(request.getPatronymic());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());

        authDao.save(user);
        authDao.saveProfileIfNotExists(user.getId());

        return new TokenResponse(jwtService.generateToken(user));
    }

    public TokenResponse login(LoginRequest request) {
        User user = authDao.findByLogin(request.getLogin())
                .orElseThrow(InvalidCredentialsException::new);

        if (!encoder.matches(request.getPassword(), user.getPasswordHash()))
            throw new InvalidCredentialsException();

        return new TokenResponse(jwtService.generateToken(user));
    }
}
