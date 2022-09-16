package de.mephisto.vpin.popper.overlay;

import de.mephisto.vpin.popper.overlay.generator.OverlayGenerator;

public class SuperMain {
  public static void main(String[] args) throws Exception {
    if(args != null && args.length > 0 && args[0].contains("config")) {
      ConfigWindow.main(args);
    }
    else if(args != null && args.length > 0 && args[0].contains("overlay")) {
      OverlayGenerator.main(args);
    }
    else {
      new ServiceRunner();
    }
  }
}
