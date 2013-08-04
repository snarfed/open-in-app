#!/usr/bin/env python
"""Generates AndroidManifest.xml based on apps.yaml."""

import yaml

CONFIG_FILE = 'apps.yaml'


def main():
  with open(CONFIG_FILE) as f:
    f.read().strip()


if __name__ == "__main__":
    main()
