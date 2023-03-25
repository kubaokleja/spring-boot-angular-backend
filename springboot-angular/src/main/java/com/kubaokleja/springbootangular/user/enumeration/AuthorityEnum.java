package com.kubaokleja.springbootangular.user.enumeration;

public enum AuthorityEnum {
    USER_READ("user:read"),
    USER_CREATE("user:create"),
    USER_UPDATE("user:update"),
    USER_DELETE("user:delete");

    String name;

    AuthorityEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
