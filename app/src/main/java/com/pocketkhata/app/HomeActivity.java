package com.pocketkhata.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.*;
import android.view.View;
import androidx.recyclerview.widget.*;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.*;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
public class HomeActivity extends BaseActivity {
 private Repository repo;private ValueEventListener listener,connectionListener;private DatabaseReference connection;
 private List<Entry> entries=new ArrayList<>();private EntryAdapter adapter;private String query="",filter="All";private boolean loaded;
 @Override protected void onCreate(Bundle b){super.onCreate(b);if(!requireLogin())return;screen(R.layout.activity_home);repo=new Repository(this);adapter=new EntryAdapter(e->{Intent i=new Intent(this,DetailActivity.class);i.putExtra("entry_id",e.getId());startActivity(i);});RecyclerView list=findViewById(R.id.list);list.setLayoutManager(new LinearLayoutManager(this));list.setAdapter(adapter);
  findViewById(R.id.add).setOnClickListener(v->startActivity(new Intent(this,EditActivity.class)));findViewById(R.id.settings).setOnClickListener(v->startActivity(new Intent(this,SettingsActivity.class)));
  ((android.widget.EditText)findViewById(R.id.search)).addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int c,int a){}public void onTextChanged(CharSequence s,int st,int before,int count){query=s.toString().toLowerCase(Locale.ROOT).trim();render();}public void afterTextChanged(Editable e){}});
  ((ChipGroup)findViewById(R.id.filters)).setOnCheckedStateChangeListener((g,ids)->{int id=ids.isEmpty()?R.id.filter_all:ids.get(0);filter=id==R.id.filter_expense?"Expense":id==R.id.filter_lent?"Lent":id==R.id.filter_borrowed?"Borrowed":"All";render();});
 }
 @Override protected void onStart(){super.onStart();if(repo==null)return;if(!requireLogin())return;String n=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();label(R.id.greeting,n==null||n.trim().isEmpty()?"YOUR LITTLE MONEY COMPANION":"HELLO, "+n.toUpperCase(Locale.ROOT));
  listener=repo.listen(new Repository.Rows(){public void loaded(List<Entry> rows){entries=rows;loaded=true;render();}public void failed(String m){say(m);label(R.id.syncStatus,"Unable to load records. Check your connection.");}});
  connection=FirebaseDatabase.getInstance(getString(R.string.database_url)).getReference(".info/connected");connectionListener=new ValueEventListener(){public void onDataChange(DataSnapshot s){boolean online=Boolean.TRUE.equals(s.getValue(Boolean.class));label(R.id.syncStatus,online?"Connected · your khata syncs in real time":"Offline · showing saved records; changes will sync when online");}public void onCancelled(DatabaseError e){label(R.id.syncStatus,"Check your internet connection");}};connection.addValueEventListener(connectionListener);
 }
 @Override protected void onStop(){if(repo!=null)repo.remove(listener);if(connection!=null&&connectionListener!=null)connection.removeEventListener(connectionListener);super.onStop();}
 private void render(){if(adapter==null)return;Summary s=Summary.calculate(entries,YearMonth.now());label(R.id.monthLabel,"SPENT · "+YearMonth.now().format(DateTimeFormatter.ofPattern("MMMM yyyy",Locale.ENGLISH)).toUpperCase(Locale.ROOT));label(R.id.spent,Money.format(s.spent));label(R.id.lent,Money.format(s.lent));label(R.id.borrowed,Money.format(s.borrowed));long budget=Prefs.user(this).getLong("budget",0);((LinearProgressIndicator)findViewById(R.id.budgetProgress)).setProgress(budget>0?(int)Math.min(100,s.spent*100.0/budget):0);label(R.id.budgetText,budget==0?"Set a monthly budget in Settings":s.spent>budget?Money.format(s.spent-budget)+" over your "+Money.format(budget)+" budget":Money.format(budget-s.spent)+" left of "+Money.format(budget));
  boolean show=Prefs.user(this).getBoolean("showSettled",true);List<Entry> visible=new ArrayList<>();for(Entry e:entries){if(!show&&e.isLoan()&&e.isSettled())continue;if(!filter.equals("All")&&!filter.equals(e.getType()))continue;String hay=(e.getTitle()+" "+e.getPerson()+" "+e.getCategory()+" "+e.getNote()).toLowerCase(Locale.ROOT);if(!hay.contains(query))continue;visible.add(e);}adapter.submit(visible);findViewById(R.id.empty).setVisibility(visible.isEmpty()&&loaded?View.VISIBLE:View.GONE);label(R.id.emptyTitle,entries.isEmpty()?"A fresh page awaits.":"No matching entries.");label(R.id.emptySubtitle,entries.isEmpty()?"Tap New entry to start your khata.":"Try another search or filter.");
 }
}
