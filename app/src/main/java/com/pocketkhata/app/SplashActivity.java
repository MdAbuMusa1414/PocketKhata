package com.pocketkhata.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
public class SplashActivity extends BaseActivity {
 private final Handler handler=new Handler(Looper.getMainLooper());
 private final Runnable next=()->{if(com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser()==null)goLogin();else goHome();};
 @Override protected void onCreate(Bundle b){super.onCreate(b);screen(R.layout.activity_splash);handler.postDelayed(next,850);}
 @Override protected void onDestroy(){handler.removeCallbacks(next);super.onDestroy();}
}
