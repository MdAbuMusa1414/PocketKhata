package com.pocketkhata.app;

import static org.junit.Assert.*;
import android.os.Looper;
import android.widget.EditText;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=32, application=PocketApp.class)
public class AuthFormTest {
 @Test public void loginRejectsEmptyAndWrongEmailWithoutCrashing(){
  try(var controller=Robolectric.buildActivity(LoginActivity.class).setup()){
   LoginActivity a=controller.get();a.findViewById(R.id.login).performClick();
   assertNotNull(((EditText)a.findViewById(R.id.email)).getError());
   ((EditText)a.findViewById(R.id.email)).setText("not-an-email");a.findViewById(R.id.login).performClick();
   assertNotNull(((EditText)a.findViewById(R.id.email)).getError());
   ((EditText)a.findViewById(R.id.email)).setText("student@example.com");a.findViewById(R.id.login).performClick();
   assertNotNull(((EditText)a.findViewById(R.id.password)).getError());
  }
 }
 @Test public void registerRejectsMissingNameAndPasswordMismatch(){
  try(var controller=Robolectric.buildActivity(RegisterActivity.class).setup()){
   RegisterActivity a=controller.get();a.findViewById(R.id.create).performClick();
   assertNotNull(((EditText)a.findViewById(R.id.name)).getError());
   ((EditText)a.findViewById(R.id.name)).setText("Student");
   ((EditText)a.findViewById(R.id.email)).setText("student@example.com");
   ((EditText)a.findViewById(R.id.password)).setText("A-valid-pass123");
   ((EditText)a.findViewById(R.id.confirm)).setText("different-password");
   a.findViewById(R.id.create).performClick();
   assertNotNull(((EditText)a.findViewById(R.id.confirm)).getError());
  }
 }
}
