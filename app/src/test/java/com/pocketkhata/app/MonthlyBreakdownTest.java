package com.pocketkhata.app;

import static org.junit.Assert.*;

import java.time.*;
import java.util.*;
import org.junit.Test;

public class MonthlyBreakdownTest {
  private Entry entry(String type, long amount, String category, String date) {
    Entry e = new Entry();
    e.setType(type);
    e.setAmountPaisa(amount);
    e.setCategory(category);
    e.setDate(date);
    return e;
  }

  @Test
  public void summaryCountsOnlySelectedMonthExpensesAndGroupsCategories() {
    List<Entry> rows =
        Arrays.asList(
            entry("Expense", 10000, "Food", "2026-11-01"),
            entry("Expense", 2500, "Food", "2026-11-02"),
            entry("Expense", 20000, "Study", "2026-11-03"),
            entry("Lent", 90000, "Food", "2026-11-03"),
            entry("Borrowed", 30000, "Other", "2026-11-04"),
            entry("Expense", 80000, "Food", "2026-10-31"));
    MonthlyBreakdown m =
        MonthlyBreakdown.calculate(rows, YearMonth.of(2026, 11), LocalDate.of(2026, 11, 9));
    assertEquals(32500, m.total);
    assertEquals(3, m.count);
    assertEquals(2, m.categories.size());
    assertEquals("Study", m.categories.get(0).name);
    assertEquals(20000, m.categories.get(0).amount);
    assertEquals(12500, m.categories.get(1).amount);
    assertEquals(Summary.calculate(rows, YearMonth.of(2026, 11)).spent, m.total);
  }

  @Test
  public void averageUsesDaysSoFarInCurrentMonthWithExactRounding() {
    MonthlyBreakdown m =
        MonthlyBreakdown.calculate(
            Collections.singletonList(entry("Expense", 455000, "Food", "2026-11-09")),
            YearMonth.of(2026, 11),
            LocalDate.of(2026, 11, 9));
    assertEquals(50556, m.average);
  }

  @Test
  public void pastMonthAverageUsesItsFullLength() {
    MonthlyBreakdown m =
        MonthlyBreakdown.calculate(
            Collections.singletonList(entry("Expense", 310000, "Food", "2026-10-10")),
            YearMonth.of(2026, 10),
            LocalDate.of(2026, 11, 9));
    assertEquals(10000, m.average);
  }

  @Test
  public void invalidDatesAreIgnoredAndEmptyCategoriesAreOther() {
    MonthlyBreakdown m =
        MonthlyBreakdown.calculate(
            Arrays.asList(
                entry("Expense", 100, "Food", "invalid"), entry("Expense", 250, "", "2026-11-01")),
            YearMonth.of(2026, 11),
            LocalDate.of(2026, 11, 9));
    assertEquals(250, m.total);
    assertEquals(1, m.count);
    assertEquals("Other", m.categories.get(0).name);
    MonthlyBreakdown empty =
        MonthlyBreakdown.calculate(
            Collections.emptyList(), YearMonth.of(2026, 11), LocalDate.of(2026, 11, 9));
    assertEquals(0, empty.average);
    assertTrue(empty.categories.isEmpty());
  }
}
