package com.pocketkhata.app;

import static org.junit.Assert.*;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.*;
import androidx.test.core.app.ApplicationProvider;
import java.io.*;
import java.util.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 32, application = Application.class, qualifiers = "w360dp-h760dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class ScreenAndPhotoTest {
  private Context context() {
    return new ContextThemeWrapper(
        ApplicationProvider.getApplicationContext(), R.style.Theme_PocketKhata);
  }

  @Test
  public void allSevenScreensInflateOnAndroid12() {
    for (int id :
        new int[] {
          R.layout.activity_splash,
          R.layout.activity_login,
          R.layout.activity_register,
          R.layout.activity_home,
          R.layout.activity_edit,
          R.layout.activity_detail,
          R.layout.activity_settings
        }) assertNotNull(LayoutInflater.from(context()).inflate(id, null).findViewById(R.id.root));
  }

  @Test
  public void formOnlyAsksForRelevantInformation() {
    View root = LayoutInflater.from(context()).inflate(R.layout.activity_edit, null);
    EntryFormUi.bind(root);
    RadioGroup type = root.findViewById(R.id.type);
    type.check(R.id.type_expense);
    assertEquals(View.VISIBLE, root.findViewById(R.id.titleLayout).getVisibility());
    assertEquals(View.VISIBLE, root.findViewById(R.id.category).getVisibility());
    assertEquals(View.GONE, root.findViewById(R.id.personLayout).getVisibility());
    type.check(R.id.type_lent);
    assertEquals(View.GONE, root.findViewById(R.id.titleLayout).getVisibility());
    assertEquals(View.GONE, root.findViewById(R.id.category).getVisibility());
    assertEquals(View.VISIBLE, root.findViewById(R.id.personLayout).getVisibility());
    assertEquals(
        "To (Name)",
        ((com.google.android.material.textfield.TextInputLayout)
                root.findViewById(R.id.personLayout))
            .getHint()
            .toString());
    type.check(R.id.type_borrowed);
    assertEquals(
        "From (Name)",
        ((com.google.android.material.textfield.TextInputLayout)
                root.findViewById(R.id.personLayout))
            .getHint()
            .toString());
    assertEquals(View.GONE, root.findViewById(R.id.extras).getVisibility());
  }

  @Test
  public void receiptIsResizedAndExifRotationIsRespected() throws Exception {
    Context c = context();
    File f = File.createTempFile("receipt", ".jpg", c.getCacheDir());
    Bitmap b = Bitmap.createBitmap(2400, 1200, Bitmap.Config.ARGB_8888);
    b.eraseColor(0xffdddddd);
    try (FileOutputStream out = new FileOutputStream(f)) {
      b.compress(Bitmap.CompressFormat.JPEG, 90, out);
    }
    b.recycle();
    ExifInterface exif = new ExifInterface(f);
    exif.setAttribute(ExifInterface.TAG_ORIENTATION, "6");
    exif.saveAttributes();
    String encoded = Photos.read(c, Uri.fromFile(f));
    Bitmap actual = Photos.decode(encoded);
    assertNotNull(actual);
    assertTrue(actual.getHeight() > actual.getWidth());
    assertTrue(actual.getHeight() <= 900);
    assertTrue(encoded.length() < 170000);
    actual.recycle();
    assertTrue(f.delete());
  }

  @Test
  public void badPhotoDoesNotCrashDecoder() {
    assertNull(Photos.decode("not-an-image"));
  }

  private View home(boolean noRecords) {
    Context c = context();
    View root = LayoutInflater.from(c).inflate(R.layout.activity_home, null);
    RecyclerView list = root.findViewById(R.id.list);
    list.setLayoutManager(new LinearLayoutManager(c));
    list.setItemAnimator(null);
    View header = LayoutInflater.from(c).inflate(R.layout.dashboard_header, list, false);
    ((TextView) header.findViewById(R.id.greeting)).setText("Sample data · design preview");
    ((TextView) header.findViewById(R.id.spent)).setText("৳ 3,450.50");
    ((TextView) header.findViewById(R.id.lent)).setText("৳ 800.00");
    ((TextView) header.findViewById(R.id.borrowed)).setText("৳ 250.00");
    ((TextView) header.findViewById(R.id.budgetText)).setText("৳ 1,549.50 left of ৳ 5,000.00");
    ((TextView) header.findViewById(R.id.syncStatus)).setText("Sample records for visual review");
    EntryAdapter adapter = new EntryAdapter(e -> {});
    List<Entry> rows = new ArrayList<>();
    if (!noRecords) {
      String[] types = {"Expense", "Lent", "Borrowed"};
      String[] names = {"Lunch at campus", "Loan to Manik", "Loan from Asha"};
      for (int i = 0; i < 3; i++) {
        Entry e = new Entry();
        e.setTitle(names[i]);
        e.setType(types[i]);
        e.setDate("2026-11-09");
        e.setCategory("Food");
        e.setAmountPaisa(i == 0 ? 12550 : i == 1 ? 80000 : 25000);
        rows.add(e);
      }
    }
    adapter.submit(rows);
    View empty = LayoutInflater.from(c).inflate(R.layout.row_empty, list, false);
    list.setAdapter(
        new ConcatAdapter(
            new SingleViewAdapter(header, true), adapter, new SingleViewAdapter(empty, noRecords)));
    return root;
  }

  private void size(View root) {
    root.measure(
        View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(1472, View.MeasureSpec.EXACTLY));
    root.layout(0, 0, 720, 1472);
  }

  @Test
  public void emptyStateCanBeScrolledAboveTheButton() {
    ViewGroup root = (ViewGroup) home(true);
    size(root);
    RecyclerView list = root.findViewById(R.id.list);
    list.scrollBy(0, 2000);
    size(root);
    View subtitle = root.findViewById(R.id.emptySubtitle);
    assertNotNull(subtitle);
    View button = root.findViewById(R.id.add);
    android.graphics.Rect
        a = new android.graphics.Rect(0, 0, subtitle.getWidth(), subtitle.getHeight()),
        b = new android.graphics.Rect(0, 0, button.getWidth(), button.getHeight());
    root.offsetDescendantRectToMyCoords(subtitle, a);
    root.offsetDescendantRectToMyCoords(button, b);
    assertTrue("Last content must be scrollable above Add button", a.bottom < b.top);
  }

  @Test
  public void navigationHasThreeReadableDestinations() {
    View root = home(false);
    com.google.android.material.bottomnavigation.BottomNavigationView nav =
        root.findViewById(R.id.navigation);
    assertEquals(3, nav.getMenu().size());
    assertEquals("Home", nav.getMenu().findItem(R.id.nav_home).getTitle());
    assertEquals("Loans", nav.getMenu().findItem(R.id.nav_loans).getTitle());
    assertEquals("Settings", nav.getMenu().findItem(R.id.nav_settings).getTitle());
  }

  @Test
  public void renderUpdatedScreens() throws Exception {
    File out = new File("build/outputs/ui-previews");
    assertTrue(out.isDirectory() || out.mkdirs());
    for (String name :
        new String[] {"login", "home-sample", "add-expense", "add-loan", "settings"}) {
      View root;
      if (name.equals("home-sample")) root = home(false);
      else {
        int id =
            name.equals("login")
                ? R.layout.activity_login
                : name.equals("settings") ? R.layout.activity_settings : R.layout.activity_edit;
        root = LayoutInflater.from(context()).inflate(id, null);
        if (name.startsWith("add-")) {
          EntryFormUi.bind(root);
          ((RadioGroup) root.findViewById(R.id.type))
              .check(name.equals("add-loan") ? R.id.type_lent : R.id.type_expense);
        }
      }
      size(root);
      Bitmap bitmap = Bitmap.createBitmap(720, 1472, Bitmap.Config.ARGB_8888);
      root.draw(new android.graphics.Canvas(bitmap));
      try (FileOutputStream stream = new FileOutputStream(new File(out, name + ".png"))) {
        assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream));
      }
      bitmap.recycle();
    }
  }
}
