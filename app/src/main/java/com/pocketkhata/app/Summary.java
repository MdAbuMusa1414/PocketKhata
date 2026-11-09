package com.pocketkhata.app;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class Summary {
  public long spent, lent, borrowed;

  public static Summary calculate(List<Entry> rows, YearMonth month) {
    Summary s = new Summary();
    for (Entry e : rows) {
      if ("Expense".equals(e.getType())) {
        try {
          if (YearMonth.from(LocalDate.parse(e.getDate())).equals(month))
            s.spent += e.getAmountPaisa();
        } catch (Exception ignored) {
        }
      } else if (!e.isSettled()) {
        if ("Lent".equals(e.getType())) s.lent += e.getAmountPaisa();
        else if ("Borrowed".equals(e.getType())) s.borrowed += e.getAmountPaisa();
      }
    }
    return s;
  }
}
