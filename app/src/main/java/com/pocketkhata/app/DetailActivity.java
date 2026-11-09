package com.pocketkhata.app;

import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.*;

public class DetailActivity extends BaseActivity {
  private Repository repo;
  private Entry entry;
  private String id;
  private DatabaseReference ref;
  private ValueEventListener listener;
  private boolean deleting, updating;

  @Override
  protected void onCreate(Bundle b) {
    super.onCreate(b);
    if (!requireLogin()) return;
    screen(R.layout.activity_detail);
    toolbar();
    id = getIntent().getStringExtra("entry_id");
    if (id == null || id.contains("/")) {
      finish();
      return;
    }
    repo = new Repository(this);
    findViewById(R.id.edit)
        .setOnClickListener(
            v -> {
              if (entry == null) return;
              Intent i = new Intent(this, EditActivity.class);
              i.putExtra("entry_id", id);
              startActivity(i);
            });
    findViewById(R.id.delete).setOnClickListener(v -> confirmDelete());
    findViewById(R.id.share).setOnClickListener(v -> share());
    findViewById(R.id.call).setOnClickListener(v -> call());
    findViewById(R.id.settle).setOnClickListener(v -> confirmSettle());
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (repo == null || !requireLogin()) return;
    ref = repo.entry(id);
    listener =
        new ValueEventListener() {
          public void onDataChange(@NonNull DataSnapshot s) {
            entry = s.getValue(Entry.class);
            if (entry == null) {
              if (!deleting)
                Toast.makeText(
                        DetailActivity.this,
                        "This entry is no longer available",
                        Toast.LENGTH_SHORT)
                    .show();
              finish();
              return;
            }
            entry.setId(id);
            render();
          }

          public void onCancelled(@NonNull DatabaseError e) {
            say("Unable to load this entry. Please sign in again.");
          }
        };
    ref.addValueEventListener(listener);
  }

  @Override
  protected void onStop() {
    if (ref != null && listener != null) ref.removeEventListener(listener);
    super.onStop();
  }

  private void render() {
    label(R.id.typeLabel, Labels.type(entry.getType()));
    label(R.id.detailAmount, Money.format(entry.getAmountPaisa()));
    label(R.id.detailTitle, entry.getTitle());
    label(R.id.detailMeta, (entry.isLoan() ? "" : entry.getCategory() + " · ") + entry.getDate());
    label(
        R.id.detailStatus,
        entry.isLoan()
            ? (entry.isSettled() ? "Paid back" : "Not paid back yet")
            : "Saved in your expenses");
    label(
        R.id.detailPerson,
        entry.getPerson().isEmpty()
            ? entry.getPhone()
            : entry.getPerson() + (entry.getPhone().isEmpty() ? "" : "\n" + entry.getPhone()));
    label(R.id.detailNote, entry.getNote().isEmpty() ? "No additional note." : entry.getNote());
    findViewById(R.id.detailPerson)
        .setVisibility(
            entry.getPerson().isEmpty() && entry.getPhone().isEmpty() ? View.GONE : View.VISIBLE);
    findViewById(R.id.detailNote)
        .setVisibility(entry.getNote().isEmpty() ? View.GONE : View.VISIBLE);
    findViewById(R.id.detailsCard)
        .setVisibility(
            entry.getPerson().isEmpty()
                    && entry.getPhone().isEmpty()
                    && entry.getNote().isEmpty()
                    && entry.getPhoto().isEmpty()
                ? View.GONE
                : View.VISIBLE);
    ImageView image = findViewById(R.id.photo);
    image.setVisibility(entry.getPhoto().isEmpty() ? View.GONE : View.VISIBLE);
    if (!entry.getPhoto().isEmpty()) image.setImageBitmap(Photos.decode(entry.getPhoto()));
    findViewById(R.id.call).setVisibility(entry.getPhone().isEmpty() ? View.GONE : View.VISIBLE);
    findViewById(R.id.settle).setVisibility(entry.isLoan() ? View.VISIBLE : View.GONE);
    ((MaterialButton) findViewById(R.id.settle))
        .setText(entry.isSettled() ? "Mark as Unpaid" : "Mark as Paid Back");
  }

  private void confirmDelete() {
    if (entry == null || deleting) return;
    new MaterialAlertDialogBuilder(this)
        .setTitle("Delete this entry?")
        .setMessage(
            "This removes “" + entry.getTitle() + "” from your khata. This cannot be undone.")
        .setNegativeButton("Keep it", null)
        .setPositiveButton(
            "Delete",
            (d, w) -> {
              deleting = true;
              findViewById(R.id.delete).setEnabled(false);
              repo.delete(
                  id,
                  error -> {
                    if (isDestroyed()) return;
                    if (error != null) {
                      deleting = false;
                      findViewById(R.id.delete).setEnabled(true);
                      say(error);
                    } else {
                      Toast.makeText(this, "Entry deleted", Toast.LENGTH_SHORT).show();
                      finish();
                    }
                  });
            })
        .show();
  }

  private void confirmSettle() {
    if (entry == null || updating) return;
    boolean value = !entry.isSettled();
    new MaterialAlertDialogBuilder(this)
        .setTitle(value ? "Money paid back?" : "Mark this loan as unpaid?")
        .setMessage(
            value
                ? ("Lent".equals(entry.getType())
                    ? "Confirm that you got this money back."
                    : "Confirm that you paid this money back.")
                : "This amount will need to be paid back again.")
        .setNegativeButton("Cancel", null)
        .setPositiveButton(
            value ? "Yes, Paid Back" : "Mark Unpaid",
            (d, w) -> {
              updating = true;
              findViewById(R.id.settle).setEnabled(false);
              repo.settle(
                  id,
                  value,
                  error -> {
                    if (isDestroyed()) return;
                    updating = false;
                    findViewById(R.id.settle).setEnabled(true);
                    if (error != null) say(error);
                    else say(value ? "Marked as paid back" : "Marked as unpaid");
                  });
            })
        .show();
  }

  private void share() {
    if (entry == null) return;
    String text =
        "PocketKhata · "
            + Labels.type(entry.getType())
            + "\n"
            + entry.getTitle()
            + "\n"
            + Money.format(entry.getAmountPaisa())
            + "\n"
            + entry.getDate()
            + " · "
            + entry.getCategory()
            + (entry.getPerson().isEmpty() ? "" : "\nPerson: " + entry.getPerson())
            + (entry.isLoan()
                ? "\nStatus: " + (entry.isSettled() ? "Paid back" : "Not paid back")
                : "");
    Intent i = new Intent(Intent.ACTION_SEND);
    i.setType("text/plain");
    i.putExtra(Intent.EXTRA_TEXT, text);
    try {
      startActivity(Intent.createChooser(i, "Share entry"));
    } catch (ActivityNotFoundException e) {
      say("No sharing app is available");
    }
  }

  private void call() {
    if (entry == null || entry.getPhone().isEmpty()) return;
    String number = entry.getPhone().replaceAll("[^+0-9]", "");
    try {
      startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null)));
    } catch (ActivityNotFoundException e) {
      say("No phone dialer is available on this device");
    }
  }
}
