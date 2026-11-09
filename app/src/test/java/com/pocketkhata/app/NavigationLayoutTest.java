package com.pocketkhata.app;

import static org.junit.Assert.*;

import android.app.Application;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.test.core.app.ApplicationProvider;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 32, application = Application.class, qualifiers = "w360dp-h800dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class NavigationLayoutTest {
  public static class Host extends BaseActivity {
    @Override
    protected void onCreate(Bundle b) {
      setTheme(R.style.Theme_PocketKhata);
      super.onCreate(b);
      screen(R.layout.activity_settings);
    }
  }

  @Test
  public void systemInsetsDoNotShrinkNavigationIconsOrLabels() {
    try (var controller = Robolectric.buildActivity(Host.class).setup()) {
      Host activity = controller.get();
      View root = activity.findViewById(R.id.root);
      BottomNavigationView nav = activity.findViewById(R.id.navigation);
      for (int bottom : new int[] {48, 96}) {
        WindowInsetsCompat insets =
            new WindowInsetsCompat.Builder()
                .setInsets(WindowInsetsCompat.Type.statusBars(), Insets.of(0, 48, 0, 0))
                .setInsets(WindowInsetsCompat.Type.navigationBars(), Insets.of(0, 0, 0, bottom))
                .build();
        WindowInsetsCompat remaining = ViewCompat.dispatchApplyWindowInsets(root, insets);
        root.measure(
            View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1600, View.MeasureSpec.EXACTLY));
        root.layout(0, 0, 720, 1600);
        assertTrue(remaining.isConsumed());
        assertEquals(bottom, root.getPaddingBottom());
        assertEquals(
            "Material navigation must not reserve the bottom inset again",
            0,
            nav.getPaddingBottom());
        for (int selected : new int[] {R.id.nav_home, R.id.nav_loans, R.id.nav_settings}) {
          nav.setSelectedItemId(selected);
          root.measure(
              View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY),
              View.MeasureSpec.makeMeasureSpec(1600, View.MeasureSpec.EXACTLY));
          root.layout(0, 0, 720, 1600);
          View item = nav.findViewById(selected);
          View icon =
              item.findViewById(com.google.android.material.R.id.navigation_bar_item_icon_view);
          TextView label =
              item.findViewById(
                  com.google.android.material.R.id.navigation_bar_item_large_label_view);
          Rect a = new Rect(0, 0, icon.getWidth(), icon.getHeight()),
              b = new Rect(0, 0, label.getWidth(), label.getHeight());
          nav.offsetDescendantRectToMyCoords(icon, a);
          nav.offsetDescendantRectToMyCoords(label, b);
          assertTrue("Selected destination needs a readable label", label.getHeight() > 0);
          assertTrue(
              "Icon and label must fit vertically inside the bar",
              a.top >= 0 && b.bottom <= nav.getHeight());
          assertTrue("Icon must stay above its label", a.bottom <= b.top + 4);
        }
      }
    }
  }

  @Test
  public void allFourHomeFiltersAndBothLoanFiltersAreVisible() {
    var context =
        new ContextThemeWrapper(
            ApplicationProvider.getApplicationContext(), R.style.Theme_PocketKhata);
    View root = LayoutInflater.from(context).inflate(R.layout.dashboard_header, null);
    DashboardFilters filters = root.findViewById(R.id.filters);
    filters.showLoansOnly(false);
    root.measure(
        View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
    root.layout(0, 0, 720, root.getMeasuredHeight());
    root.measure(
        View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
    root.layout(0, 0, 720, root.getMeasuredHeight());
    for (int id :
        new int[] {R.id.filter_all, R.id.filter_expense, R.id.filter_lent, R.id.filter_borrowed})
      assertEquals(View.VISIBLE, root.findViewById(id).getVisibility());
    View give = root.findViewById(R.id.filter_lent), take = root.findViewById(R.id.filter_borrowed);
    assertEquals("Loan buttons should form a readable second row", give.getTop(), take.getTop());
    assertTrue(take.getRight() <= filters.getWidth());
    assertEquals("Give a Loan", ((TextView) give).getText().toString());
    assertEquals("Take a Loan", ((TextView) take).getText().toString());
    filters.showLoansOnly(true);
    assertEquals(View.GONE, root.findViewById(R.id.filter_expense).getVisibility());
    assertEquals(View.VISIBLE, give.getVisibility());
    assertEquals(View.VISIBLE, take.getVisibility());
  }
}
