package org.snarfed.android.open-in-app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

/**
 * Intercepts Facebook URLs and handles them by opening the corresponding
 * profile, post, etc. in the native Facebook app.
 */
public final class Facebook extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // String uri = "content://media/external/audio/playlists/1";

        // // create extras bundle
        // Bundle bundle = new Bundle();
        // bundle.putString(PluginBundleManager.BUNDLE_EXTRA_STRING_URI, uri);

        // // broadcast intent
        // Log.d(EditActivity.TAG, "Broadcasting FIRE_SETTING with " + bundle);
        // Intent intent = new Intent(getApplicationContext(), FireReceiver.class)
        //     .setAction(com.twofortyfouram.locale.Intent.ACTION_FIRE_SETTING)
        //     .putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, bundle);
        // getApplicationContext().sendBroadcast(intent);

        finish();
    }
}
