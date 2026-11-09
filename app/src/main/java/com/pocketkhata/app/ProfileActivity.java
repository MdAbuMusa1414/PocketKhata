package com.pocketkhata.app;

import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;
import java.io.File;
import java.util.*;
import java.util.concurrent.*;

public final class ProfileActivity extends BaseActivity {
  private ProfileStore store;
  private ValueEventListener profileListener, connectionListener;
  private DatabaseReference connection;
  private String photo = "", ownerUid;
  private Uri cameraUri;
  private boolean loaded, saving, processing, connected;
  private final ExecutorService worker = Executors.newSingleThreadExecutor();
  private final ActivityResultLauncher<String[]> gallery =
      registerForActivityResult(
          new ActivityResultContracts.OpenDocument(),
          uri -> {
            if (uri != null) readPhoto(uri);
          });
  private final ActivityResultLauncher<Uri> camera =
      registerForActivityResult(
          new ActivityResultContracts.TakePicture(),
          ok -> {
            if (ok && cameraUri != null) readPhoto(cameraUri);
          });

  @Override
  protected void onCreate(Bundle saved) {
    super.onCreate(saved);
    if (!requireLogin()) return;
    screen(R.layout.activity_profile);
    toolbar();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ownerUid = user.getUid();
    store = new ProfileStore(this);
    ((TextInputEditText) findViewById(R.id.profileName)).setText(user.getDisplayName());
    TextInputEditText email = findViewById(R.id.profileEmail);
    email.setText(user.getEmail());
    email.setKeyListener(null);
    email.setFocusable(false);
    if (saved != null) {
      photo = saved.getString("profile_photo", "");
      loaded = saved.getBoolean("profile_loaded");
      String uri = saved.getString("profile_camera");
      if (uri != null) cameraUri = Uri.parse(uri);
    }
    findViewById(R.id.changePhoto).setOnClickListener(v -> choosePhoto());
    findViewById(R.id.profilePhoto).setOnClickListener(v -> choosePhoto());
    findViewById(R.id.saveProfile).setOnClickListener(v -> save());
    showPhoto();
    controls();
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (store == null || !requireLogin()) return;
    profileListener =
        store.listen(
            new ProfileStore.Listener() {
              public void loaded(UserProfile profile) {
                if (!loaded) {
                  if (!profile.getName().isEmpty())
                    ((TextInputEditText) findViewById(R.id.profileName)).setText(profile.getName());
                  ((TextInputEditText) findViewById(R.id.profilePhone)).setText(profile.getPhone());
                  photo = profile.getPhoto();
                  loaded = true;
                  showPhoto();
                  controls();
                }
              }

              public void failed(String message) {
                label(R.id.profileStatus, message);
                say(message);
              }
            });
    connection =
        FirebaseDatabase.getInstance(getString(R.string.database_url))
            .getReference(".info/connected");
    connectionListener =
        new ValueEventListener() {
          public void onDataChange(@NonNull DataSnapshot value) {
            connected = Boolean.TRUE.equals(value.getValue(Boolean.class));
          }

          public void onCancelled(@NonNull DatabaseError error) {
            connected = false;
          }
        };
    connection.addValueEventListener(connectionListener);
  }

  private void choosePhoto() {
    if (!loaded || saving || processing) return;
    List<String> choices = new ArrayList<>(Arrays.asList("Choose Photo", "Take Photo"));
    if (!photo.isEmpty()) choices.add("Remove Photo");
    new MaterialAlertDialogBuilder(this)
        .setTitle("Your Profile Photo")
        .setItems(
            choices.toArray(new String[0]),
            (dialog, which) -> {
              if (which == 0) {
                try {
                  gallery.launch(new String[] {"image/*"});
                } catch (ActivityNotFoundException error) {
                  say("No photo picker is available");
                }
              } else if (which == 1) takePhoto();
              else {
                photo = "";
                showPhoto();
              }
            })
        .show();
  }

  private void takePhoto() {
    try {
      File directory = new File(getCacheDir(), "receipts");
      if (!directory.exists() && !directory.mkdirs()) throw new java.io.IOException();
      cameraUri =
          FileProvider.getUriForFile(
              this,
              getPackageName() + ".fileprovider",
              File.createTempFile("profile_", ".jpg", directory));
      camera.launch(cameraUri);
    } catch (Exception error) {
      say("Could not open the camera. You can choose a photo instead.");
    }
  }

  private void readPhoto(Uri uri) {
    processing = true;
    controls();
    worker.execute(
        () -> {
          try {
            String image = Photos.read(this, uri);
            runOnUiThread(
                () -> {
                  if (isDestroyed()) return;
                  photo = image;
                  processing = false;
                  showPhoto();
                  controls();
                });
          } catch (Exception error) {
            runOnUiThread(
                () -> {
                  if (isDestroyed()) return;
                  processing = false;
                  controls();
                  say("Could not read that photo. Please choose another.");
                });
          }
        });
  }

  private void showPhoto() {
    ProfilePhotos.bind(findViewById(R.id.profilePhoto), photo);
  }

  private void controls() {
    boolean editable = loaded && !saving;
    findViewById(R.id.profileName).setEnabled(editable);
    findViewById(R.id.profilePhone).setEnabled(editable);
    findViewById(R.id.changePhoto).setEnabled(editable && !processing);
    findViewById(R.id.saveProfile).setEnabled(editable && !processing);
    findViewById(R.id.profileProgress)
        .setVisibility(!loaded || saving || processing ? View.VISIBLE : View.GONE);
    label(
        R.id.profileStatus,
        !loaded
            ? "Loading your profile…"
            : processing
                ? "Preparing your photo…"
                : saving ? "Saving your profile…" : "Your email belongs to your login account.");
  }

  private void save() {
    if (!loaded || saving || processing || !requireLogin()) return;
    String name = input(R.id.profileName), phone = input(R.id.profilePhone);
    String issue = ProfileValidation.nameError(name);
    if (issue != null) {
      error(R.id.profileName, issue);
      return;
    }
    issue = ProfileValidation.phoneError(phone);
    if (issue != null) {
      error(R.id.profilePhone, issue);
      return;
    }
    if (!connected) {
      say("Connect to the internet to save your profile");
      return;
    }
    UserProfile profile = new UserProfile();
    profile.setName(name);
    profile.setPhone(ProfileValidation.normalizePhone(phone));
    profile.setPhoto(photo);
    profile.setUpdatedAt(System.currentTimeMillis());
    saving = true;
    controls();
    store.save(
        profile,
        message -> {
          if (isDestroyed() || isFinishing()) return;
          if (message != null) {
            saving = false;
            controls();
            say(message);
            return;
          }
          FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
          if (user == null || !ownerUid.equals(user.getUid())) {
            goLogin();
            return;
          }
          user.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(name).build())
              .addOnCompleteListener(
                  this,
                  task -> {
                    Toast.makeText(
                            this,
                            task.isSuccessful()
                                ? "Profile saved"
                                : "Profile saved. Your sign-in name will refresh later.",
                            Toast.LENGTH_LONG)
                        .show();
                    finish();
                  });
        });
  }

  @Override
  protected void onSaveInstanceState(@NonNull Bundle saved) {
    super.onSaveInstanceState(saved);
    saved.putString("profile_photo", photo);
    saved.putBoolean("profile_loaded", loaded);
    if (cameraUri != null) saved.putString("profile_camera", cameraUri.toString());
  }

  @Override
  protected void onStop() {
    if (store != null) store.remove(profileListener);
    if (connection != null && connectionListener != null)
      connection.removeEventListener(connectionListener);
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    worker.shutdown();
    super.onDestroy();
  }
}
