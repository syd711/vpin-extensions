package de.mephisto.vpin.extensions.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Updater {
  private final static Logger LOG = LoggerFactory.getLogger(Updater.class);

  private final static int VERSION = 1;
  private final static String BASE_URL = "https://github.com/syd711/vpin-extensions/releases/tag/";

  public static void update(String versionSegment) throws Exception {
    try {
      URL url = new URL(getDownloadSegment(versionSegment));
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.addRequestProperty ("Accept", "application/zip");
      connection.setDoOutput(true);
      BufferedInputStream in = new BufferedInputStream(url.openStream());
      File out = new File("./vpin-extensions.jar");
      FileOutputStream fileOutputStream = new FileOutputStream(out);
      byte dataBuffer[] = new byte[1024];
      int bytesRead;
      while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
        fileOutputStream.write(dataBuffer, 0, bytesRead);
      }
      in.close();
      fileOutputStream.close();
      LOG.info("Downloaded update file " + out.getAbsolutePath());
    } catch (Exception e) {
      LOG.error("Failed to execute update: " + e.getMessage(), e);
      throw e;
    }
  }

  public static String getVersionSegment() {
    return "1.0." + VERSION;
  }

  public static String checkForUpdate() {
    boolean updateAvailable = false;
    int nextVersion = VERSION + 1;
    String segment = "1.0." + nextVersion;
    while (pingUpdate(segment)) {
      nextVersion = nextVersion + 1;
      segment = "1.0." + nextVersion;
      updateAvailable = true;
    }

    if (updateAvailable) {
      nextVersion = nextVersion - 1;
      segment = "1.0." + nextVersion;
      return segment;
    }
    return null;
  }

  private static boolean pingUpdate(String versionSegment) {
    String url = BASE_URL + versionSegment;
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
      connection.setConnectTimeout(5000);
      connection.setReadTimeout(500);
      connection.setRequestMethod("HEAD");
      int responseCode = connection.getResponseCode();
      return (200 <= responseCode && responseCode <= 399);
    } catch (IOException exception) {
      return false;
    }
  }

  /**
   * E.g. https://github.com/syd711/vpin-extensions/releases/download/1.0.2/VPinExtensions.zip
   *
   * @param versionSegment
   * @return
   */
  private static String getDownloadSegment(String versionSegment) {
    return BASE_URL + versionSegment + "/vpin-extensions.jar";
  }
}
