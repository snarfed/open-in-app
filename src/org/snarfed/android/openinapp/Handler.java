package org.snarfed.android.openinapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.yaml.snakeyaml.Yaml;

public class Handler extends Activity {
    static final String TAG = "OpenLinkInApp";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getData();
        if (uri == null || uri.getPath() == null) {
            Log.e(TAG, "No URI in intent!");
            finish();
            return;
        }

        // Read config file and find the app for this host
        Map config = (Map)new Yaml().load(
            getResources().openRawResource(R.raw.apps));
        Map app = null;
        for (Map a : (List<Map>)config.get("apps")) {
            for (String host : (List<String>)a.get("hosts")) {
                if (host.equals(uri.getHost())) {
                    app = a;
                    break;
                }
            }
        }

        if (app == null) {
            Log.e(TAG, "Intent URI " + uri + " has unknown host.");
            finish();
            return;
        }

        // Build new URI
        String base = (String)app.get("new_base");
        if (base == null) {
            base = uri.getScheme() + "://" + uri.getHost() + "/";
        }

        List<Map<String, String>> transforms = (List<Map<String, String>>)
                                               app.get("uri_transforms");
        String path = uri.getPath();
        if (transforms != null) {
            boolean matched = false;
            for (Map<String, String> transform : transforms) {
                Matcher matcher = Pattern.compile("/" + transform.get("from") + "/?")
                                  .matcher(path);
                if (matcher.matches()) {
                    path = matcher.replaceFirst(transform.get("to"));
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                giveUp(uri, app);
                return;
            }
        }

        Uri newUri = Uri.parse(base + path);
        Log.i(TAG, "Redirecting " + uri + " to " + newUri);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setData(newUri);
        String pkg = (String)app.get("package");
        if (pkg != null) {
            intent.setPackage(pkg);
        }

        // Recommended to prevent occasional Instagram app crashes. Try it if we
        // start seeing that.
        // https://groups.google.com/d/msg/instagram-api-developers/QmLGb4ImWLU/aXFlNFKfSnUJ
        // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
        finish();
    }

    public void giveUp(Uri uri, Map app) {
        // I couldn't find a way to either 1) filter my own app/activity out of
        // the handlers for the resulting mobile.twitter.com intent, or 2) force
        // an intent to open only in a browser. CATEGORY_BROWSABLE,
        // CATEGORY_APP_BROWSER, set/makeMainSelectorActivity() etc all failed.
        // I could limit package to com.android... or com.google... but didn't
        // want to ignore a user's different default browser.
        //
        // So, I create the chooser manually and remove this app and the Twitter
        // app (which redirects to mobile.twitter.com). Code based on
        // http://hkdevtips.blogspot.com/2013/02/customize-your-actionchooser-intent.html
        Toast.makeText(this, "Sorry, " + (String)app.get("name") +
                       " can't handle this link.",
                       Toast.LENGTH_LONG).show();

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        List<Intent> apps = new ArrayList<Intent>();
        String appPkg = (String)app.get("package");
        for (ResolveInfo info : getPackageManager().queryIntentActivities(intent, 0)) {
            String pkg = info.activityInfo.packageName;
            if (!pkg.equals(getClass().getPackage().getName()) &&
                !pkg.equals(appPkg)) {
                apps.add(new Intent(intent).setPackage(pkg));
            }
        }

        // Create the chooser with the first explicit app intent, then add the
        // others as extras.
        intent = Intent.createChooser(apps.remove(0), null);
        intent.putExtra(Intent.EXTRA_INITIAL_INTENTS, apps.toArray(new Parcelable[]{}));
        startActivity(intent);
        finish();
    }
}
