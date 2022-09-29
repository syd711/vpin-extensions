package de.mephisto.vpin.extensions.generator;

import de.mephisto.vpin.GameInfo;
import de.mephisto.vpin.VPinService;
import de.mephisto.vpin.b2s.B2SImageRatio;
import de.mephisto.vpin.extensions.util.Config;
import de.mephisto.vpin.highscores.Highscore;
import de.mephisto.vpin.highscores.Score;
import de.mephisto.vpin.util.ImageUtil;
import de.mephisto.vpin.util.SystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CardGraphics {
  private final static Logger LOG = LoggerFactory.getLogger(CardGraphics.class);

  public BufferedImage drawHighscores(VPinService service, GameInfo game) throws Exception {
    boolean USE_DIRECTB2S = Config.getCardGeneratorConfig().getBoolean("card.useDirectB2S");
    B2SImageRatio DIRECTB2S_RATIO = B2SImageRatio.valueOf(Config.getCardGeneratorConfig().getString("card.ratio", B2SImageRatio.RATIO_16x9.name()));
    int BLUR_PIXELS = Config.getCardGeneratorConfig().getInt("card.blur");

    File sourceFile = new File(SystemInfo.RESOURCES + "backgrounds", Config.getCardGeneratorConfig().get("card.background"));
    if (USE_DIRECTB2S && game.getDirectB2SFile().exists()) {
      sourceFile = game.getDirectB2SImage();
      if (!sourceFile.exists()) {
        sourceFile = service.createDirectB2SImage(game, DIRECTB2S_RATIO, 1280);
      }
    }

    BufferedImage backgroundImage = ImageUtil.loadBackground(sourceFile);
    if (USE_DIRECTB2S) {
      backgroundImage = ImageUtil.crop(backgroundImage, DIRECTB2S_RATIO.getXRatio(), DIRECTB2S_RATIO.getYRatio());
    }

    if (BLUR_PIXELS > 0) {
      backgroundImage = ImageUtil.blurImage(backgroundImage, BLUR_PIXELS);
    }

    float alphaWhite = Config.getCardGeneratorConfig().getFloat("card.alphacomposite.white");
    float alphaBlack = Config.getCardGeneratorConfig().getFloat("card.alphacomposite.black");
    ImageUtil.applyAlphaComposites(backgroundImage, alphaWhite, alphaBlack);
    renderTableChallenge(backgroundImage, game);

    int borderWidth = Config.getCardGeneratorConfig().getInt("card.border.width");
    ImageUtil.drawBorder(backgroundImage, borderWidth);

    return backgroundImage;
  }

  /**
   * The upper section, usually with the three topscores.
   */
  private void renderTableChallenge(BufferedImage image, GameInfo game) throws Exception {
    int ROW_SEPARATOR = Config.getCardGeneratorConfig().getInt("card.highscores.row.separator");
    int WHEEL_PADDING = Config.getCardGeneratorConfig().getInt("card.highscores.row.padding.left");

    String TITLE_TEXT = Config.getCardGeneratorConfig().getString("card.title.text");

    String SCORE_FONT_NAME = Config.getCardGeneratorConfig().getString("card.score.font.name");
    int SCORE_FONT_STYLE = Config.getCardGeneratorConfig().getInt("card.score.font.style");
    int SCORE_FONT_SIZE = Config.getCardGeneratorConfig().getInt("card.score.font.size");

    String TITLE_FONT_NAME = Config.getCardGeneratorConfig().getString("card.title.font.name");
    int TITLE_FONT_STYLE = Config.getCardGeneratorConfig().getInt("card.title.font.style");
    int TITLE_FONT_SIZE = Config.getCardGeneratorConfig().getInt("card.title.font.size");

    String TABLE_FONT_NAME = Config.getCardGeneratorConfig().getString("card.table.font.name");
    int TABLE_FONT_STYLE = Config.getCardGeneratorConfig().getInt("card.table.font.style");
    int TABLE_FONT_SIZE = Config.getCardGeneratorConfig().getInt("card.table.font.size");

    int TITLE_Y_OFFSET = Config.getCardGeneratorConfig().getInt("card.title.y.offset");

    Highscore highscore = game.resolveHighscore();
    if (highscore != null) {
      Graphics g = image.getGraphics();
      ImageUtil.setDefaultColor(g, Config.getCardGeneratorConfig().getString("card.font.color"));
      int imageWidth = image.getWidth();

      g.setFont(new Font(TITLE_FONT_NAME, TITLE_FONT_STYLE, TITLE_FONT_SIZE));

      String title = TITLE_TEXT;
      int titleWidth = g.getFontMetrics().stringWidth(title);
      int titleY = TITLE_FONT_SIZE + TITLE_Y_OFFSET;
      g.drawString(title, imageWidth / 2 - titleWidth / 2, titleY);

      g.setFont(new Font(TABLE_FONT_NAME, TABLE_FONT_STYLE, TABLE_FONT_SIZE));
      String tableName = game.getGameDisplayName();
      int width = g.getFontMetrics().stringWidth(tableName);
      int tableNameY = titleY + TABLE_FONT_SIZE + TABLE_FONT_SIZE / 2;
      g.drawString(tableName, imageWidth / 2 - width / 2, tableNameY);


      g.setFont(new Font(SCORE_FONT_NAME, SCORE_FONT_STYLE, SCORE_FONT_SIZE));
      int count = 0;
      int scoreWidth = 0;

      List<String> scores = new ArrayList<>();
      for (Score score : highscore.getScores()) {
        String scoreString = score.getPosition() + ". " + score.getUserInitials() + " " + score.getScore();
        scores.add(scoreString);

        int singleScoreWidth = g.getFontMetrics().stringWidth(title);
        if (scoreWidth < singleScoreWidth) {
          scoreWidth = singleScoreWidth;
        }
        count++;
        if (count == 3) {
          break;
        }
      }

      tableNameY = tableNameY + TABLE_FONT_SIZE / 2;

      //draw wheel icon
      File wheelIconFile = game.getWheelIconFile();
      int wheelY = tableNameY + ROW_SEPARATOR;
      int wheelSize = 3 * SCORE_FONT_SIZE + 3 * ROW_SEPARATOR;
      if (wheelIconFile.exists()) {
        BufferedImage wheelImage = ImageIO.read(wheelIconFile);
        g.drawImage(wheelImage, WHEEL_PADDING, wheelY, wheelSize, wheelSize, null);
      }


      //the wheelsize should match the height of three score entries
      int scoreX = WHEEL_PADDING + wheelSize + WHEEL_PADDING;
      int scoreY = tableNameY;
      for (String score : scores) {
        scoreY = scoreY + SCORE_FONT_SIZE + ROW_SEPARATOR;
        g.drawString(score, scoreX, scoreY);
      }
    }
  }
}
