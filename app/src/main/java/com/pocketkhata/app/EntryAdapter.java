package com.pocketkhata.app;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.Holder> {
  public interface Click {
    void open(Entry e);
  }

  private final List<Entry> rows = new ArrayList<>();
  private final Click click;

  public EntryAdapter(Click c) {
    click = c;
  }

  public void submit(List<Entry> list) {
    rows.clear();
    rows.addAll(list);
    notifyDataSetChanged();
  }

  @NonNull
  public Holder onCreateViewHolder(@NonNull ViewGroup p, int type) {
    return new Holder(LayoutInflater.from(p.getContext()).inflate(R.layout.row_entry, p, false));
  }

  public void onBindViewHolder(@NonNull Holder h, int position) {
    Entry e = rows.get(position);
    h.title.setText(e.getTitle());
    String date = e.getDate();
    try {
      date = LocalDate.parse(date).format(DateTimeFormatter.ofPattern("d MMM", Locale.ENGLISH));
    } catch (Exception ignored) {
    }
    h.meta.setText((e.getPerson().isEmpty() ? e.getCategory() : e.getPerson()) + " · " + date);
    h.amount.setText(Money.format(e.getAmountPaisa()));
    h.type.setText(
        e.isLoan() && e.isSettled()
            ? "Paid · " + Labels.type(e.getType())
            : Labels.type(e.getType()));
    h.icon.setImageResource(
        "Lent".equals(e.getType())
            ? R.drawable.ic_arrow_out
            : "Borrowed".equals(e.getType()) ? R.drawable.ic_arrow_in : R.drawable.ic_wallet);
    h.itemView.setOnClickListener(v -> click.open(e));
  }

  public int getItemCount() {
    return rows.size();
  }

  static class Holder extends RecyclerView.ViewHolder {
    TextView title, meta, amount, type;
    ImageView icon;

    Holder(View v) {
      super(v);
      title = v.findViewById(R.id.entryTitle);
      meta = v.findViewById(R.id.entryMeta);
      amount = v.findViewById(R.id.entryAmount);
      type = v.findViewById(R.id.entryType);
      icon = v.findViewById(R.id.entryIcon);
    }
  }
}
