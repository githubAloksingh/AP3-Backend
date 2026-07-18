package com.banking.entity;

public enum Role {
    ROLE_ADMIN,
    ROLE_CUSTOMER;

    public String getAuthority() {
        return name();
    }
}
