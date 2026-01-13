package ru.otus.java.basic.friendship.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.otus.java.basic.core.security.JwtService;
import ru.otus.java.basic.friendship.dto.ContactResponse;
import ru.otus.java.basic.friendship.dto.FriendshipRequest;
import ru.otus.java.basic.friendship.service.FriendshipService;

import java.util.List;

@RestController
@RequestMapping("/friends")
@CrossOrigin(origins = "http://localhost:4200")
public class FriendshipController {
    private final FriendshipService friendshipService;
    private final JwtService jwtService;

    public FriendshipController(FriendshipService friendshipService, JwtService jwtService) {
        this.friendshipService = friendshipService;
        this.jwtService = jwtService;
    }

    @GetMapping("/my")
    public List<ContactResponse> getMyContacts(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.getUserId(token);

        return friendshipService.getMyContacts(userId);
    }

    @GetMapping("/search")
    public List<ContactResponse> searchNewContacts(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String query) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.getUserId(token);

        return friendshipService.searchNewContacts(userId, query);
    }

    @GetMapping("/{id}")
    public ContactResponse searchById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.getUserId(token);

        return friendshipService.searchById(userId, id);
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public ContactResponse addContact(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody FriendshipRequest friendshipRequest) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.getUserId(token);

        return friendshipService.addContact(userId, friendshipRequest.getFriendId());
    }

    @PostMapping("/approve")
    @ResponseStatus(HttpStatus.CREATED)
    public ContactResponse approveContact(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody FriendshipRequest friendshipRequest) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.getUserId(token);

        return friendshipService.approveContact(userId, friendshipRequest.getFriendId());
    }

    @PostMapping("/cancelRequest")
    @ResponseStatus(HttpStatus.CREATED)
    public ContactResponse cancelFriendshipRequest(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody FriendshipRequest friendshipRequest) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.getUserId(token);

        return friendshipService.cancelFriendshipRequest(userId, friendshipRequest.getFriendId());
    }

    @PostMapping("/delete")
    @ResponseStatus(HttpStatus.CREATED)
    public ContactResponse deleteFriend(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody FriendshipRequest friendshipRequest) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.getUserId(token);

        return friendshipService.deleteFriend(userId, friendshipRequest.getFriendId());
    }

    @PostMapping("/block")
    @ResponseStatus(HttpStatus.CREATED)
    public ContactResponse blockContact(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody FriendshipRequest friendshipRequest) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.getUserId(token);

        return friendshipService.blockContact(userId, friendshipRequest.getFriendId());
    }
}
