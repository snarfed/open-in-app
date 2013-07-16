package org.snarfed.android.openinapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.net.Uri;
import android.util.Log;

// Test command line: adb -d shell am start -d [link]
// https://mobile.twitter.com/nelson/status/356256558549704704
// http://mobile.twitter.com/nelson

public class TwitterWebIntent extends Activity {
  private static final String TAG = "TwitterWebIntent";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Uri uri = getIntent().getData();
    if (uri == null || uri.getPath() == null) {
      Log.i(TAG, "No URI in intent! Exiting.");
      finish();
      return;
    }

    Intent intent = new Intent(
        Intent.ACTION_VIEW, Uri.parse("https://twitter.com" + uri.getPath()));
    startActivity(intent);
    finish();
  }
}
