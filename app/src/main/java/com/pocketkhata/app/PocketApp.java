package com.pocketkhata.app;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;

public class PocketApp extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    com.google.firebase.FirebaseApp.initializeApp(this);
    FirebaseDatabase.getInstance(getString(R.string.database_url)).setPersistenceEnabled(true);
  }
}
