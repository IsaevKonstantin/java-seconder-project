package ru.otus.java.basic.friendship.service;

import org.springframework.stereotype.Service;
import ru.otus.java.basic.friendship.dao.FriendshipDao;
import ru.otus.java.basic.friendship.dto.ContactResponse;

import java.util.List;

@Service
public class FriendshipService {
    private final FriendshipDao friendshipDao;

    public FriendshipService(FriendshipDao friendshipDao) {
        this.friendshipDao = friendshipDao;
    }

    public List<ContactResponse> getMyContacts(Long userId) {
        return friendshipDao.getMyContacts(userId);
    }

    public List<ContactResponse> searchNewContacts(Long userId, String query) {
        return friendshipDao.searchNewContacts(userId, query);
    }

    public ContactResponse addContact(Long userId, Long contactId) {
        return friendshipDao.addContact(userId, contactId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user data"));
    }

    public ContactResponse approveContact(Long userId, Long contactId) {
        return friendshipDao.approveContact(userId, contactId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user data"));
    }

    public ContactResponse cancelFriendshipRequest(Long userId, Long contactId) {
        return friendshipDao.cancelFriendshipReq(userId, contactId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user data"));
    }

    public ContactResponse deleteFriend(Long userId, Long contactId) {
        return friendshipDao.deleteFriend(userId, contactId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user data"));
    }

    public ContactResponse blockContact(Long userId, Long contactId) {
        return friendshipDao.blockContact(userId, contactId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user data"));
    }

    public boolean areFriends(Long userId, Long friendId) {
        return friendshipDao.areFriends(userId, friendId);
    }
}
