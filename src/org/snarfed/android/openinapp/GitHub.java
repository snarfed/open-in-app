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
// https://github.com/snarfed/open-in-app
// https://github.com/snarfed

// https://github.com/github/android/blob/master/app/AndroidManifest.xml
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

    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    Log.i(TAG, "Redirecting " + uri + " to package com.github.mobile");
    intent.setPackage("com.github.mobile");

    startActivity(intent);
    finish();
  }
}
