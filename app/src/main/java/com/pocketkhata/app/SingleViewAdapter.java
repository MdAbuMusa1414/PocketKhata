package com.pocketkhata.app;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public final class SingleViewAdapter extends RecyclerView.Adapter<SingleViewAdapter.Holder> {
  private final View view;
  private boolean shown;

  public SingleViewAdapter(View view, boolean shown) {
    this.view = view;
    this.shown = shown;
  }

  public void setShown(boolean value) {
    if (shown == value) return;
    shown = value;
    if (value) notifyItemInserted(0);
    else notifyItemRemoved(0);
  }

  @NonNull
  @Override
  public Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
    return new Holder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull Holder holder, int position) {}

  @Override
  public int getItemCount() {
    return shown ? 1 : 0;
  }

  static final class Holder extends RecyclerView.ViewHolder {
    Holder(View view) {
      super(view);
    }
  }
}
