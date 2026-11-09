package com.pocketkhata.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Shared search, type and paid-loan filters for the complete Home record list. */
public final class LedgerRecords {
  private LedgerRecords() {}

  public static List<Entry> select(
      List<Entry> entries, boolean showPaidLoans, String type, String query) {
    List<Entry> visible = new ArrayList<>();
    String search = query.toLowerCase(Locale.ROOT).trim();
    for (Entry entry : entries) {
      if (!showPaidLoans && entry.isLoan() && entry.isSettled()) continue;
      if (!"All".equals(type) && !type.equals(entry.getType())) continue;
      String text =
          (entry.getTitle()
                  + " "
                  + entry.getPerson()
                  + " "
                  + entry.getCategory()
                  + " "
                  + entry.getNote())
              .toLowerCase(Locale.ROOT);
      if (!text.contains(search)) continue;
      visible.add(entry);
    }
    return visible;
  }
}
