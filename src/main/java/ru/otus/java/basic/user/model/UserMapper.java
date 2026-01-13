package ru.otus.java.basic.user.model;

import ru.otus.java.basic.user.dto.UserResponse;

import java.util.Base64;

public class UserMapper {
    public static UserResponse toResponse(User user, UserProfile profile) {
        UserResponse r = new UserResponse();

        r.setId(user.getId());
        r.setLogin(user.getLogin());
        r.setFirstName(user.getFirstName());
        r.setLastName(user.getLastName());
        r.setPatronymic(user.getPatronymic());
        r.setPhone(user.getPhone());
        r.setEmail(user.getEmail());
        if (profile != null) {
            r.setAbout(profile.getAbout());
            if (profile.getAvatar() != null) {
                r.setAvatarBase64(
                        Base64.getEncoder().encodeToString(profile.getAvatar())
                );
            }
        }

        return r;
    }
}
