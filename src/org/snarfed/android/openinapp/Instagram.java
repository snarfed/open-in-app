package org.snarfed.android.openinapp;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

// Test command line: adb -d shell am start -d [link]
// http://instagram.com/p/byuvjTsqJo/
// https://www.instagram.com/snarfed
//
// Currently the Instagram app only handles intents for opening pictures and
// resetting your password, not opening user profiles.
//
// https://groups.google.com/forum/#!msg/instagram-api-developers/7XUKm9HSAdg/9SrdVmB4trQJ
// http://stackoverflow.com/questions/15497261/open-instagram-user-profile
// https://github.com/danthemellowman/instagram-decompiled/blob/master/AndroidManifest.xml
//
// Instagram does have an instagram:// scheme, but evidently it's iOS only.
// http://instagram.com/developer/iphone-hooks/#
public class Instagram extends Activity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Uri uri = getIntent().getData();
    if (uri == null || uri.getPath() == null) {
      Log.i(Constants.TAG, "No URI in intent! Exiting.");
      finish();
      return;
    }

    Uri newUri = uri;
    // The Instagram app handles direct links to pictures with
    // http://insta.gram/p/..., but afaict not user or other links.
    if (Pattern.matches("/p/[^/]+/?", uri.getPath())) {
        newUri = Uri.parse("http://instagr.am" + uri.getPath());
    }

    Intent intent = new Intent(Intent.ACTION_VIEW, newUri);
    // Recommended to prevent occasional Instagram app crashes:
    // https://groups.google.com/d/msg/instagram-api-developers/QmLGb4ImWLU/aXFlNFKfSnUJ
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

    Log.i(Constants.TAG, "Redirecting " + uri + " to " + newUri);
    startActivity(intent);
    finish();
  }
}
