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
  private ProfileStore profileStore;
  private ValueEventListener profileListener;
  private ValueEventListener listener, connectionListener;
  private DatabaseReference connection;
  private List<Entry> entries = new ArrayList<>();
  private EntryAdapter adapter;
  private SingleViewAdapter emptyAdapter;
  private View dashboard, empty;
  private String query = "", filter = "All";
  private boolean loaded;
  private String userGreeting = "Welcome to your khata";

  @Override
  protected void onCreate(Bundle b) {
    super.onCreate(b);
    if (!requireLogin()) return;
    screen(R.layout.activity_home);
    repo = new Repository(this);
    profileStore = new ProfileStore(this);
    findViewById(R.id.profileEntry)
        .setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
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
              if ("Lent".equals(filter) || "Borrowed".equals(filter))
                i.putExtra("initial_type", filter);
              startActivity(i);
            });
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
    nav.setSelectedItemId(R.id.nav_home);
    nav.setOnItemSelectedListener(
        item -> {
          if (item.getItemId() == R.id.nav_home) return true;
          Class<?> target =
              item.getItemId() == R.id.nav_summary ? SummaryActivity.class : SettingsActivity.class;
          startActivity(
              new Intent(this, target)
                  .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
          return false;
        });
    ((DashboardFilters) dashboard.findViewById(R.id.filters)).showAllRecords();
    dlabel(R.id.listHeading, "Your Records");
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
    ((BottomNavigationView) findViewById(R.id.navigation)).setSelectedItemId(R.id.nav_home);
  }

  private void dlabel(int id, String text) {
    ((TextView) dashboard.findViewById(id)).setText(text);
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (repo == null || !requireLogin()) return;
    String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    userGreeting =
        name == null || name.trim().isEmpty() ? "Welcome to your khata" : "Hello, " + name;
    dlabel(R.id.greeting, userGreeting);
    profileListener =
        profileStore.listen(
            new ProfileStore.Listener() {
              public void loaded(UserProfile profile) {
                ProfilePhotos.bind(findViewById(R.id.homeAvatar), profile.getPhoto());
                if (!profile.getName().isEmpty())
                  dlabel(R.id.greeting, "Hello, " + profile.getName());
              }

              public void failed(String message) {
                ProfilePhotos.bind(findViewById(R.id.homeAvatar), "");
              }
            });
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
    if (profileStore != null) profileStore.remove(profileListener);
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
    dlabel(R.id.budgetAmount, budget > 0 ? Money.format(budget) : "Not set");
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
    List<Entry> visible = LedgerRecords.select(entries, show, filter, query);
    adapter.submit(visible);
    emptyAdapter.setShown(visible.isEmpty() && loaded);
    ((TextView) empty.findViewById(R.id.emptyTitle))
        .setText(query.isEmpty() ? "No records here yet" : "No matching records");
    ((TextView) empty.findViewById(R.id.emptySubtitle))
        .setText(
            query.isEmpty()
                ? "Tap Add Record to get started."
                : "Try a different search or filter.");
  }
}
