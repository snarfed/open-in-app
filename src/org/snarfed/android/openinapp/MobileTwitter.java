package org.snarfed.android.openinapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.net.Uri;
import android.util.Log;

// Test command line: adb -d shell am start -d [link]
// https://mobile.twitter.com/nelson/status/356256558549704704
// http://mobile.twitter.com/nelson

// Just convert the mobile.twitter.com domain to just twitter.com. The Twitter
// app doesn't handle mobile.twitter.com URLs for some reason.
public class MobileTwitter extends Activity {
  private static final String TAG = "MobileTwitter";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Uri uri = getIntent().getData();
    if (uri == null || uri.getPath() == null) {
      Log.i(TAG, "No URI in intent! Exiting.");
      finish();
      return;
    }

    // http://omgwtfgames.com/2012/01/android-intents-captured-by-various-twitter-clients/
    Uri newUri = Uri.parse("https://twitter.com" + uri.getPath());
    Log.i(TAG, "Redirecting " + uri + " to " + newUri);
    Intent intent = new Intent(Intent.ACTION_VIEW, newUri);
    startActivity(intent);
    finish();
  }
}
