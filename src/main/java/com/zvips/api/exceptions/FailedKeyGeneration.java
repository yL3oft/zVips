package com.zvips.api.exceptions;

public class FailedKeyGeneration extends RuntimeException {
  public FailedKeyGeneration(String message) {
    super(message);
  }
}
