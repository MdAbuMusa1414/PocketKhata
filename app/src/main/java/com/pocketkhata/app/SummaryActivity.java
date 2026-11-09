package com.pocketkhata.app;

import android.content.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.ValueEventListener;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public final class SummaryActivity extends BaseActivity {
  private Repository repository;
  private ValueEventListener listener;
  private List<Entry> entries = new ArrayList<>();
  private YearMonth month = YearMonth.now();
  private boolean loaded;

  @Override
  protected void onCreate(Bundle saved) {
    super.onCreate(saved);
    if (!requireLogin()) return;
    screen(R.layout.activity_summary);
    toolbar();
    repository = new Repository(this);
    if (saved != null) {
      try {
        month = YearMonth.parse(saved.getString("summary_month", month.toString()));
      } catch (Exception ignored) {
        month = YearMonth.now();
      }
    }
    findViewById(R.id.previousMonth)
        .setOnClickListener(
            v -> {
              month = month.minusMonths(1);
              render();
            });
    findViewById(R.id.nextMonth)
        .setOnClickListener(
            v -> {
              if (month.isBefore(YearMonth.now())) {
                month = month.plusMonths(1);
                render();
              }
            });
    findViewById(R.id.shareMonth).setOnClickListener(v -> share());
    BottomNavigationView nav = findViewById(R.id.navigation);
    nav.setSelectedItemId(R.id.nav_summary);
    nav.setOnItemSelectedListener(
        item -> {
          if (item.getItemId() == R.id.nav_summary) return true;
          Class<?> target =
              item.getItemId() == R.id.nav_home ? HomeActivity.class : SettingsActivity.class;
          startActivity(
              new Intent(this, target)
                  .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
          finish();
          return true;
        });
    render();
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (repository == null || !requireLogin()) return;
    listener =
        repository.listen(
            new Repository.Rows() {
              public void loaded(List<Entry> rows) {
                entries = rows;
                loaded = true;
                render();
              }

              public void failed(String message) {
                say(message);
                label(R.id.summaryStatus, "Could not load this summary.");
              }
            });
  }

  private void render() {
    MonthlyBreakdown result = MonthlyBreakdown.calculate(entries, month, LocalDate.now());
    label(
        R.id.summaryMonth, month.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)));
    label(R.id.summaryTotal, Money.format(result.total));
    label(
        R.id.summaryCount,
        result.count + (result.count == 1 ? " expense recorded" : " expenses recorded"));
    label(R.id.summaryAverage, Money.format(result.average));
    label(
        R.id.averageHint,
        month.equals(YearMonth.now()) ? "Per day, up to today" : "Per day, full month");
    label(
        R.id.summaryTop,
        result.categories.isEmpty() ? "No expenses" : result.categories.get(0).name);
    label(
        R.id.summaryStatus,
        loaded ? "Expenses only · loans are counted separately on Home" : "Loading your summary…");
    findViewById(R.id.nextMonth).setEnabled(month.isBefore(YearMonth.now()));
    findViewById(R.id.nextMonth).setAlpha(month.isBefore(YearMonth.now()) ? 1f : 0.35f);
    LinearLayout categories = findViewById(R.id.categoryList);
    categories.removeAllViews();
    for (MonthlyBreakdown.Category category : result.categories) {
      View row = getLayoutInflater().inflate(R.layout.row_category_summary, categories, false);
      ((TextView) row.findViewById(R.id.categoryName)).setText(category.name);
      ((TextView) row.findViewById(R.id.categoryAmount)).setText(Money.format(category.amount));
      ((LinearProgressIndicator) row.findViewById(R.id.categoryProgress))
          .setProgressCompat(
              result.total == 0 ? 0 : (int) Math.round(category.amount * 100.0 / result.total),
              false);
      categories.addView(row);
    }
    findViewById(R.id.emptyCategories)
        .setVisibility(loaded && result.categories.isEmpty() ? View.VISIBLE : View.GONE);
  }

  private void share() {
    if (!loaded) {
      say("Please wait for your summary to load");
      return;
    }
    MonthlyBreakdown result = MonthlyBreakdown.calculate(entries, month, LocalDate.now());
    StringBuilder text =
        new StringBuilder("PocketKhata · ")
            .append(month)
            .append("\nExpenses: ")
            .append(Money.format(result.total))
            .append("\nDaily average: ")
            .append(Money.format(result.average));
    for (MonthlyBreakdown.Category category : result.categories)
      text.append("\n").append(category.name).append(": ").append(Money.format(category.amount));
    Intent intent =
        new Intent(Intent.ACTION_SEND)
            .setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, text.toString());
    try {
      startActivity(Intent.createChooser(intent, "Share Monthly Summary"));
    } catch (ActivityNotFoundException error) {
      say("No sharing app is available");
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle saved) {
    super.onSaveInstanceState(saved);
    saved.putString("summary_month", month.toString());
  }

  @Override
  protected void onStop() {
    if (repository != null && listener != null) repository.remove(listener);
    super.onStop();
  }
}
