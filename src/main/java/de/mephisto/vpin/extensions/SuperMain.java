package de.mephisto.vpin.extensions;

import de.mephisto.vpin.extensions.generator.OverlayGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuperMain {
  private final static Logger LOG = LoggerFactory.getLogger(SuperMain.class);
  public static void main(String[] args) throws Exception {
    LOG.info("Starting version " + Updater.getCurrentVersion());

    if (args != null && args.length > 0 && args[0].contains("config")) {
      new Splash();
    }
    else if (args != null && args.length > 0 && args[0].contains("test")) {
      FXTest.main(args);
    }
    else if (args != null && args.length > 0 && args[0].contains("overlay")) {
      OverlayGenerator.main(args);
    }
    else {
      new ServiceRunner();
    }
  }
}
