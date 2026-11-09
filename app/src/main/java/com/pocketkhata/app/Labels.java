package com.pocketkhata.app;

public final class Labels {
  private Labels() {}

  public static String type(String type) {
    return "Lent".equals(type) ? "Loan Given" : "Borrowed".equals(type) ? "Loan Taken" : "Expense";
  }
}
