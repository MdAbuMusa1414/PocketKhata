package com.pocketkhata.app;

import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;
import java.time.YearMonth;

public class SettingsActivity extends BaseActivity {
  private Repository repo;
  private ValueEventListener summaryListener;

  @Override
  protected void onCreate(Bundle b) {
    super.onCreate(b);
    if (!requireLogin()) return;
    screen(R.layout.activity_settings);
    toolbar();
    com.google.android.material.bottomnavigation.BottomNavigationView nav =
        findViewById(R.id.navigation);
    nav.setSelectedItemId(R.id.nav_settings);
    nav.setOnItemSelectedListener(
        item -> {
          if (item.getItemId() == R.id.nav_settings) return true;
          Intent i = new Intent(this, HomeActivity.class);
          i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
          i.putExtra("open_loans", item.getItemId() == R.id.nav_loans);
          startActivity(i);
          finish();
          return true;
        });
    repo = new Repository(this);
    label(R.id.account, FirebaseAuth.getInstance().getCurrentUser().getEmail());
    long budget = Prefs.user(this).getLong("budget", 0);
    if (b == null && budget > 0)
      ((TextInputEditText) findViewById(R.id.budget)).setText(Money.editable(budget));
    ((SwitchMaterial) findViewById(R.id.showSettled))
        .setChecked(Prefs.user(this).getBoolean("showSettled", true));
    findViewById(R.id.saveSettings)
        .setOnClickListener(
            v -> {
              long amount = 0;
              if (!input(R.id.budget).isEmpty()) {
                try {
                  amount = Money.parse(input(R.id.budget));
                } catch (Exception e) {
                  error(R.id.budget, e.getMessage());
                  return;
                }
              }
              Prefs.user(this)
                  .edit()
                  .putLong("budget", amount)
                  .putBoolean(
                      "showSettled", ((SwitchMaterial) findViewById(R.id.showSettled)).isChecked())
                  .apply();
              say("Preferences saved on this phone");
            });
    findViewById(R.id.github)
        .setOnClickListener(
            v -> {
              try {
                startActivity(
                    new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/MdAbuMusa1414/PocketKhata")));
              } catch (ActivityNotFoundException e) {
                say("No browser is available");
              }
            });
    findViewById(R.id.shareSummary).setOnClickListener(v -> shareSummary());
    findViewById(R.id.logout)
        .setOnClickListener(
            v ->
                new MaterialAlertDialogBuilder(this)
                    .setTitle("Close your khata?")
                    .setMessage(
                        "Your synced records stay in your account. You can sign in again anytime.")
                    .setNegativeButton("Stay", null)
                    .setPositiveButton(
                        "Log out",
                        (d, w) -> {
                          FirebaseAuth.getInstance().signOut();
                          goLogin();
                        })
                    .show());
  }

  private void shareSummary() {
    findViewById(R.id.shareSummary).setEnabled(false);
    summaryListener =
        repo.listen(
            new Repository.Rows() {
              public void loaded(java.util.List<Entry> rows) {
                repo.remove(summaryListener);
                findViewById(R.id.shareSummary).setEnabled(true);
                Summary s = Summary.calculate(rows, YearMonth.now());
                String text =
                    "My PocketKhata summary · "
                        + YearMonth.now()
                        + "\nSpent this month: "
                        + Money.format(s.spent)
                        + "\nLoan given (not paid back): "
                        + Money.format(s.lent)
                        + "\nLoan taken (not paid back): "
                        + Money.format(s.borrowed);
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, text);
                try {
                  startActivity(Intent.createChooser(i, "Share your summary"));
                } catch (ActivityNotFoundException e) {
                  say("No sharing app is available");
                }
              }

              public void failed(String message) {
                findViewById(R.id.shareSummary).setEnabled(true);
                say(message);
                repo.remove(summaryListener);
              }
            });
  }

  @Override
  protected void onStop() {
    if (repo != null && summaryListener != null) repo.remove(summaryListener);
    super.onStop();
  }
}
