package org.snarfed.android.openinapp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

// Test command line: adb -d shell am start -d [link]

// TODO: handle usernames, e.g. http://facebook.com/snarfed.org . The naive link
// like fb://profile/snarfed.org doesn't work.
public class Facebook extends Activity {
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
      // http://wiki.akosma.com/IPhone_URL_Schemes#Facebook

      // http://facebook.com/212038
      new Transform("([0-9]+)", "profile/$1"),

      // http://facebook.com/snarfed.org
      // Looks like there's no way to handle usernames yet, other than looking
      // up their user id.
      // http://stackoverflow.com/questions/9281267/how-do-i-link-to-a-facebook-user-in-the-ios-app-by-using-the-username
      // The old way of passing it in "extra_user_display_name" no longer works:
      // http://forum.frandroid.com/topic/22299-facebooktwitter-intent/
      //
      // new Transform("([^/]+)", "profile/$1"),

      // https://www.facebook.com/pages/mockfb/225279024204684
      new Transform("pages/([^/.?]+)/([^/.?]+)", "page/$2"),

      // http://facebook.com/504988744/posts/10151785603608745
      new Transform("([^/.?]+)/posts/([^/.?]+)", "post/$1_$2?owner=$1"),

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
      Log.i(Constants.TAG, "No URI in intent! Exiting.");
      finish();
      return;
    }

    Intent intent = new Intent(Intent.ACTION_VIEW);
    boolean matched = false;
    for (Transform tx : transforms) {
      Matcher matcher = tx.pattern.matcher(uri.getPath());
      if (matcher.matches()) {
        Uri newUri = Uri.parse("fb://" + matcher.replaceFirst(tx.replace));
        Log.i(Constants.TAG, "Redirecting " + uri + " to " + newUri);
        intent.setData(newUri);
        matched = true;
        break;
      }
    }

    if (!matched) {
      Toast.makeText(this, "Sorry, Open Link in App didn't understand this Facebook link.",
                     Toast.LENGTH_SHORT).show();
      intent.setData(Uri.parse("fb://root"));
    }

    startActivity(intent);
    finish();
  }
}
