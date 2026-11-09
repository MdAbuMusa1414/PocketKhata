package com.pocketkhata.app;

/** Private profile metadata. The account email is read directly from FirebaseAuth. */
public final class UserProfile {
  private String name = "", phone = "", photo = "";
  private long updatedAt;

  public UserProfile() {}

  public String getName() {
    return name;
  }

  public void setName(String value) {
    name = value == null ? "" : value;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String value) {
    phone = value == null ? "" : value;
  }

  public String getPhoto() {
    return photo;
  }

  public void setPhoto(String value) {
    photo = value == null ? "" : value;
  }

  public long getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(long value) {
    updatedAt = value;
  }
}
