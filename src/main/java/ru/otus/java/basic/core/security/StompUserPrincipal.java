package ru.otus.java.basic.core.security;

import java.security.Principal;

public class StompUserPrincipal implements Principal {
    private final String name;

    public StompUserPrincipal(Long userId) {
        this.name = userId.toString();
    }

    @Override
    public String getName() {
        return name;
    }

    // Обязательно реализовать equals и hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof StompUserPrincipal)) return false;

        StompUserPrincipal that = (StompUserPrincipal) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
