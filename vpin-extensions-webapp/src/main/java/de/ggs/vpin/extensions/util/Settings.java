package de.ggs.vpin.extensions.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.*;
import java.util.Properties;

public class Settings {
  private final static Logger LOG = LoggerFactory.getLogger(Settings.class);

  private static Properties properties = new Properties();
  private static File propertiesFile;

  public static void init(String home) {
    try {
      propertiesFile = new File(home, "settings.properties");
      properties.load(new FileInputStream(propertiesFile));

      new Thread() {
        public void run() {
          try {
            final Path path = FileSystems.getDefault().getPath(propertiesFile.getParentFile().getPath());
            final WatchService watchService = FileSystems.getDefault().newWatchService();
            final WatchKey watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            while (true) {
              final WatchKey wk = watchService.take();
              for (WatchEvent<?> event : wk.pollEvents()) {
                final Path changed = (Path) event.context();
                if (changed.endsWith("settings.properties")) {
                  LOG.info("Detected settings change.");
                  properties.load(new FileInputStream(propertiesFile));
                }
              }
              // reset the key
              wk.reset();
            }
          } catch (Exception e) {
            LOG.error("Failed to load data store: " + e.getMessage(), e);
          }
        }
      }.start();
    } catch (Exception e) {
      LOG.error("Failed to load data store: " + e.getMessage(), e);
    }
  }

  public static String get(String key) {
    String value = properties.getProperty(key);
    if(value != null) {
      value = value.trim();
    }
    return value;
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
