package de.ggs.vpin.extensions.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

public class PropertiesStore {
  private final static Logger LOG = LoggerFactory.getLogger(PropertiesStore.class);

  private static Properties properties = new Properties();
  private static File propertiesFile;

  public static void init(String home) {
    try {
      propertiesFile = new File(home,"table.properties");
      properties.load(new FileInputStream(propertiesFile));
    } catch (IOException e) {
     LOG.error("Failed to load data store: " + e.getMessage(), e);
    }
  }

  public static String get(String key) {
    return properties.getProperty(key);
  }

  public static void set(String key, String value) {
    properties.setProperty(key ,value);
    try {
      properties.store(new FileOutputStream(propertiesFile), null);
    } catch (Exception e) {
      LOG.error("Failed to store data store: " + e.getMessage(), e);
    }
  }
}
