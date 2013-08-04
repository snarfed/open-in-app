#!/usr/bin/env python
"""Generates AndroidManifest.xml based on apps.yaml."""

import yaml

CONFIG_FILE = 'apps.yaml'


def main():
  with open(CONFIG_FILE) as f:
    config = yaml.load(f)

  schemes = config['schemes']
  data = []
  for app in config['apps']:
    assert 'hosts' in app, 'app %s is missing hosts field' % app['name']
    data_elements = []

    for host in app['hosts']:
      for scheme in schemes:
        prefixes = app.get('prefixes', [])
        patterns = app.get('patterns', [])
        if not prefixes and not patterns:
          prefixes = ['/']

        for tag, values in ('pathPrefix', prefixes), ('pathPattern', patterns):
          for value in values:
            # To catch links in Chrome, must include scheme, host, and
            # either pathPrefix or pathPattern.
            # http://stackoverflow.com/questions/17706667
            data.append(
              '<data android:scheme="%s" android:host="%s" android:%s="%s" />' %
              (scheme, host, tag, value))

  print """
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
      package="org.snarfed.android.openinapp"
      android:versionCode="4"
      android:versionName="1.0.3">

  <uses-sdk android:minSdkVersion="4"
            android:targetSdkVersion="17" />

  <application android:label="@string/app_name" android:icon="@drawable/u_turn">

    <activity android:name="Handler" android:exported="true">
%s
    </activity>

  </application>
</manifest>
""" % '\n'.join(data)


if __name__ == "__main__":
    main()
