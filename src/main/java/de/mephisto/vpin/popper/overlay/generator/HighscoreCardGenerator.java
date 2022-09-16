package de.mephisto.vpin.popper.overlay.generator;

import de.mephisto.vpin.GameInfo;
import de.mephisto.vpin.VPinService;
import de.mephisto.vpin.popper.PopperScreen;
import de.mephisto.vpin.popper.overlay.util.Config;
import de.mephisto.vpin.util.SystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;

public class HighscoreCardGenerator extends GraphicsGenerator {
  private final static Logger LOG = LoggerFactory.getLogger(HighscoreCardGenerator.class);

  public static File SAMPLE_FILE = new File(SystemInfo.RESOURCES, "highscore-card-sample.png");

  public static BufferedImage generateCard(GameInfo game) throws Exception {
    String screenName = Config.getCardGeneratorConfig().getString("popper.screen");
    PopperScreen screen = PopperScreen.valueOf(screenName);
    return generateCard(game, screen, null);
  }

  public static BufferedImage generateCard(GameInfo game, PopperScreen screen) throws Exception {
    return generateCard(game, screen, null);
  }

  public static BufferedImage generateCard(GameInfo game, PopperScreen screen, File target) throws Exception {
    return new HighscoreCardGenerator().generate(game, screen, target);
  }

  BufferedImage generate(GameInfo game, PopperScreen screen, File target) throws Exception {
    try {
      File sourceFile = new File("./resources", Config.getCardGeneratorConfig().get("card.background"));
      BufferedImage backgroundImage = super.loadBackground(sourceFile);
      HighscoreCardGraphics.drawHighscores(backgroundImage, game);

      if(target == null) {
        target = game.getPopperScreenMedia(screen);
      }

      if(target.exists()) {
        target.delete();
      }

      if(sourceFile.getName().endsWith(".png")) {
        super.writePNG(backgroundImage, target);
      }
      else {
        super.writePNG(backgroundImage, target);
      }
      return backgroundImage;
    } catch (Exception e) {
      LOG.error("Failed to generate card: " + e.getMessage(), e);
      throw e;
    }
  }

  private Date getLastModificationDate(GameInfo target, PopperScreen screen) {
    return new Date();
  }

  public static void main(String[] args) throws Exception {
    VPinService service = VPinService.create(false);
    try {
      GameInfo gameInfo = service.getGameByRom("STLE");
      new HighscoreCardGenerator().generate(gameInfo, PopperScreen.Other2, HighscoreCardGenerator.SAMPLE_FILE);
    } catch (Exception e) {
      e.printStackTrace();
    }
    service.shutdown();
  }
}
