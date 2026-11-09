package com.pocketkhata.app;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

/** Monthly expenses only; loans do not inflate spending or category totals. */
public final class MonthlyBreakdown {
  public long total, average;
  public int count;
  public final List<Category> categories = new ArrayList<>();

  public static final class Category {
    public final String name;
    public final long amount;

    Category(String name, long amount) {
      this.name = name;
      this.amount = amount;
    }
  }

  public static MonthlyBreakdown calculate(List<Entry> rows, YearMonth month, LocalDate today) {
    MonthlyBreakdown result = new MonthlyBreakdown();
    Map<String, Long> totals = new HashMap<>();
    for (Entry entry : rows) {
      if (!"Expense".equals(entry.getType())) continue;
      try {
        if (!YearMonth.from(LocalDate.parse(entry.getDate())).equals(month)) continue;
      } catch (Exception invalidDate) {
        continue;
      }
      result.total += entry.getAmountPaisa();
      result.count++;
      String category = entry.getCategory().trim().isEmpty() ? "Other" : entry.getCategory();
      totals.put(category, totals.getOrDefault(category, 0L) + entry.getAmountPaisa());
    }
    int days = month.equals(YearMonth.from(today)) ? today.getDayOfMonth() : month.lengthOfMonth();
    result.average =
        BigDecimal.valueOf(result.total)
            .divide(BigDecimal.valueOf(days), 0, RoundingMode.HALF_UP)
            .longValueExact();
    for (Map.Entry<String, Long> entry : totals.entrySet())
      result.categories.add(new Category(entry.getKey(), entry.getValue()));
    result.categories.sort(
        Comparator.comparingLong((Category c) -> c.amount).reversed().thenComparing(c -> c.name));
    return result;
  }
}
