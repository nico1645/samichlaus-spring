package com.samichlaus.api.domain.constants;

import lombok.Getter;

@Getter
public enum Version {
    PRD(0), TST(1);
    private final int value;

    Version(int value) {
        this.value = value;
    }

    public static Version fromValue(String value) {
        for (Version tourVersion : Version.values()) {
            if (tourVersion.toString().equals(value)) {
                return tourVersion;
            }
        }
        throw new IllegalArgumentException("Invalid Rayon value: " + value);
    }
}
