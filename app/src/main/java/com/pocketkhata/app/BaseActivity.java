package com.pocketkhata.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
public abstract class BaseActivity extends AppCompatActivity {
 protected void screen(int layout){
  WindowCompat.setDecorFitsSystemWindows(getWindow(),false); setContentView(layout);
  View root=findViewById(R.id.root);
  ViewCompat.setOnApplyWindowInsetsListener(root,(v,i)->{Insets b=i.getInsets(WindowInsetsCompat.Type.systemBars()); Insets ime=i.getInsets(WindowInsetsCompat.Type.ime());v.setPadding(b.left,b.top,b.right,Math.max(b.bottom,ime.bottom));return i;});
 }
 protected boolean requireLogin(){ if(FirebaseAuth.getInstance().getCurrentUser()!=null)return true;goLogin();return false; }
 protected void goLogin(){Intent i=new Intent(this,LoginActivity.class);i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);startActivity(i);finish();}
 protected void goHome(){Intent i=new Intent(this,HomeActivity.class);i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);startActivity(i);finish();}
 protected void toolbar(){MaterialToolbar t=findViewById(R.id.toolbar);if(t!=null)t.setNavigationOnClickListener(v->finish());}
 protected String input(int id){TextInputEditText e=findViewById(id);return e.getText()==null?"":e.getText().toString().trim();}
 protected void error(int id,String message){TextInputEditText e=findViewById(id);e.setError(message);e.requestFocus();}
 protected void say(String message){Snackbar.make(findViewById(R.id.root),message,Snackbar.LENGTH_LONG).show();}
 protected void label(int id,String value){((TextView)findViewById(id)).setText(value);}
}
