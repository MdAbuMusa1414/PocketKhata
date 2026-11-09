package com.pocketkhata.app;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthException;

public final class Errors {
  public static String auth(Exception e) {
    if (e instanceof FirebaseNetworkException)
      return "Cannot connect. Check your internet and try again.";
    if (e instanceof FirebaseAuthException) {
      String c = ((FirebaseAuthException) e).getErrorCode();
      switch (c) {
        case "ERROR_EMAIL_ALREADY_IN_USE":
          return "This email already has an account. Please log in.";
        case "ERROR_WEAK_PASSWORD":
          return "Choose a stronger password with at least 8 characters.";
        case "ERROR_INVALID_EMAIL":
          return "Please enter a valid email address.";
        case "ERROR_USER_DISABLED":
          return "This account has been disabled.";
        case "ERROR_TOO_MANY_REQUESTS":
          return "Too many attempts. Please try again later.";
        case "ERROR_OPERATION_NOT_ALLOWED":
          return "Email/password sign-in is not enabled for this project.";
        default:
          return "Could not sign in. Check your email and password, then try again.";
      }
    }
    return "Something went wrong. Please try again.";
  }
}
