package de.mephisto.vpin.popper.overlay.generator;

import de.mephisto.vpin.GameInfo;
import de.mephisto.vpin.highscores.Highscore;
import de.mephisto.vpin.highscores.Score;
import de.mephisto.vpin.popper.overlay.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HighscoreCardGraphics extends VPinGraphics {
  private final static Logger LOG = LoggerFactory.getLogger(HighscoreCardGraphics.class);

  private static int ROW_SEPARATOR = Config.getCardGeneratorConfig().getInt("card.highscores.row.separator");
  private static int ROW_PADDING_LEFT = Config.getCardGeneratorConfig().getInt("card.highscores.row.padding.left");

  private static String TITLE_TEXT = Config.getCardGeneratorConfig().getString("card.title.text");

  private static String SCORE_FONT_NAME = Config.getCardGeneratorConfig().getString("card.score.font.name");
  private static int SCORE_FONT_STYLE = Config.getCardGeneratorConfig().getInt("card.score.font.style");
  private static int SCORE_FONT_SIZE = Config.getCardGeneratorConfig().getInt("card.score.font.size");

  private static String TITLE_FONT_NAME = Config.getCardGeneratorConfig().getString("card.title.font.name");
  private static int TITLE_FONT_STYLE = Config.getCardGeneratorConfig().getInt("card.title.font.style");
  private static int TITLE_FONT_SIZE = Config.getCardGeneratorConfig().getInt("card.title.font.size");

  private static String TABLE_FONT_NAME = Config.getCardGeneratorConfig().getString("card.table.font.name");
  private static int TABLE_FONT_STYLE = Config.getCardGeneratorConfig().getInt("card.table.font.style");
  private static int TABLE_FONT_SIZE = Config.getCardGeneratorConfig().getInt("card.table.font.size");

  private static int TITLE_Y_OFFSET = Config.getCardGeneratorConfig().getInt("card.title.y.offset");

  private static void initValues() {
    ROW_SEPARATOR = Config.getCardGeneratorConfig().getInt("card.highscores.row.separator");
    ROW_PADDING_LEFT = Config.getCardGeneratorConfig().getInt("card.highscores.row.padding.left");

    TITLE_TEXT = Config.getCardGeneratorConfig().getString("card.title.text");

    SCORE_FONT_NAME = Config.getCardGeneratorConfig().getString("card.score.font.name");
    SCORE_FONT_STYLE = Config.getCardGeneratorConfig().getInt("card.score.font.style");
    SCORE_FONT_SIZE = Config.getCardGeneratorConfig().getInt("card.score.font.size");

    TITLE_FONT_NAME = Config.getCardGeneratorConfig().getString("card.title.font.name");
    TITLE_FONT_STYLE = Config.getCardGeneratorConfig().getInt("card.title.font.style");
    TITLE_FONT_SIZE = Config.getCardGeneratorConfig().getInt("card.title.font.size");

    TABLE_FONT_NAME = Config.getCardGeneratorConfig().getString("card.table.font.name");
    TABLE_FONT_STYLE = Config.getCardGeneratorConfig().getInt("card.table.font.style");
    TABLE_FONT_SIZE = Config.getCardGeneratorConfig().getInt("card.table.font.size");

    TITLE_Y_OFFSET = Config.getCardGeneratorConfig().getInt("card.title.y.offset");
  }

  public static void drawHighscores(BufferedImage image, GameInfo game) throws Exception {
    initValues();

    float alphaWhite = Config.getCardGeneratorConfig().getFloat("card.alphacomposite.white");
    float alphaBlack = Config.getCardGeneratorConfig().getFloat("card.alphacomposite.black");
    applyAlphaComposites(image, alphaWhite, alphaBlack);
    renderTableChallenge(image, game);

    int borderWidth = Config.getCardGeneratorConfig().getInt("card.border.width");
    drawBorder(image, borderWidth);
  }

  /**
   * The upper section, usually with the three topscores.
   */
  private static void renderTableChallenge(BufferedImage image, GameInfo game) throws Exception {
    Highscore highscore = game.resolveHighscore();
    if (highscore != null) {
      Graphics g = image.getGraphics();
      setDefaultColor(g, Config.getCardGeneratorConfig().getString("card.font.color"));
      int imageWidth = image.getWidth();

      g.setFont(new Font(TITLE_FONT_NAME, TITLE_FONT_STYLE, TITLE_FONT_SIZE));

      String title = TITLE_TEXT;
      int titleWidth = g.getFontMetrics().stringWidth(title);
      int titleY = TITLE_FONT_SIZE + TITLE_Y_OFFSET;
      g.drawString(title, imageWidth / 2 - titleWidth / 2, titleY);

      g.setFont(new Font(TABLE_FONT_NAME, TABLE_FONT_STYLE, TABLE_FONT_SIZE));
      String tableName = game.getGameDisplayName();
      int width = g.getFontMetrics().stringWidth(tableName);
      int tableNameY = titleY + TABLE_FONT_SIZE + TABLE_FONT_SIZE/2;
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

      int position = 0;
      int wheelWidth = 3 * TITLE_FONT_SIZE + 3 * ROW_SEPARATOR;
      for (String score : scores) {
        position++;
        int scoreY = tableNameY + position * TITLE_FONT_SIZE + ROW_SEPARATOR;
        g.drawString(score, ROW_PADDING_LEFT + wheelWidth + ROW_SEPARATOR, scoreY);
      }

      File wheelIconFile = game.getWheelIconFile();
      int wheelY = tableNameY + ROW_SEPARATOR;
      if (wheelIconFile.exists()) {
        BufferedImage wheelImage = ImageIO.read(wheelIconFile);
        g.drawImage(wheelImage, ROW_PADDING_LEFT, wheelY, wheelWidth, wheelWidth, null);
      }
    }
  }
}
