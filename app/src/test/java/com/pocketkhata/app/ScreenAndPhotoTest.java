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
  public void allNineScreensInflateOnAndroid12() {
    for (int id :
        new int[] {
          R.layout.activity_splash,
          R.layout.activity_login,
          R.layout.activity_register,
          R.layout.activity_home,
          R.layout.activity_edit,
          R.layout.activity_detail,
          R.layout.activity_settings,
          R.layout.activity_summary,
          R.layout.activity_profile
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

  @Test
  public void profilePhotoCanBeReplacedAndRemovedWithoutKeepingTheOldImage() {
    ImageView avatar = new ImageView(context());
    avatar.setLayoutParams(new ViewGroup.LayoutParams(92, 92));
    Bitmap image = Bitmap.createBitmap(80, 60, Bitmap.Config.ARGB_8888);
    image.eraseColor(0xffa47990);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    image.compress(Bitmap.CompressFormat.JPEG, 80, out);
    String encoded =
        android.util.Base64.encodeToString(out.toByteArray(), android.util.Base64.NO_WRAP);
    ProfilePhotos.bind(avatar, encoded);
    assertEquals(ImageView.ScaleType.CENTER_CROP, avatar.getScaleType());
    assertEquals(0, avatar.getPaddingLeft());
    assertTrue(avatar.getDrawable() instanceof android.graphics.drawable.BitmapDrawable);
    ProfilePhotos.bind(avatar, "");
    assertEquals(ImageView.ScaleType.CENTER_INSIDE, avatar.getScaleType());
    assertTrue(avatar.getPaddingLeft() > 0);
    assertFalse(avatar.getDrawable() instanceof android.graphics.drawable.BitmapDrawable);
    ProfilePhotos.bind(avatar, "invalid image data");
    assertNotNull(avatar.getDrawable());
    image.recycle();
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
    ((TextView) header.findViewById(R.id.budgetAmount)).setText("৳ 5,000.00");
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
    root.measure(
        View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(1472, View.MeasureSpec.EXACTLY));
    root.layout(0, 0, 720, 1472);
  }

  @Test
  public void emptyStateCanBeScrolledAboveNavigationAndBelowTheAddButton() {
    ViewGroup root = (ViewGroup) home(true);
    size(root);
    RecyclerView list = root.findViewById(R.id.list);
    list.scrollBy(0, 2000);
    size(root);
    View subtitle = root.findViewById(R.id.emptySubtitle);
    assertNotNull(subtitle);
    View button = root.findViewById(R.id.navigation);
    android.graphics.Rect
        a = new android.graphics.Rect(0, 0, subtitle.getWidth(), subtitle.getHeight()),
        b = new android.graphics.Rect(0, 0, button.getWidth(), button.getHeight());
    root.offsetDescendantRectToMyCoords(subtitle, a);
    root.offsetDescendantRectToMyCoords(button, b);
    assertTrue("Last content must be scrollable above navigation", a.bottom < b.top);
    android.graphics.Rect action = new android.graphics.Rect();
    View add = root.findViewById(R.id.add);
    action.set(0, 0, add.getWidth(), add.getHeight());
    root.offsetDescendantRectToMyCoords(add, action);
    assertTrue(
        "Add action must stay in the header, clear of record content",
        action.bottom <= list.getTop());
  }

  @Test
  public void navigationHasThreeReadableDestinations() {
    View root = home(false);
    com.google.android.material.bottomnavigation.BottomNavigationView nav =
        root.findViewById(R.id.navigation);
    assertEquals(3, nav.getMenu().size());
    assertEquals("Home", nav.getMenu().findItem(R.id.nav_home).getTitle());
    assertEquals("Summary", nav.getMenu().findItem(R.id.nav_summary).getTitle());
    assertEquals("Settings", nav.getMenu().findItem(R.id.nav_settings).getTitle());
  }

  @Test
  public void settingsIsOnlyInTheBottomNavigation() {
    View root = home(false);
    assertEquals(0, settingsShortcutCount(root));
    com.google.android.material.bottomnavigation.BottomNavigationView nav =
        root.findViewById(R.id.navigation);
    assertEquals("Settings", nav.getMenu().findItem(R.id.nav_settings).getTitle());
  }

  private int settingsShortcutCount(View view) {
    int count =
        view instanceof ImageButton
                && "Settings"
                    .contentEquals(
                        view.getContentDescription() == null ? "" : view.getContentDescription())
            ? 1
            : 0;
    if (view instanceof ViewGroup) {
      ViewGroup group = (ViewGroup) view;
      for (int i = 0; i < group.getChildCount(); i++)
        count += settingsShortcutCount(group.getChildAt(i));
    }
    return count;
  }

  @Test
  public void threeFinanceBoxesFitWithoutClippingMoney() {
    ViewGroup header =
        (ViewGroup) LayoutInflater.from(context()).inflate(R.layout.dashboard_header, null);
    ((TextView) header.findViewById(R.id.spent)).setText("৳ 888,888.99");
    header.measure(
        View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
    header.layout(0, 0, 720, header.getMeasuredHeight());
    View a = header.findViewById(R.id.expenseTotalCard),
        b = header.findViewById(R.id.givenTotalCard),
        c = header.findViewById(R.id.takenTotalCard);
    assertTrue(a.getRight() <= b.getLeft());
    assertTrue(b.getRight() <= c.getLeft());
    TextView amount = header.findViewById(R.id.spent);
    assertEquals(1, amount.getLineCount());
    assertTrue(
        amount.getLayout().getLineWidth(0)
            <= amount.getWidth() - amount.getPaddingLeft() - amount.getPaddingRight() + 1);
  }

  @Test
  public void renderUpdatedScreens() throws Exception {
    File out = new File("build/outputs/ui-previews");
    assertTrue(out.isDirectory() || out.mkdirs());
    android.graphics.drawable.Drawable logo = context().getDrawable(R.drawable.ic_logo);
    Bitmap brand = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
    logo.setBounds(0, 0, 512, 512);
    logo.draw(new android.graphics.Canvas(brand));
    try (FileOutputStream stream = new FileOutputStream(new File(out, "brand-mark.png"))) {
      brand.compress(Bitmap.CompressFormat.PNG, 100, stream);
    }
    brand.recycle();

    for (String name :
        new String[] {
          "login",
          "register",
          "home-sample",
          "add-expense",
          "add-loan",
          "details",
          "settings",
          "summary",
          "profile"
        }) {
      View root;
      if (name.equals("home-sample")) root = home(false);
      else {
        int id =
            name.equals("login")
                ? R.layout.activity_login
                : name.equals("register")
                    ? R.layout.activity_register
                    : name.equals("details")
                        ? R.layout.activity_detail
                        : name.equals("settings")
                            ? R.layout.activity_settings
                            : name.equals("summary")
                                ? R.layout.activity_summary
                                : name.equals("profile")
                                    ? R.layout.activity_profile
                                    : R.layout.activity_edit;
        root = LayoutInflater.from(context()).inflate(id, null);
        if (name.equals("details")) {
          ((TextView) root.findViewById(R.id.typeLabel)).setText("Loan Given");
          ((TextView) root.findViewById(R.id.detailAmount)).setText("৳ 800.00");
          ((TextView) root.findViewById(R.id.detailTitle)).setText("Loan to Sample Contact");
          ((TextView) root.findViewById(R.id.detailMeta)).setText("2026-11-10");
          ((TextView) root.findViewById(R.id.detailStatus)).setText("Not paid back yet");
          ((TextView) root.findViewById(R.id.detailPerson)).setText("Sample Contact\n01700000000");
          root.findViewById(R.id.detailNote).setVisibility(View.GONE);
        }
        if (name.equals("profile")) {
          ((TextView) root.findViewById(R.id.profileName)).setText("Sample Student");
          ((TextView) root.findViewById(R.id.profilePhone)).setText("01700000000");
          ((TextView) root.findViewById(R.id.profileEmail)).setText("student@example.com");
          ((TextView) root.findViewById(R.id.profileStatus))
              .setText("Sample profile · design preview");
          root.findViewById(R.id.profileProgress).setVisibility(View.GONE);
        }
        if (name.equals("summary")) {
          ((TextView) root.findViewById(R.id.summaryTotal)).setText("৳ 3,450.50");
          ((TextView) root.findViewById(R.id.summaryCount)).setText("9 expenses recorded");
          ((TextView) root.findViewById(R.id.summaryAverage)).setText("৳ 383.39");
          ((TextView) root.findViewById(R.id.summaryTop)).setText("Food");
          ((TextView) root.findViewById(R.id.summaryStatus))
              .setText("Sample data · design preview");
          root.findViewById(R.id.emptyCategories).setVisibility(View.GONE);
          ((com.google.android.material.bottomnavigation.BottomNavigationView)
                  root.findViewById(R.id.navigation))
              .setSelectedItemId(R.id.nav_summary);
          LinearLayout categories = root.findViewById(R.id.categoryList);
          String[] names = {"Food", "Study", "Transport"};
          long[] amounts = {180000, 85050, 80000};
          for (int i = 0; i < names.length; i++) {
            View row =
                LayoutInflater.from(context())
                    .inflate(R.layout.row_category_summary, categories, false);
            ((TextView) row.findViewById(R.id.categoryName)).setText(names[i]);
            ((TextView) row.findViewById(R.id.categoryAmount)).setText(Money.format(amounts[i]));
            categories.addView(row);
          }
        }
        if (name.startsWith("add-")) {
          EntryFormUi.bind(root);
          ((RadioGroup) root.findViewById(R.id.type))
              .check(name.equals("add-loan") ? R.id.type_lent : R.id.type_expense);
        }
      }
      // Attach layouts so Material hint/indicator animations reach their final displayed state.
      try (var controller =
          org.robolectric.Robolectric.buildActivity(android.app.Activity.class).setup().visible()) {
        controller.get().setContentView(root);
        org.robolectric.shadows.ShadowLooper.idleMainLooper(
            1, java.util.concurrent.TimeUnit.SECONDS);
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
}
