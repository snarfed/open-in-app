package org.snarfed.android.openinapp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.net.Uri;
import android.util.Log;

// Test command line: adb -d shell am start -d [link]

// TODO: handle profile/page usernames, e.g. http://facebook.com/snarfed.org .
// The naive link like fb://profile/snarfed.org doesn't work.
public class Facebook extends Activity {
  private static final String TAG = "Facebook";

  private static class Transform {
    Transform(String pattern, String replace) {
      this.pattern = Pattern.compile("/" + pattern + "/?");
      this.replace = replace;
    }

    Pattern pattern;
    String replace;
  }

  static final Transform[] transforms = new Transform[] {
      // http://stackoverflow.com/a/6638342/186123

      // http://facebook.com/212038
      new Transform("([^/.?]+)", "profile/$1"),

      // https://www.facebook.com/pages/mockfb/225279024204684
      new Transform("pages/([^/.?]+)/([^/.?]+)", "page/$2"),

      // http://facebook.com/504988744/posts/10151785603608745
      new Transform("[^/.?]/posts/[^/.?]", "post/$1_$2?owner=$1"),

      // https://www.facebook.com/groups/257050967664385/
      new Transform("groups/([^/.?]+)", "group/$1"),

      // this doesn't work yet. :/
      // https://www.facebook.com/photo.php?fbid=522958101087051&set=at.113298848719647.8466.100001185986830.212743&type=1&theater
      // new Transform("photo\\.php\\?(.*)fbid=([0-9]+)", "photos?photo=$2"),
  };

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Uri uri = getIntent().getData();
    if (uri == null || uri.getPath() == null) {
      Log.i(TAG, "No URI in intent! Exiting.");
      finish();
      return;
    }

    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    boolean matched = false;
    for (Transform tx : transforms) {
      Matcher matcher = tx.pattern.matcher(uri.getPath());
      if (matcher.matches()) {
        Uri newUri = Uri.parse("fb://" + matcher.replaceFirst(tx.replace));
        Log.i(TAG, "Redirecting " + uri + " to " + newUri);
        intent.setData(newUri);
        matched = true;
        break;
      }
    }

    if (!matched) {
      Log.w(TAG, "No match for" + uri + ", resending original intent.");
    }

    startActivity(intent);
    finish();
  }
}
