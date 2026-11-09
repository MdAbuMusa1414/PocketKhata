package com.pocketkhata.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.*;
import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.*;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HomeActivity extends BaseActivity {
  private Repository repo;
  private ValueEventListener listener, connectionListener;
  private DatabaseReference connection;
  private List<Entry> entries = new ArrayList<>();
  private EntryAdapter adapter;
  private SingleViewAdapter emptyAdapter;
  private View dashboard, empty;
  private String query = "", filter = "All";
  private boolean loaded, loanTab;

  @Override
  protected void onCreate(Bundle b) {
    super.onCreate(b);
    if (!requireLogin()) return;
    screen(R.layout.activity_home);
    repo = new Repository(this);
    RecyclerView list = findViewById(R.id.list);
    list.setLayoutManager(new LinearLayoutManager(this));
    list.setItemAnimator(null);
    dashboard = getLayoutInflater().inflate(R.layout.dashboard_header, list, false);
    empty = getLayoutInflater().inflate(R.layout.row_empty, list, false);
    emptyAdapter = new SingleViewAdapter(empty, false);
    adapter =
        new EntryAdapter(
            e -> {
              Intent i = new Intent(this, DetailActivity.class);
              i.putExtra("entry_id", e.getId());
              startActivity(i);
            });
    list.setAdapter(
        new ConcatAdapter(new SingleViewAdapter(dashboard, true), adapter, emptyAdapter));
    findViewById(R.id.add)
        .setOnClickListener(
            v -> {
              Intent i = new Intent(this, EditActivity.class);
              if (loanTab) i.putExtra("initial_type", "Lent");
              startActivity(i);
            });
    findViewById(R.id.settings)
        .setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
    ((EditText) dashboard.findViewById(R.id.search))
        .addTextChangedListener(
            new TextWatcher() {
              public void beforeTextChanged(CharSequence s, int st, int c, int a) {}

              public void onTextChanged(CharSequence s, int st, int before, int count) {
                query = s.toString().toLowerCase(Locale.ROOT).trim();
                render();
              }

              public void afterTextChanged(Editable e) {}
            });
    ((DashboardFilters) dashboard.findViewById(R.id.filters))
        .setOnSelectionChangedListener(
            id -> {
              filter =
                  id == R.id.filter_expense
                      ? "Expense"
                      : id == R.id.filter_lent
                          ? "Lent"
                          : id == R.id.filter_borrowed ? "Borrowed" : "All";
              render();
            });
    BottomNavigationView nav = findViewById(R.id.navigation);
    nav.setOnItemSelectedListener(
        item -> {
          if (item.getItemId() == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return false;
          }
          setLoanTab(item.getItemId() == R.id.nav_loans);
          list.scrollToPosition(0);
          return true;
        });
    loanTab =
        b != null ? b.getBoolean("loan_tab") : getIntent().getBooleanExtra("open_loans", false);
    nav.setSelectedItemId(loanTab ? R.id.nav_loans : R.id.nav_home);
    setLoanTab(loanTab);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
    ((BottomNavigationView) findViewById(R.id.navigation))
        .setSelectedItemId(
            intent.getBooleanExtra("open_loans", false) ? R.id.nav_loans : R.id.nav_home);
  }

  private void dlabel(int id, String text) {
    ((TextView) dashboard.findViewById(id)).setText(text);
  }

  private void setLoanTab(boolean value) {
    loanTab = value;
    filter = "All";
    dashboard.findViewById(R.id.spendingCard).setVisibility(value ? View.GONE : View.VISIBLE);
    ((DashboardFilters) dashboard.findViewById(R.id.filters)).showLoansOnly(value);
    dlabel(R.id.homeTitle, value ? "My Loans" : "My Money");
    dlabel(R.id.listHeading, value ? "Loan Records" : "Recent Records");
    render();
  }

  @Override
  protected void onSaveInstanceState(Bundle b) {
    super.onSaveInstanceState(b);
    b.putBoolean("loan_tab", loanTab);
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (repo == null || !requireLogin()) return;
    String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    dlabel(
        R.id.greeting,
        name == null || name.trim().isEmpty() ? "Welcome to your khata" : "Hello, " + name);
    listener =
        repo.listen(
            new Repository.Rows() {
              public void loaded(List<Entry> rows) {
                entries = rows;
                loaded = true;
                render();
              }

              public void failed(String message) {
                say(message);
                dlabel(R.id.syncStatus, "Cannot load records. Check your internet.");
              }
            });
    connection =
        FirebaseDatabase.getInstance(getString(R.string.database_url))
            .getReference(".info/connected");
    connectionListener =
        new ValueEventListener() {
          public void onDataChange(DataSnapshot s) {
            dlabel(
                R.id.syncStatus,
                Boolean.TRUE.equals(s.getValue(Boolean.class))
                    ? "● Connected · records saved to your account"
                    : "○ Offline · showing saved records");
          }

          public void onCancelled(DatabaseError e) {
            dlabel(R.id.syncStatus, "Check your internet connection");
          }
        };
    connection.addValueEventListener(connectionListener);
  }

  @Override
  protected void onStop() {
    if (repo != null) repo.remove(listener);
    if (connection != null && connectionListener != null)
      connection.removeEventListener(connectionListener);
    super.onStop();
  }

  private void render() {
    if (adapter == null) return;
    Summary s = Summary.calculate(entries, YearMonth.now());
    dlabel(
        R.id.monthLabel,
        YearMonth.now().format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)));
    dlabel(R.id.spent, Money.format(s.spent));
    dlabel(R.id.lent, Money.format(s.lent));
    dlabel(R.id.borrowed, Money.format(s.borrowed));
    long budget = Prefs.user(this).getLong("budget", 0);
    ((LinearProgressIndicator) dashboard.findViewById(R.id.budgetProgress))
        .setProgressCompat(budget > 0 ? (int) Math.min(100, s.spent * 100.0 / budget) : 0, false);
    dlabel(
        R.id.budgetText,
        budget == 0
            ? "Add your monthly budget in Settings"
            : s.spent > budget
                ? Money.format(s.spent - budget) + " over budget"
                : Money.format(budget - s.spent) + " left of " + Money.format(budget));
    boolean show = Prefs.user(this).getBoolean("showSettled", true);
    List<Entry> visible = new ArrayList<>();
    for (Entry e : entries) {
      if (loanTab && !e.isLoan()) continue;
      if (!show && e.isLoan() && e.isSettled()) continue;
      if (!filter.equals("All") && !filter.equals(e.getType())) continue;
      String hay =
          (e.getTitle() + " " + e.getPerson() + " " + e.getCategory() + " " + e.getNote())
              .toLowerCase(Locale.ROOT);
      if (hay.contains(query)) visible.add(e);
    }
    adapter.submit(visible);
    emptyAdapter.setShown(visible.isEmpty() && loaded);
    ((TextView) empty.findViewById(R.id.emptyTitle))
        .setText(
            query.isEmpty()
                ? (loanTab ? "No loans here yet" : "No records here yet")
                : "No matching records");
    ((TextView) empty.findViewById(R.id.emptySubtitle))
        .setText(
            query.isEmpty()
                ? "Tap Add Record to get started."
                : "Try a different search or filter.");
  }
}
