package com.pocketkhata.app;

import static org.junit.Assert.*;

import java.time.YearMonth;
import java.util.Arrays;
import org.junit.Test;

public class MoneySummaryTest {
  @Test
  public void decimalAmountsStayExact() {
    assertEquals(10, Money.parse("0.10"));
    assertEquals(12345, Money.parse("123.45"));
    assertEquals(999999999, Money.parse("9999999.99"));
    assertEquals("123.45", Money.editable(12345));
  }

  @Test
  public void wrongAmountsAreRejected() {
    for (String s : new String[] {"", "0", "-1", "1.234", "1e3", "NaN", "10000000", "text", "."}) {
      try {
        Money.parse(s);
        fail("Accepted " + s);
      } catch (IllegalArgumentException expected) {
      }
    }
  }

  @Test
  public void budgetCountsOnlyExpensesInSelectedMonth() {
    Entry a = row("Expense", 5000, "2026-11-01", false);
    Entry b = row("Expense", 10000, "2026-10-31", false);
    Entry c = row("Lent", 30000, "2026-11-02", false);
    Summary s = Summary.calculate(Arrays.asList(a, b, c), YearMonth.of(2026, 11));
    assertEquals(5000, s.spent);
    assertEquals(30000, s.lent);
  }

  @Test
  public void settledLoansDoNotCountAsOutstanding() {
    Summary s =
        Summary.calculate(
            Arrays.asList(
                row("Lent", 50000, "2026-10-01", true),
                row("Lent", 10000, "2026-10-01", false),
                row("Borrowed", 20000, "2026-10-01", false),
                row("Borrowed", 30000, "2026-11-01", true)),
            YearMonth.of(2026, 11));
    assertEquals(10000, s.lent);
    assertEquals(20000, s.borrowed);
    assertEquals(0, s.spent);
  }

  @Test
  public void invalidDateDoesNotCrashDashboard() {
    Summary s =
        Summary.calculate(
            Arrays.asList(row("Expense", 100, "invalid", false)), YearMonth.of(2026, 11));
    assertEquals(0, s.spent);
  }

  private Entry row(String type, long amount, String date, boolean settled) {
    Entry e = new Entry();
    e.setType(type);
    e.setAmountPaisa(amount);
    e.setDate(date);
    e.setSettled(settled);
    return e;
  }
}
