package com.Santino.Usuario.exception;

public class UserAlreadyExists extends RuntimeException {

  public UserAlreadyExists(String message) {
    super(message);
  }

}
