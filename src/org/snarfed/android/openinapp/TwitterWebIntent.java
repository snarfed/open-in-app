package org.snarfed.android.openinapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.net.Uri;
import android.util.Log;

// This activity is currently unused. The official Twitter app handles web
// intent URLs, but it redirects them to the browser. It doesn't have native
// intents for handling retweeting, favorites, etc. anyway. :/

// Test command line: adb -d shell am start -d [link]

// test links from https://dev.twitter.com/docs/intents :
// http://twitter.com/intent/tweet?url=https://twitter.com/intent/tweet?url=http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FIn_Watermelon_Sugar&in_reply_to=62862594515546112&via=twicodeer&text=foo%20bar&hashtags=baz,baj
// http://twitter.com/intent/favorite?tweet_id=62862594515546112
// http://twitter.com/intent/retweet?tweet_id=62862594515546112
// http://twitter.com/intent/user?screen_name=alwaysmikegomez

public class TwitterWebIntent extends Activity {
  static final String TAG = "OpenLinkInApp.TwitterWebIntent";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Uri uri = getIntent().getData();
    if (uri == null || uri.getPath() == null) {
      Log.i(TAG, "No URI in intent! Exiting.");
      finish();
      return;
    }

    // http://wiki.akosma.com/IPhone_URL_Schemes#Twitter
    // http://omgwtfgames.com/2012/01/android-intents-captured-by-various-twitter-clients/
    Intent intent = null;
    if (uri.getPath().equals("/intent/tweet")) {
      intent = new Intent(Intent.ACTION_SEND);
      intent.setType("text/plain");

      StringBuilder builder = new StringBuilder();
      String text = uri.getQueryParameter("text");
      if (text != null) {
        builder.append(text);
      }

      String hashtags = uri.getQueryParameter("hashtags");
      if (hashtags != null) {
        for (String tag : hashtags.split(",")) {
          builder.append(" #" + tag);
        }
      }

      String url = uri.getQueryParameter("url");
      if (url != null) {
        builder.append(url);
      }

      String via = uri.getQueryParameter("via");
      if (via != null) {
        builder.append(" via @" + via);
      }

      intent.putExtra(Intent.EXTRA_TEXT, text);
      Log.i(TAG, "Redirecting " + uri + " to ACTION_SEND with text/plain: " + text);

    } else {
      intent = new Intent(getIntent());
      Log.i(TAG, "Unknown path " + uri.getPath() + " , resending original intent.");
    }

    startActivity(intent);
    finish();
  }
}
