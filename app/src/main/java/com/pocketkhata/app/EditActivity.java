package com.pocketkhata.app;

import android.app.DatePickerDialog;
import android.content.*;
import android.net.Uri;
import android.os.*;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import androidx.annotation.NonNull;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
public class EditActivity extends BaseActivity {
 private static final String[] CATEGORIES={"Food","Transport","Study","Rent & bills","Shopping","Health","Entertainment","Other"};
 private Entry entry=new Entry();private Repository repo;private LocalDate date=LocalDate.now();private String photo="";private Uri cameraUri;private boolean editing,loadingPhoto,saving,connected;
 private DatabaseReference connectivity;private ValueEventListener connectionListener;private final ExecutorService worker=Executors.newSingleThreadExecutor();private final Handler handler=new Handler(Looper.getMainLooper());
 private final ActivityResultLauncher<String[]> gallery=registerForActivityResult(new ActivityResultContracts.OpenDocument(),uri->{if(uri!=null)loadPhoto(uri);});
 private final ActivityResultLauncher<Uri> camera=registerForActivityResult(new ActivityResultContracts.TakePicture(),ok->{if(ok&&cameraUri!=null)loadPhoto(cameraUri);});
 @Override protected void onCreate(Bundle b){super.onCreate(b);if(!requireLogin())return;screen(R.layout.activity_edit);toolbar();repo=new Repository(this);
  Spinner category=findViewById(R.id.category);ArrayAdapter<String> a=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,CATEGORIES);a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);category.setAdapter(a);
  ((RadioGroup)findViewById(R.id.type)).check(R.id.type_expense);String id=getIntent().getStringExtra("entry_id");editing=id!=null;if(editing){((com.google.android.material.appbar.MaterialToolbar)findViewById(R.id.toolbar)).setTitle("Edit entry");findViewById(R.id.save).setEnabled(false);}
  if(b!=null){date=LocalDate.parse(b.getString("date",LocalDate.now().toString()));photo=b.getString("photo","");String u=b.getString("camera");if(u!=null)cameraUri=Uri.parse(u);String cached=b.getString("entry");if(cached!=null){entry=fromJson(cached);}}
  if(editing&&b==null){repo.entry(id).addListenerForSingleValueEvent(new ValueEventListener(){public void onDataChange(@NonNull DataSnapshot s){if(isDestroyed())return;Entry value=s.getValue(Entry.class);if(value==null){android.widget.Toast.makeText(EditActivity.this,"This entry no longer exists",Toast.LENGTH_SHORT).show();finish();return;}entry=value;entry.setId(id);fill();findViewById(R.id.save).setEnabled(true);}public void onCancelled(@NonNull DatabaseError e){say("Could not load this entry");finish();}});}else if(editing){entry.setId(id);}
  updateDate();showPhoto();findViewById(R.id.date).setOnClickListener(v->pickDate());findViewById(R.id.gallery).setOnClickListener(v->{try{gallery.launch(new String[]{"image/*"});}catch(ActivityNotFoundException e){say("No image picker is available on this phone");}});findViewById(R.id.camera).setOnClickListener(v->takePhoto());findViewById(R.id.removePhoto).setOnClickListener(v->{photo="";showPhoto();});findViewById(R.id.save).setOnClickListener(v->save());
  connectivity=FirebaseDatabase.getInstance(getString(R.string.database_url)).getReference(".info/connected");connectionListener=new ValueEventListener(){public void onDataChange(@NonNull DataSnapshot s){connected=Boolean.TRUE.equals(s.getValue(Boolean.class));}public void onCancelled(@NonNull DatabaseError e){connected=false;}};connectivity.addValueEventListener(connectionListener);
 }
 private void fill(){((TextInputEditText)findViewById(R.id.title)).setText(entry.getTitle());((TextInputEditText)findViewById(R.id.amount)).setText(Money.editable(entry.getAmountPaisa()));((TextInputEditText)findViewById(R.id.person)).setText(entry.getPerson());((TextInputEditText)findViewById(R.id.phone)).setText(entry.getPhone());((TextInputEditText)findViewById(R.id.note)).setText(entry.getNote());((RadioGroup)findViewById(R.id.type)).check("Lent".equals(entry.getType())?R.id.type_lent:"Borrowed".equals(entry.getType())?R.id.type_borrowed:R.id.type_expense);for(int n=0;n<CATEGORIES.length;n++)if(CATEGORIES[n].equals(entry.getCategory()))((Spinner)findViewById(R.id.category)).setSelection(n);try{date=LocalDate.parse(entry.getDate());}catch(Exception ignored){}photo=entry.getPhoto();updateDate();showPhoto();}
 private void updateDate(){((MaterialButton)findViewById(R.id.date)).setText(date.format(DateTimeFormatter.ofPattern("EEEE, d MMM yyyy",Locale.ENGLISH)));}
 private void pickDate(){DatePickerDialog d=new DatePickerDialog(this,(v,y,m,day)->{date=LocalDate.of(y,m+1,day);updateDate();},date.getYear(),date.getMonthValue()-1,date.getDayOfMonth());d.getDatePicker().setMaxDate(System.currentTimeMillis());d.show();}
 private void takePhoto(){try{File dir=new File(getCacheDir(),"receipts");if(!dir.exists()&&!dir.mkdirs())throw new java.io.IOException();File file=File.createTempFile("receipt_",".jpg",dir);cameraUri=FileProvider.getUriForFile(this,getPackageName()+".fileprovider",file);camera.launch(cameraUri);}catch(Exception e){say("Could not open the camera. You can choose a gallery photo instead.");}}
 private void loadPhoto(Uri uri){loadingPhoto=true;findViewById(R.id.save).setEnabled(false);say("Preparing your photo…");worker.execute(()->{try{String result=Photos.read(this,uri);runOnUiThread(()->{if(isDestroyed())return;photo=result;loadingPhoto=false;showPhoto();findViewById(R.id.save).setEnabled(!saving);});}catch(Exception e){runOnUiThread(()->{if(isDestroyed())return;loadingPhoto=false;findViewById(R.id.save).setEnabled(!saving);say("Could not read this image. Please choose another photo.");});}});}
 private void showPhoto(){ImageView v=findViewById(R.id.photo);if(photo.isEmpty())v.setImageResource(R.drawable.ic_receipt);else v.setImageBitmap(Photos.decode(photo));findViewById(R.id.removePhoto).setVisibility(photo.isEmpty()?View.GONE:View.VISIBLE);}
 private void save(){if(loadingPhoto||saving)return;String title=input(R.id.title);if(title.isEmpty()){error(R.id.title,"Give this entry a title");return;}long amount;try{amount=Money.parse(input(R.id.amount));}catch(Exception e){error(R.id.amount,e.getMessage());return;}
  String phone=input(R.id.phone);if(!phone.isEmpty()&&!phone.matches("\\+?[0-9 ()-]{5,20}")){error(R.id.phone,"Enter a valid contact number or leave it empty");return;}
  int typeId=((RadioGroup)findViewById(R.id.type)).getCheckedRadioButtonId();String type=typeId==R.id.type_lent?"Lent":typeId==R.id.type_borrowed?"Borrowed":"Expense";
  if(!"Expense".equals(type)&&input(R.id.person).isEmpty()){error(R.id.person,"Enter the person’s name for this loan");return;}
  if(!connected){say("Connect to the internet before saving your entry");return;}
  boolean typeChanged=!type.equals(entry.getType());entry.setTitle(title);entry.setAmountPaisa(amount);entry.setType(type);entry.setCategory(((Spinner)findViewById(R.id.category)).getSelectedItem().toString());entry.setDate(date.toString());entry.setPerson(input(R.id.person));entry.setPhone(phone);entry.setNote(input(R.id.note));entry.setPhoto(photo);if(typeChanged||!entry.isLoan())entry.setSettled(false);long now=System.currentTimeMillis();if(entry.getCreatedAt()==0)entry.setCreatedAt(now);entry.setUpdatedAt(now);busy(true);
  handler.postDelayed(()->{if(saving&&!isDestroyed()){say("Still syncing. Stay online; this entry will appear in your khata when saved.");}},15000);
  repo.save(entry,error->{if(isDestroyed()||isFinishing())return;busy(false);if(error!=null)say(error);else{Toast.makeText(this,editing?"Entry updated":"Entry saved",Toast.LENGTH_SHORT).show();finish();}});
 }
 private void busy(boolean value){saving=value;findViewById(R.id.save).setEnabled(!value);findViewById(R.id.progress).setVisibility(value?View.VISIBLE:View.GONE);}
 @Override protected void onSaveInstanceState(@NonNull Bundle b){super.onSaveInstanceState(b);b.putString("date",date.toString());b.putString("photo",photo);if(cameraUri!=null)b.putString("camera",cameraUri.toString());b.putString("entry",toJson(entry));}
 private String toJson(Entry e){org.json.JSONObject o=new org.json.JSONObject();try{o.put("created",e.getCreatedAt());o.put("settled",e.isSettled());o.put("type",e.getType());}catch(Exception ignored){}return o.toString();}
 private Entry fromJson(String s){Entry e=new Entry();try{org.json.JSONObject o=new org.json.JSONObject(s);e.setCreatedAt(o.optLong("created"));e.setSettled(o.optBoolean("settled"));e.setType(o.optString("type","Expense"));}catch(Exception ignored){}return e;}
 @Override protected void onDestroy(){handler.removeCallbacksAndMessages(null);worker.shutdown();if(connectivity!=null&&connectionListener!=null)connectivity.removeEventListener(connectionListener);super.onDestroy();}
}
