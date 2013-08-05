Open Link in App
================

Ever click on a Facebook or Twitter link and it opens in the browser instead of
the native app? Me too. No fun. This app intercepts those links and sends them
to their native app.

(See the
[Play Store listing](https://play.google.com/store/apps/details?id=org.snarfed.android.openinapp)
and [blog post announcement](http://snarfed.org/2013-07-16_open_link_in_app).)

The Twitter app handles some links automatically, but not others, e.g.
mobile.twitter.com links. The Facebook app doesn't handle any. Many others, e.g.
Google+, handle all of their links correctly.

To use, after you click on a Facebook or Twitter link, just select Open Link in
App from the chooser dialog box. If you don't see the dialog box on a link that
you think should work, go to Settings => Apps => Browser or Chrome => Clear
defaults.

Known issue: clicking on a link in Chrome does *not* trigger this app, due to
[this Chrome bug](https://code.google.com/p/chromium/issues/detail?id=235060).
I've
[asked for a workaround](http://stackoverflow.com/questions/17706667/workaround-for-chrome-on-android-not-firing-intent-when-links-are-clicked),
and I'll use whatever I find, but I'm not holding my breath.

I'll add more apps and link types as I come across them. Feel free to ask for
any specific ones you want. Enjoy!


Development notes
===

This app is heavily data-driven. The external apps to integrate with are defined
in apps.yaml. generate_manifest.py uses that file at compile time to generate
AndroidManifest.xml, and the app reads it at runtime to determine how to handle
and redirect intents.

The Python YAML library is PyYAML, grossly hacked as a symlink to App Engine's
version in ~/google_appengine/ so that I don't have to check it into the repo.
The Java YAML library is SnakeYAML, checked into libs/ as a jar.

Test command line to open URL with ACTION_VIEW intent:
adb -d shell am start -d [link]

Lots of apps' AndroidManifest.xml manifest files are in app_manifests/.
To extract an app's manifest:
- "Back up" the app with
  [Astro File Manager](https://play.google.com/store/apps/details?id=com.metago.astro)
- adb pull the apk from /sdcard/backups/apps
- extract AndroidManifest.xml with
  [apktool](http://code.google.com/p/android-apktool/): apktool decode FILE.apk

For an intent filter to catch taps on links in Chrome for Android, you have to
include scheme, host, *and* either pathPrefix or pathPattern in the intent
filter's data element: http://stackoverflow.com/questions/17706667

Oddly, this also seems to "unlock" other apps' intent filters for browser link
taps too. Goodreads, for example, doesn't normally handle browser link taps, but
it does when Open Link in App is installed. Odd.

Unfortunately, pathPattern is a very limited subset of regexp: only . and * are
supported. That's not enough for some of the URI pattern matching we need. In
these cases, we overspecify a prefix or pattern and do the rest of the filtering
at runtime.


Todos
===
deregister intents for apps that aren't installed
More apps:
WordPress
OpenTable
Netflix
Hulu
YouTube? or does it already handle everything right?
Tumblr
...others?
