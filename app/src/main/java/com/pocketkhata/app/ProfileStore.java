package com.pocketkhata.app;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public final class ProfileStore {
  public interface Listener {
    void loaded(UserProfile profile);

    void failed(String message);
  }

  private final DatabaseReference reference;

  public ProfileStore(Context context) {
    String uid = FirebaseAuth.getInstance().getUid();
    if (uid == null) throw new IllegalStateException("Sign in first");
    reference =
        FirebaseDatabase.getInstance(context.getString(R.string.database_url))
            .getReference("users")
            .child(uid)
            .child("profile");
  }

  public ValueEventListener listen(Listener callback) {
    ValueEventListener listener =
        new ValueEventListener() {
          public void onDataChange(@NonNull DataSnapshot snapshot) {
            try {
              UserProfile profile = snapshot.getValue(UserProfile.class);
              callback.loaded(profile == null ? new UserProfile() : profile);
            } catch (DatabaseException error) {
              callback.failed("Could not read your profile");
            }
          }

          public void onCancelled(@NonNull DatabaseError error) {
            callback.failed("Could not load your profile. Check your connection.");
          }
        };
    reference.addValueEventListener(listener);
    return listener;
  }

  public void remove(ValueEventListener listener) {
    if (listener != null) reference.removeEventListener(listener);
  }

  public void save(UserProfile profile, Repository.Done callback) {
    reference.setValue(
        profile,
        (error, ref) ->
            callback.complete(
                error == null ? null : "Could not save your profile. Please try again."));
  }
}
