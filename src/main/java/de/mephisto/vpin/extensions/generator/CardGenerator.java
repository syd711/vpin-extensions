package de.mephisto.vpin.extensions.generator;

import de.mephisto.vpin.GameInfo;
import de.mephisto.vpin.VPinService;
import de.mephisto.vpin.extensions.util.Config;
import de.mephisto.vpin.popper.PopperScreen;
import de.mephisto.vpin.util.ImageUtil;
import de.mephisto.vpin.util.SystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;

public class CardGenerator {
  private final static Logger LOG = LoggerFactory.getLogger(CardGenerator.class);

  public static File SAMPLE_FILE = new File(SystemInfo.RESOURCES, "highscore-card-sample.png");

  public static BufferedImage generateCard(VPinService service, GameInfo game) throws Exception {
    String screenName = Config.getCardGeneratorConfig().getString("popper.screen");
    PopperScreen screen = PopperScreen.valueOf(screenName);
    return generateCard(service, game, screen, null);
  }

  public static BufferedImage generateCard(VPinService service, GameInfo game, PopperScreen screen) throws Exception {
    return generateCard(service, game, screen, null);
  }

  public static BufferedImage generateCard(VPinService service, GameInfo game, PopperScreen screen, File target) throws Exception {
    return new CardGenerator().generate(service, game, screen, target);
  }

  BufferedImage generate(VPinService service, GameInfo game, PopperScreen screen, File target) throws Exception {
    try {
      BufferedImage backgroundImage = new CardGraphics().drawHighscores(service, game);
      if (target == null) {
        target = game.getPopperScreenMedia(screen);
      }
      if (target.exists()) {
        target.delete();
      }

      ImageUtil.write(backgroundImage, target);
      return backgroundImage;
    } catch (Exception e) {
      LOG.error("Failed to generate card: " + e.getMessage(), e);
      throw e;
    }
  }
}
