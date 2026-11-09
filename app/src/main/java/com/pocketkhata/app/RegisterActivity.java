package com.pocketkhata.app;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.*;
public class RegisterActivity extends BaseActivity {
 @Override protected void onCreate(Bundle b){super.onCreate(b);screen(R.layout.activity_register);toolbar();findViewById(R.id.create).setOnClickListener(v->create());}
 private void create(){String name=input(R.id.name),email=input(R.id.email),pass=((TextInputEditText)findViewById(R.id.password)).getText().toString(),confirm=((TextInputEditText)findViewById(R.id.confirm)).getText().toString();
  if(name.length()<2){error(R.id.name,"Enter your name (at least 2 characters)");return;}
  if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){error(R.id.email,"Enter a valid email address");return;}
  if(pass.length()<8){error(R.id.password,"Use at least 8 characters");return;}
  if(!pass.equals(confirm)){error(R.id.confirm,"Passwords do not match");return;}
  findViewById(R.id.create).setEnabled(false);findViewById(R.id.progress).setVisibility(View.VISIBLE);
  FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,pass).addOnCompleteListener(this,t->{if(!t.isSuccessful()){findViewById(R.id.create).setEnabled(true);findViewById(R.id.progress).setVisibility(View.GONE);say(Errors.auth(t.getException()));return;}
   FirebaseUser user=t.getResult().getUser();user.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(name).build()).addOnCompleteListener(this,p->{android.widget.Toast.makeText(this,p.isSuccessful()?"Your khata is ready":"Account ready. Your name could not be saved yet.",android.widget.Toast.LENGTH_LONG).show();goHome();});
  });
 }
}
