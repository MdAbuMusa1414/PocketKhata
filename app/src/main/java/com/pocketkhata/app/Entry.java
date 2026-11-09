package com.pocketkhata.app;

import com.google.firebase.database.Exclude;

public class Entry {
  private String id = "",
      title = "",
      type = "Expense",
      category = "Other",
      date = "",
      person = "",
      phone = "",
      note = "",
      photo = "";
  private long amountPaisa, createdAt, updatedAt;
  private boolean settled;

  public Entry() {}

  @Exclude
  public String getId() {
    return id;
  }

  public void setId(String x) {
    id = x;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String x) {
    title = x;
  }

  public String getType() {
    return type;
  }

  public void setType(String x) {
    type = x;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String x) {
    category = x;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String x) {
    date = x;
  }

  public String getPerson() {
    return person;
  }

  public void setPerson(String x) {
    person = x;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String x) {
    phone = x;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String x) {
    note = x;
  }

  public String getPhoto() {
    return photo;
  }

  public void setPhoto(String x) {
    photo = x;
  }

  public long getAmountPaisa() {
    return amountPaisa;
  }

  public void setAmountPaisa(long x) {
    amountPaisa = x;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(long x) {
    createdAt = x;
  }

  public long getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(long x) {
    updatedAt = x;
  }

  public boolean isSettled() {
    return settled;
  }

  public void setSettled(boolean x) {
    settled = x;
  }

  @Exclude
  public boolean isLoan() {
    return "Lent".equals(type) || "Borrowed".equals(type);
  }
}
