package ru.otus.java.basic.user.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.otus.java.basic.user.dao.UserProfileDao;
import ru.otus.java.basic.user.dto.UserResponse;
import ru.otus.java.basic.user.dto.UserUpdateRequest;
import ru.otus.java.basic.user.dao.UserDao;
import ru.otus.java.basic.user.model.User;
import ru.otus.java.basic.user.model.UserMapper;
import ru.otus.java.basic.user.model.UserProfile;
import ru.otus.java.basic.core.validator.PasswordValidator;
import ru.otus.java.basic.core.exceptions.InvalidCredentialsException;
import ru.otus.java.basic.core.exceptions.LoginAlreadyExistsException;

import java.util.Base64;

@Service
public class UserService {
    private final UserDao userDao = new UserDao();
    private final UserProfileDao profileDao = new UserProfileDao();
    private final PasswordValidator passwordValidator = new PasswordValidator();
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserResponse getUserById(Long id) {
        User user = userDao.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid user data"));

        UserProfile profile = profileDao.findByUserId(user.getId()).orElse(null);

        return UserMapper.toResponse(user, profile);
    }

    public UserResponse updateUserData(Long id, UserUpdateRequest request) {
        int updCount = 0;

        User user = userDao.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid user data"));

        if (!request.getLogin().equals(user.getLogin())) {
            if (userDao.existsByLogin(request.getLogin())) {
                throw new LoginAlreadyExistsException("Login already exists");
            }
        }

        user.setLogin(request.getLogin());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPatronymic(request.getPatronymic());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        updCount += userDao.update(user);

        UserProfile profile = profileDao.findByUserId(user.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid user data"));

        profile.setAbout(request.getAbout());
        profile.setAvatar(
                request.getAvatarBase64() == null
                        ? null
                        : Base64.getDecoder().decode(request.getAvatarBase64())
        );
        updCount += profileDao.update(profile);

        if (request.getNewPassword() != null && request.getCurrentPassword() != null) {
            if (!encoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
                throw new InvalidCredentialsException();
            }
            passwordValidator.validate(request.getNewPassword());
            user.setPasswordHash(encoder.encode(request.getNewPassword()));
            updCount += userDao.updatePassword(user);
        }

        if (updCount == 0)
            throw new IllegalArgumentException("User data not update");

        return UserMapper.toResponse(user, profile);
    }
}
