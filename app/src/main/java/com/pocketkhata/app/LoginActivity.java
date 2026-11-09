package com.pocketkhata.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
public class LoginActivity extends BaseActivity {
 private FirebaseAuth auth;
 @Override protected void onCreate(Bundle b){super.onCreate(b);screen(R.layout.activity_login);auth=FirebaseAuth.getInstance();
  if(auth.getCurrentUser()!=null){goHome();return;}
  MaterialCheckBox remember=findViewById(R.id.remember);remember.setChecked(Prefs.login(this).getBoolean("remember",true));
  if(b==null)((TextInputEditText)findViewById(R.id.email)).setText(Prefs.login(this).getString("email",""));
  findViewById(R.id.login).setOnClickListener(v->login());findViewById(R.id.register).setOnClickListener(v->startActivity(new Intent(this,RegisterActivity.class)));findViewById(R.id.forgot).setOnClickListener(v->reset());
 }
 private boolean emailValid(){if(!Patterns.EMAIL_ADDRESS.matcher(input(R.id.email)).matches()){error(R.id.email,"Enter a valid email address");return false;}return true;}
 private void busy(boolean value){findViewById(R.id.progress).setVisibility(value?View.VISIBLE:View.GONE);findViewById(R.id.login).setEnabled(!value);findViewById(R.id.forgot).setEnabled(!value);findViewById(R.id.register).setEnabled(!value);}
 private void login(){if(!emailValid())return;String pass=((TextInputEditText)findViewById(R.id.password)).getText().toString();if(pass.isEmpty()){error(R.id.password,"Enter your password");return;}busy(true);auth.signInWithEmailAndPassword(input(R.id.email),pass).addOnCompleteListener(this,t->{busy(false);if(t.isSuccessful()){boolean r=((MaterialCheckBox)findViewById(R.id.remember)).isChecked();Prefs.login(this).edit().putBoolean("remember",r).putString("email",r?input(R.id.email):"").apply();android.widget.Toast.makeText(this,"Welcome to your khata",android.widget.Toast.LENGTH_SHORT).show();goHome();}else say(Errors.auth(t.getException()));});}
 private void reset(){if(!emailValid())return;busy(true);auth.sendPasswordResetEmail(input(R.id.email)).addOnCompleteListener(this,t->{busy(false);if(t.isSuccessful())say("If an account exists, check its inbox for a reset link.");else say(Errors.auth(t.getException()));});}
}
