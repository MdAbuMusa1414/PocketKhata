package com.pocketkhata.app;

import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputLayout;

/** Shows only the information needed for the selected kind of record. */
public final class EntryFormUi {
  private EntryFormUi() {}

  public static void bind(View root) {
    RadioGroup type = root.findViewById(R.id.type);
    type.setOnCheckedChangeListener((group, checked) -> apply(root, checked));
    apply(root, type.getCheckedRadioButtonId());
  }

  public static void apply(View root, int typeId) {
    boolean expense = typeId == R.id.type_expense;
    root.findViewById(R.id.titleLayout).setVisibility(expense ? View.VISIBLE : View.GONE);
    root.findViewById(R.id.category).setVisibility(expense ? View.VISIBLE : View.GONE);
    root.findViewById(R.id.categoryLabel).setVisibility(expense ? View.VISIBLE : View.GONE);
    root.findViewById(R.id.personLayout).setVisibility(expense ? View.GONE : View.VISIBLE);
    ((TextInputLayout) root.findViewById(R.id.personLayout))
        .setHint(typeId == R.id.type_lent ? "To (Name)" : "From (Name)");
    ((TextView) root.findViewById(R.id.typeHelp))
        .setText(
            expense
                ? "You paid for something."
                : typeId == R.id.type_lent
                    ? "Someone will pay you back."
                    : "You will pay someone back.");
  }
}
