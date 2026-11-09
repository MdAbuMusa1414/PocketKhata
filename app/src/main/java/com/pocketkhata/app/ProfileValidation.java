package com.pocketkhata.app;

public final class ProfileValidation {
  private ProfileValidation() {}

  public static String nameError(String value) {
    if (value == null || value.trim().length() < 2) return "Enter your full name";
    if (value.trim().length() > 60) return "Use 60 characters or fewer";
    return null;
  }

  public static String normalizePhone(String value) {
    return value == null ? "" : value.replaceAll("[ ()-]", "").trim();
  }

  public static String phoneError(String value) {
    String phone = normalizePhone(value);
    if (!phone.isEmpty() && !phone.matches("\\+?[0-9]{7,15}")) return "Enter a valid phone number";
    return null;
  }
}
