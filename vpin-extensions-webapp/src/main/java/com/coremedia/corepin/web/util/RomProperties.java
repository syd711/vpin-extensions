package com.coremedia.corepin.web.util;

import org.ini4j.Ini;
import org.ini4j.IniPreferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

public class RomProperties {
  private final static Logger LOG = LoggerFactory.getLogger(RomProperties.class);

  private static Preferences properties;
  private static File roms;

  public static void init(String home) {
    try {
      File roms = new File(home, "roms.ini");

      Ini ini = new Ini(roms);
      properties = new IniPreferences(ini).node("mapping");
    } catch (IOException e) {
      LOG.error("Failed to load rom list: " + e.getMessage(), e);
    }
  }

  public static String get(String key) {
    return properties.get(key, null);
  }
}
