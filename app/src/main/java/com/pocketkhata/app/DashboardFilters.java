package com.pocketkhata.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.google.android.material.chip.Chip;

/** Two fixed, weighted rows keep all four filter labels readable on narrow phones. */
public final class DashboardFilters extends LinearLayout {
  public interface OnSelectionChangedListener {
    void selected(int id);
  }

  private OnSelectionChangedListener listener;
  private int selectedId = R.id.filter_all;
  private final int[] ids = {
    R.id.filter_all, R.id.filter_expense, R.id.filter_lent, R.id.filter_borrowed
  };

  public DashboardFilters(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    for (int id : ids) findViewById(id).setOnClickListener(v -> check(v.getId()));
    check(R.id.filter_all);
  }

  public void setOnSelectionChangedListener(OnSelectionChangedListener value) {
    listener = value;
  }

  public void check(int id) {
    boolean changed = selectedId != id;
    selectedId = id;
    for (int childId : ids) ((Chip) findViewById(childId)).setChecked(childId == id);
    if (changed && listener != null) listener.selected(id);
  }

  public void showAllRecords() {
    findViewById(R.id.filter_expense).setVisibility(View.VISIBLE);
    findViewById(R.id.filter_lent).setVisibility(View.VISIBLE);
    findViewById(R.id.filter_borrowed).setVisibility(View.VISIBLE);
    ((Chip) findViewById(R.id.filter_all)).setText("All");
    check(R.id.filter_all);
  }
}
