package com.pocketkhata.app;

import static org.junit.Assert.*;

import java.util.*;
import org.junit.Test;

public class LedgerRecordsTest {
  @Test
  public void recordsIncludesAllTypesAndHomeShowsFiveRecentMatches() {
    List<Entry> rows = new ArrayList<>();
    String[] types = {"Expense", "Lent", "Borrowed"};
    for (int i = 0; i < 9; i++) {
      Entry e = new Entry();
      e.setTitle("Record " + i);
      e.setType(types[i % 3]);
      rows.add(e);
    }
    List<Entry> home = LedgerRecords.select(rows, false, true, "All", "");
    List<Entry> records = LedgerRecords.select(rows, true, true, "All", "");
    assertEquals(5, home.size());
    assertEquals(9, records.size());
    assertEquals("Record 0", home.get(0).getTitle());
    assertEquals("Record 8", records.get(8).getTitle());
    assertTrue(records.stream().anyMatch(e -> "Expense".equals(e.getType())));
  }

  @Test
  public void searchFiltersAndPaidLoanPreferenceWorkInRecords() {
    Entry expense = new Entry();
    expense.setTitle("Lunch");
    expense.setType("Expense");
    Entry lent = new Entry();
    lent.setTitle("Loan to Manik");
    lent.setPerson("Manik");
    lent.setType("Lent");
    lent.setSettled(true);
    Entry borrowed = new Entry();
    borrowed.setTitle("Loan from Asha");
    borrowed.setPerson("Asha");
    borrowed.setType("Borrowed");
    List<Entry> rows = Arrays.asList(expense, lent, borrowed);
    assertEquals(1, LedgerRecords.select(rows, true, true, "All", "MANIK").size());
    assertEquals(0, LedgerRecords.select(rows, true, false, "All", "Manik").size());
    assertEquals(1, LedgerRecords.select(rows, true, true, "Borrowed", "").size());
    assertEquals(2, LedgerRecords.select(rows, true, false, "All", "").size());
  }
}
