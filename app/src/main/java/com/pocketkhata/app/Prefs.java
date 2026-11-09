package com.pocketkhata.app;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.auth.FirebaseAuth;
public final class Prefs {
 public static SharedPreferences user(Context c){String uid=FirebaseAuth.getInstance().getUid();return c.getSharedPreferences("preferences_"+(uid==null?"guest":uid),Context.MODE_PRIVATE);}
 public static SharedPreferences login(Context c){return c.getSharedPreferences("login",Context.MODE_PRIVATE);}
}
