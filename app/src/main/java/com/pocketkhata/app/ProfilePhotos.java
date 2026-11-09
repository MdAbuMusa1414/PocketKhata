package com.pocketkhata.app;

import android.graphics.Bitmap;
import android.widget.ImageView;

public final class ProfilePhotos {
  private ProfilePhotos() {}

  public static void bind(ImageView view, String photo) {
    Bitmap bitmap = photo == null || photo.isEmpty() ? null : Photos.decode(photo);
    if (bitmap == null) {
      int width = view.getLayoutParams() == null ? 0 : view.getLayoutParams().width;
      int padding = width > 0 ? width / 6 : 8;
      view.setPadding(padding, padding, padding, padding);
      view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
      view.setImageResource(R.drawable.ic_person);
    } else {
      view.setPadding(0, 0, 0, 0);
      view.setScaleType(ImageView.ScaleType.CENTER_CROP);
      view.setImageBitmap(bitmap);
    }
  }
}
