package com.pocketkhata.app;

import static org.junit.Assert.*;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import androidx.exifinterface.media.ExifInterface;
import androidx.test.core.app.ApplicationProvider;
import java.io.File;
import java.io.FileOutputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 32, application = Application.class, qualifiers = "w360dp-h760dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class ScreenAndPhotoTest {
  @Test
  public void allSevenScreensInflateOnAndroid12() {
    Context context =
        new ContextThemeWrapper(
            ApplicationProvider.getApplicationContext(), R.style.Theme_PocketKhata);
    int[] layouts = {
      R.layout.activity_splash,
      R.layout.activity_login,
      R.layout.activity_register,
      R.layout.activity_home,
      R.layout.activity_edit,
      R.layout.activity_detail,
      R.layout.activity_settings
    };
    for (int id : layouts) {
      View view = LayoutInflater.from(context).inflate(id, null);
      assertNotNull(view.findViewById(R.id.root));
    }
  }

  @Test
  public void receiptIsResizedAndExifRotationIsRespected() throws Exception {
    Context c = ApplicationProvider.getApplicationContext();
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
  public void renderLoginAndSampleDashboard() throws Exception {
    Context c = new ContextThemeWrapper(ApplicationProvider.getApplicationContext(), R.style.Theme_PocketKhata);
    File out = new File("build/outputs/ui-previews");
    assertTrue(out.isDirectory() || out.mkdirs());
    for (int layout : new int[]{R.layout.activity_login, R.layout.activity_home}) {
      View root = LayoutInflater.from(c).inflate(layout, null);
      if (layout == R.layout.activity_home) {
        ((android.widget.TextView) root.findViewById(R.id.greeting)).setText("SAMPLE DATA · DESIGN PREVIEW");
        ((android.widget.TextView) root.findViewById(R.id.spent)).setText("৳ 3,450.50");
        ((android.widget.TextView) root.findViewById(R.id.lent)).setText("৳ 800.00");
        ((android.widget.TextView) root.findViewById(R.id.borrowed)).setText("৳ 250.00");
        ((android.widget.TextView) root.findViewById(R.id.budgetText)).setText("৳ 1,549.50 left of ৳ 5,000.00");
        ((com.google.android.material.progressindicator.LinearProgressIndicator) root.findViewById(R.id.budgetProgress)).setProgress(69);
        ((android.widget.TextView) root.findViewById(R.id.syncStatus)).setText("Sample entries for visual review");
        root.findViewById(R.id.empty).setVisibility(View.GONE);
        androidx.recyclerview.widget.RecyclerView list = root.findViewById(R.id.list);
        list.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(c));
        EntryAdapter adapter = new EntryAdapter(e -> {});
        list.setAdapter(adapter);
        java.util.List<Entry> entries = new java.util.ArrayList<>();
        for (String title : new String[]{"Lunch at campus", "Money lent to a classmate", "Bus to university"}) {
          Entry e = new Entry();e.setTitle(title);e.setDate("2026-11-09");e.setCategory("Food");e.setAmountPaisa(12550);entries.add(e);
        }
        adapter.submit(entries);
      }
      int width = 720, height = 1520;
      root.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
      root.layout(0, 0, width, height);
      Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
      root.draw(new android.graphics.Canvas(bitmap));
      try (FileOutputStream stream = new FileOutputStream(new File(out, layout == R.layout.activity_login ? "login.png" : "home-sample.png"))) { assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)); }
      bitmap.recycle();
    }
  }
}
