package de.ggs.vpin.extensions.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Settings {
  private final static Logger LOG = LoggerFactory.getLogger(Settings.class);

  private static Properties properties = new Properties();
  private static File propertiesFile;

  public static final String GAME_EXIT_DELAY = "game.exit.delay.ms";

  public static void init(String home) {
    try {
      propertiesFile = new File(home, "settings.properties");
      properties.load(new FileInputStream(propertiesFile));
    } catch (IOException e) {
      LOG.error("Failed to load data store: " + e.getMessage(), e);
    }
  }

  public static String get(String key) {
    return properties.getProperty(key);
  }

  public static void set(String key, String value) {
    properties.setProperty(key, value);
    try {
      properties.store(new FileOutputStream(propertiesFile), null);
    } catch (Exception e) {
      LOG.error("Failed to store data store: " + e.getMessage(), e);
    }
  }
}
