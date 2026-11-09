package com.pocketkhata.app;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.*;

public final class Repository {
  public interface Rows {
    void loaded(List<Entry> rows);

    void failed(String message);
  }

  public interface Done {
    void complete(String error);
  }

  private final DatabaseReference ref;

  public Repository(Context c) {
    String uid = FirebaseAuth.getInstance().getUid();
    if (uid == null) throw new IllegalStateException("Sign in first");
    ref =
        FirebaseDatabase.getInstance(c.getString(R.string.database_url))
            .getReference("users")
            .child(uid)
            .child("entries");
  }

  public ValueEventListener listen(Rows cb) {
    ValueEventListener l =
        new ValueEventListener() {
          public void onDataChange(@NonNull DataSnapshot s) {
            List<Entry> list = new ArrayList<>();
            for (DataSnapshot row : s.getChildren()) {
              try {
                Entry e = row.getValue(Entry.class);
                if (e != null) {
                  e.setId(row.getKey());
                  list.add(e);
                }
              } catch (DatabaseException ignored) {
              }
            }
            list.sort(
                Comparator.comparing(Entry::getDate)
                    .reversed()
                    .thenComparing(Comparator.comparingLong(Entry::getCreatedAt).reversed()));
            cb.loaded(list);
          }

          public void onCancelled(@NonNull DatabaseError e) {
            cb.failed("Could not load your khata. Check your connection or sign in again.");
          }
        };
    ref.addValueEventListener(l);
    return l;
  }

  public void remove(ValueEventListener l) {
    if (l != null) ref.removeEventListener(l);
  }

  public DatabaseReference entry(String id) {
    return ref.child(id);
  }

  public void save(Entry e, Done cb) {
    if (e.getId().isEmpty()) e.setId(ref.push().getKey());
    ref.child(e.getId())
        .setValue(
            e,
            (error, r) ->
                cb.complete(
                    error == null ? null : "Could not save. Check your connection and try again."));
  }

  public void delete(String id, Done cb) {
    ref.child(id)
        .removeValue(
            (error, r) -> cb.complete(error == null ? null : "Could not delete this entry."));
  }

  public void settle(String id, boolean value, Done cb) {
    Map<String, Object> updates = new HashMap<>();
    updates.put("settled", value);
    updates.put("updatedAt", System.currentTimeMillis());
    ref.child(id)
        .updateChildren(
            updates,
            (error, r) ->
                cb.complete(error == null ? null : "Could not update the repayment status."));
  }
}
