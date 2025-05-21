package com.mb.transactionbackend.enums;

public enum RoleEnum {
    ROLE_USER,
    ROLE_ADMIN;

    public String getValue() {
        return this.name();
    }
}