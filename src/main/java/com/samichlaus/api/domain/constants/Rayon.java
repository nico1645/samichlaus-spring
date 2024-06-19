package com.samichlaus.api.domain.constants;

import lombok.Getter;

@Getter
public enum Rayon {
  RAYON1(1),
  RAYON2(2),
  RAYON3(3);

  private final int value;

  Rayon(int value) {
    this.value = value;
  }

  public static Rayon fromValue(int value) {
    for (Rayon rayon : Rayon.values()) {
      if (rayon.value == value) {
        return rayon;
      }
    }
    throw new IllegalArgumentException("Invalid Rayon value: " + value);
  }
}
