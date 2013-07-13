package org.snarfed.android.openinapp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

/* Test links:
http://facebook.com/snarfed.org
http://facebook.com/212038
https://facebook.com/212038
http://www.facebook.com/212038
http://facebook.com/504988744/posts/10151785603608745
*/

// TODO: handle profile/page usernames, e.g. http://facebook.com/snarfed.org .
// The naive link like fb://profile/snarfed.org doesn't work.
public class OpenLink extends Activity {
  private static final String TAG = "OpenLink";

  private static class Transform {
    Transform(String pattern, String replace) {
      this.pattern = Pattern.compile("/" + pattern + "/?");
      this.replace = replace;
    }

    Pattern pattern;
    String replace;
  }

  private static final Transform transforms[] = {
    // Facebook (from http://stackoverflow.com/a/6638342/186123 )
    new Transform("([^/]+)", "/profile/$1"),
    new Transform("pages/([^/]+)/([^/]+)", "/page/$2"),
    new Transform("[^/]/posts/[^/]", "/post/$1_$2?owner=$1"),
    new Transform("groups/([^/]+)", "/group/$1"),
  };

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Uri uri = getIntent().getData();
    if (uri == null || uri.getPath() == null) {
      Log.i(TAG, "No URI in intent! Exiting.");
      finish();
      return;
    }

    Uri newUri = null;
    for (Transform tx : transforms) {
      Matcher matcher = tx.pattern.matcher(uri.getPath());
      if (matcher.matches()) {
        newUri = Uri.parse("fb:/" + matcher.replaceFirst(tx.replace));
        break;
      }
    }

    if (newUri != null) {
      try {
        // Check for the Facebook app.
        getPackageManager().getPackageInfo("com.facebook.katana", 0);
        Log.i(TAG, "Redirecting " + uri + " to Facebook app via " + newUri);
        Intent intent = new Intent(Intent.ACTION_VIEW, newUri);
        startActivity(intent);
        finish();
        return;
      } catch (Exception e) {
        Log.w(TAG, "Couldn't find Facebook app: ", e);
      }
    }

    Log.i(TAG, "Sending to browser: " + uri);
    Intent intent = new Intent(Intent.ACTION_VIEW, uri); // old style
    finish();
  }
}
