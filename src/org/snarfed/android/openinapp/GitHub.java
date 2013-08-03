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

// The GitHub app's UriLauncherActivity claims it can handle all github.com and
// gist.github.com URLs, so just pass them on untouched.
// https://github.com/github/android/blob/master/app/AndroidManifest.xml

// It doesn't actually handle some of them though:
// Files: http://github.com/snarfed/open-in-app/blob/master/build.xml
// Issue browser: http://github.com/snarfed/facebook-atom/issues
// Pull request browser: http://github.com/rogerhu/mockfacebook/pulls
// All gist URLs:
//  https://gist.github.com/JakeWharton
//  https://gist.github.com/JakeWharton/6002797
public class GitHub extends Activity {
  private static final String TAG = "GitHub";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Uri uri = getIntent().getData();
    if (uri == null || uri.getPath() == null) {
      Log.i(TAG, "No URI in intent! Exiting.");
      finish();
      return;
    }

    // Strip www. from host if it's there.
    Uri newUri = Uri.parse("https://github.com" + uri.getPath());
    Intent intent = new Intent(Intent.ACTION_VIEW, newUri);
    Log.i(TAG, "Redirecting " + uri + " to package com.github.mobile");
    intent.setPackage("com.github.mobile");
    startActivity(intent);
    finish();
  }
}
