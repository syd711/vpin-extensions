package de.mephisto.vpin.extensions;

import de.mephisto.vpin.extensions.generator.OverlayGenerator;

public class SuperMain {
  public static void main(String[] args) throws Exception {
    if(args != null && args.length > 0 && args[0].contains("config")) {
      new Splash();
    }
    else if(args != null && args.length > 0 && args[0].contains("test")) {
      FXTest.main(args);
    }
    else if(args != null && args.length > 0 && args[0].contains("overlay")) {
      OverlayGenerator.main(args);
    }
    else {
      new ServiceRunner();
    }
  }
}
