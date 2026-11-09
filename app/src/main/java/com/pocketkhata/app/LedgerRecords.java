package com.pocketkhata.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Shared filters; Home shows five recent matches, Records shows the complete history. */
public final class LedgerRecords {
  private LedgerRecords() {}

  public static List<Entry> select(
      List<Entry> entries, boolean allRecords, boolean showPaidLoans, String type, String query) {
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
      if (!allRecords && visible.size() == 5) break;
    }
    return visible;
  }
}
