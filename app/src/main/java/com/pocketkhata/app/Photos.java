package com.pocketkhata.app;

import android.content.Context;
import android.graphics.*;
import android.net.Uri;
import android.util.Base64;
import androidx.exifinterface.media.ExifInterface;
import java.io.*;

public final class Photos {
  public static String read(Context c, Uri uri) throws IOException {
    BitmapFactory.Options bounds = new BitmapFactory.Options();
    bounds.inJustDecodeBounds = true;
    try (InputStream in = c.getContentResolver().openInputStream(uri)) {
      BitmapFactory.decodeStream(in, null, bounds);
    }
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0)
      throw new IOException("This file is not a supported image");
    BitmapFactory.Options opts = new BitmapFactory.Options();
    opts.inSampleSize = 1;
    while (Math.max(bounds.outWidth, bounds.outHeight) / opts.inSampleSize > 1440)
      opts.inSampleSize *= 2;
    Bitmap bitmap;
    try (InputStream in = c.getContentResolver().openInputStream(uri)) {
      bitmap = BitmapFactory.decodeStream(in, null, opts);
    }
    if (bitmap == null) throw new IOException("Cannot open this image");
    Matrix matrix = new Matrix();
    try (InputStream in = c.getContentResolver().openInputStream(uri)) {
      int o =
          new ExifInterface(in)
              .getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
      switch (o) {
        case 2:
          matrix.setScale(-1, 1);
          break;
        case 3:
          matrix.setRotate(180);
          break;
        case 4:
          matrix.setRotate(180);
          matrix.postScale(-1, 1);
          break;
        case 5:
          matrix.setRotate(90);
          matrix.postScale(-1, 1);
          break;
        case 6:
          matrix.setRotate(90);
          break;
        case 7:
          matrix.setRotate(-90);
          matrix.postScale(-1, 1);
          break;
        case 8:
          matrix.setRotate(-90);
          break;
      }
    }
    if (!matrix.isIdentity()) {
      Bitmap rotated =
          Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
      if (rotated != bitmap) bitmap.recycle();
      bitmap = rotated;
    }
    float scale = Math.min(1f, 900f / Math.max(bitmap.getWidth(), bitmap.getHeight()));
    if (scale < 1) {
      Bitmap scaled =
          Bitmap.createScaledBitmap(
              bitmap,
              Math.max(1, Math.round(bitmap.getWidth() * scale)),
              Math.max(1, Math.round(bitmap.getHeight() * scale)),
              true);
      if (scaled != bitmap) bitmap.recycle();
      bitmap = scaled;
    }
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    for (int quality = 78; quality >= 30; quality -= 12) {
      out.reset();
      bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
      if (out.size() <= 125000) break;
    }
    bitmap.recycle();
    if (out.size() > 125000) throw new IOException("Try a simpler or smaller image");
    return Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP);
  }

  public static Bitmap decode(String data) {
    try {
      byte[] b = Base64.decode(data, Base64.DEFAULT);
      return BitmapFactory.decodeByteArray(b, 0, b.length);
    } catch (Exception e) {
      return null;
    }
  }
}
