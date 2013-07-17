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
