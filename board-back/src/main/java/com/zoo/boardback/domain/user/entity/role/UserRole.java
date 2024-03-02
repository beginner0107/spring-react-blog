package com.zoo.boardback.domain.user.entity.role;

import lombok.Getter;

@Getter
public enum UserRole {
    GENERAL_USER("ROLE_USER"), MANAGE_USER("ROLE_ADMIN");

    private final String roleName;

    UserRole(String roleName) {
        this.roleName = roleName;
    }

    public static UserRole findRole(String role) {
        for (UserRole userRole : values()) {
            if (userRole.roleName.equals(role)) {
                return userRole;
            }
        }
        throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
        return roleName;
    }
}
