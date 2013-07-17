package org.snarfed.android.openinapp;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

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
    Intent intent = new Intent(Intent.ACTION_VIEW, newUri);
    if (Pattern.matches("/[^/]+", uri.getPath()) ||  // user
        Pattern.matches("/[^/]+/status/[^/]+", uri.getPath())) {  // tweet
      Log.i(TAG, "Redirecting " + uri + " to " + newUri);
      intent.setPackage("com.twitter.android");
    } else {
      // twitter.com URLs redirect back to mobile.twitter.com URLs on phones,
      // and I couldn't find a way to either 1) filter my own app/activity out
      // of the handlers for the resulting mobile.twitter.com intent, or 2)
      // force an intent to open only in a browser. CATEGORY_BROWSABLE,
      // CATEGORY_APP_BROWSER, set/makeMainSelectorActivity() etc all failed.
      // I could limit package to com.android... or com.google... but didn't
      // want to ignore a user's different default browser.
      //
      // So, I create the chooser manually and remove this app and the Twitter
      // app (which redirects to mobile.twitter.com). Code based on
      // http://hkdevtips.blogspot.com/2013/02/customize-your-actionchooser-intent.html
      Toast.makeText(this,
                     "Sorry, Open Link in App didn't understand this Twitter link.",
                     Toast.LENGTH_SHORT).show();

      List<Intent> apps = new ArrayList<Intent>();
      for (ResolveInfo info : getPackageManager().queryIntentActivities(intent, 0)) {
        String pkg = info.activityInfo.packageName;
        if (!pkg.equals(getClass().getPackage()) &&
            !pkg.equals("com.twitter.android")) {
          apps.add(new Intent(intent).setPackage(pkg));
        }
      }

      String title = getResources().getText(R.string.twitter_app_chooser_title).toString();
      // Create the chooser with the first explicit app intent, then add the
      // others as extras.
      intent = Intent.createChooser(apps.remove(0), title);
      intent.putExtra(Intent.EXTRA_INITIAL_INTENTS, apps.toArray(new Parcelable[]{}));
    }

    startActivity(intent);
    finish();
  }
}
