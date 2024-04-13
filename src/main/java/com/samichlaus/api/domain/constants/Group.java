package com.samichlaus.api.domain.constants;

public enum Group {
    A, B, C, D, E, F, G, H, Z;

    public static Group fromValue(String value) {
        for (Group group : Group.values()) {
            if (group.toString().equals(value)) {
                return group;
            }
        }
        throw new IllegalArgumentException("Invalid Group value: " + value);
    }
}
