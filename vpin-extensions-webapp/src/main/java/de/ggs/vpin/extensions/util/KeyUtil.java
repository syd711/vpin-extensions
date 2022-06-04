package de.ggs.vpin.extensions.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class KeyUtil {
  private final static Logger LOG = LoggerFactory.getLogger(KeyUtil.class);

  public static void pressKey(int keyEvent, int delay) {
    try {
      System.setProperty("java.awt.headless", "false");
      Robot r = new Robot();
      r.keyPress(keyEvent);
      r.delay(delay);
      r.keyRelease(keyEvent);
      LOG.info("Key pressed " + keyEvent);
    } catch (Exception ex) {
      LOG.error("Failed to send key event: " + ex.getMessage());
    }
  }

}
