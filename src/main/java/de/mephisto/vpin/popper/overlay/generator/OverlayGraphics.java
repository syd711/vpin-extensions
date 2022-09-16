package de.mephisto.vpin.popper.overlay.generator;

import de.mephisto.vpin.GameInfo;
import de.mephisto.vpin.VPinService;
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

public class OverlayGraphics extends VPinGraphics {
  private final static Logger LOG = LoggerFactory.getLogger(OverlayGraphics.class);

  private static String HIGHSCORE_TEXT = Config.getOverlayGeneratorConfig().getString("overlay.highscores.text");
  private static String TITLE_TEXT = Config.getOverlayGeneratorConfig().getString("overlay.title.text");

  private static String SCORE_FONT_NAME = Config.getOverlayGeneratorConfig().getString("overlay.score.font.name");
  private static int SCORE_FONT_STYLE = Config.getOverlayGeneratorConfig().getInt("overlay.score.font.style");
  private static int SCORE_FONT_SIZE = Config.getOverlayGeneratorConfig().getInt("overlay.score.font.size");

  private static String TITLE_FONT_NAME = Config.getOverlayGeneratorConfig().getString("overlay.title.font.name");
  private static int TITLE_FONT_STYLE = Config.getOverlayGeneratorConfig().getInt("overlay.title.font.style");
  private static int TITLE_FONT_SIZE = Config.getOverlayGeneratorConfig().getInt("overlay.title.font.size");

  private static String TABLE_FONT_NAME = Config.getOverlayGeneratorConfig().getString("overlay.table.font.name");
  private static int TABLE_FONT_STYLE = Config.getOverlayGeneratorConfig().getInt("overlay.table.font.style");
  private static int TABLE_FONT_SIZE = Config.getOverlayGeneratorConfig().getInt("overlay.table.font.size");

  private static int TITLE_Y_OFFSET = Config.getOverlayGeneratorConfig().getInt("overlay.title.y.offset");
  private static int ROW_SEPARATOR = Config.getOverlayGeneratorConfig().getInt("overlay.highscores.row.separator");
  private static int ROW_PADDING_LEFT = Config.getOverlayGeneratorConfig().getInt("overlay.highscores.row.padding.left");
  private static int ROW_HEIGHT = TABLE_FONT_SIZE + ROW_SEPARATOR + SCORE_FONT_SIZE;

  private static void initValues() {
    HIGHSCORE_TEXT = Config.getOverlayGeneratorConfig().getString("overlay.highscores.text");
    TITLE_TEXT = Config.getOverlayGeneratorConfig().getString("overlay.title.text");

    SCORE_FONT_NAME = Config.getOverlayGeneratorConfig().getString("overlay.score.font.name");
    SCORE_FONT_STYLE = Config.getOverlayGeneratorConfig().getInt("overlay.score.font.style");
    SCORE_FONT_SIZE = Config.getOverlayGeneratorConfig().getInt("overlay.score.font.size");

    TITLE_FONT_NAME = Config.getOverlayGeneratorConfig().getString("overlay.title.font.name");
    TITLE_FONT_STYLE = Config.getOverlayGeneratorConfig().getInt("overlay.title.font.style");
    TITLE_FONT_SIZE = Config.getOverlayGeneratorConfig().getInt("overlay.title.font.size");

    TABLE_FONT_NAME = Config.getOverlayGeneratorConfig().getString("overlay.table.font.name");
    TABLE_FONT_STYLE = Config.getOverlayGeneratorConfig().getInt("overlay.table.font.style");
    TABLE_FONT_SIZE = Config.getOverlayGeneratorConfig().getInt("overlay.table.font.size");

    TITLE_Y_OFFSET = Config.getOverlayGeneratorConfig().getInt("overlay.title.y.offset");
    ROW_SEPARATOR = Config.getOverlayGeneratorConfig().getInt("overlay.highscores.row.separator");
    ROW_PADDING_LEFT = Config.getOverlayGeneratorConfig().getInt("overlay.highscores.row.padding.left");
    ROW_HEIGHT = TABLE_FONT_SIZE + ROW_SEPARATOR + SCORE_FONT_SIZE;
  }

  public static void drawGames(BufferedImage image, VPinService service, GameInfo gameOfTheMonth) throws Exception {
    initValues();
    float alphaWhite = Config.getOverlayGeneratorConfig().getFloat("overlay.alphacomposite.white");
    float alphaBlack = Config.getOverlayGeneratorConfig().getFloat("overlay.alphacomposite.black");
    applyAlphaComposites(image, alphaWhite, alphaBlack);

    int highscoreListYOffset = TITLE_Y_OFFSET + TITLE_FONT_SIZE;
    if (gameOfTheMonth != null) {
      highscoreListYOffset = renderTableChallenge(image, gameOfTheMonth, highscoreListYOffset);
    }
    renderHighscoreList(image, gameOfTheMonth, service, highscoreListYOffset);
  }

  /**
   * The upper section, usually with the three topscores.
   */
  private static int renderTableChallenge(BufferedImage image, GameInfo challengedGame, int highscoreListYOffset) throws Exception {
    Highscore highscore = challengedGame.resolveHighscore();
    int returnOffset = highscoreListYOffset;
    if (highscore != null) {
      Graphics g = image.getGraphics();
      setDefaultColor(g, Config.getOverlayGeneratorConfig().getString("overlay.font.color"));
      int imageWidth = image.getWidth();

      g.setFont(new Font(TITLE_FONT_NAME, TITLE_FONT_STYLE, TITLE_FONT_SIZE));

      String title = TITLE_TEXT;
      int titleWidth = g.getFontMetrics().stringWidth(title);
      int titleY = ROW_SEPARATOR + TITLE_FONT_SIZE + TITLE_Y_OFFSET;
      g.drawString(title, imageWidth / 2 - titleWidth / 2, titleY);

      g.setFont(new Font(TABLE_FONT_NAME, TABLE_FONT_STYLE, TABLE_FONT_SIZE));
      String challengedTable = challengedGame.getGameDisplayName();
      int width = g.getFontMetrics().stringWidth(challengedTable);
      int tableNameY = titleY + ROW_SEPARATOR + TITLE_FONT_SIZE;
      g.drawString(challengedTable, imageWidth / 2 - width / 2, tableNameY);

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
      int totalScoreAndWheelWidth = scoreWidth + wheelWidth;

      for (String score : scores) {
        position++;
        int scoreY = tableNameY + position * TITLE_FONT_SIZE + ROW_SEPARATOR;
        g.drawString(score, imageWidth / 2 - totalScoreAndWheelWidth / 2 + wheelWidth + ROW_SEPARATOR, scoreY);
      }

      File wheelIconFile = challengedGame.getWheelIconFile();
      int wheelY = tableNameY + ROW_SEPARATOR;
      returnOffset = wheelY * 2 + SCORE_FONT_SIZE * 2;
      if (wheelIconFile.exists()) {
        BufferedImage wheelImage = ImageIO.read(wheelIconFile);
        g.drawImage(wheelImage, imageWidth / 2 - totalScoreAndWheelWidth / 2, wheelY, wheelWidth, wheelWidth, null);
      }
    }

    returnOffset+= TITLE_FONT_SIZE /2;
    return returnOffset;
  }

  private static void renderHighscoreList(BufferedImage image, GameInfo gameOfTheMonth, VPinService service, int highscoreListYOffset) throws Exception {
    Graphics g = image.getGraphics();
    setDefaultColor(g, Config.getOverlayGeneratorConfig().getString("overlay.font.color"));
    int imageWidth = image.getWidth();
    int imageHeight = image.getHeight();

    g.setFont(new Font(TITLE_FONT_NAME, Font.PLAIN, TITLE_FONT_SIZE));
    String text = HIGHSCORE_TEXT;
    int highscoreTextWidth = g.getFontMetrics().stringWidth(text);

    g.drawString(text, imageWidth / 2 - highscoreTextWidth / 2, highscoreListYOffset);

    int yStart = highscoreListYOffset + ROW_SEPARATOR + TITLE_FONT_SIZE / 2;

    List<GameInfo> gameInfos = service.getGameInfos();
    gameInfos.sort((o1, o2) -> (int) (o2.getLastPlayed().getTime() - o1.getLastPlayed().getTime()));

    for (GameInfo game : gameInfos) {
      Highscore highscore = game.resolveHighscore();
      if (highscore == null) {
        LOG.info("Skipped highscore rendering of " + game.getGameDisplayName() + ", no highscore info found");
        continue;
      }

      if (gameOfTheMonth != null && gameOfTheMonth.getGameDisplayName().equals(game.getGameDisplayName())) {
        continue;
      }

      File wheelIconFile = game.getWheelIconFile();
      if(!wheelIconFile.exists() && Config.getOverlayGeneratorConfig().getBoolean("overlay.skipWithMissingWheels")) {
        continue;
      }

      if (wheelIconFile.exists()) {
        BufferedImage wheelImage = ImageIO.read(wheelIconFile);
        g.drawImage(wheelImage, ROW_PADDING_LEFT, yStart, ROW_HEIGHT, ROW_HEIGHT, null);
      }

      int x = ROW_HEIGHT + ROW_PADDING_LEFT + ROW_HEIGHT / 3;
      g.setFont(new Font(TABLE_FONT_NAME, TABLE_FONT_SIZE, TABLE_FONT_SIZE));

      String tableName = game.getGameDisplayName();
      tableName = "<p color=\"#00FF00\"" + tableName + "</p>";
      g.drawString(game.getGameDisplayName(), x, yStart + SCORE_FONT_SIZE);

      g.setFont(new Font(SCORE_FONT_NAME, SCORE_FONT_STYLE, SCORE_FONT_SIZE));
      g.drawString(highscore.getUserInitials() + " " + highscore.getScore(), x,
          yStart + SCORE_FONT_SIZE + ((ROW_HEIGHT - SCORE_FONT_SIZE) / 2) + SCORE_FONT_SIZE / 2);

      yStart = yStart + ROW_HEIGHT + ROW_SEPARATOR;
      if (!isRemainingSpaceAvailable(imageHeight, yStart)) {
        break;
      }
    }
  }

  private static boolean isRemainingSpaceAvailable(int imageHeight, int positionY) {
    int remaining = imageHeight - positionY;
    return remaining > (ROW_HEIGHT + ROW_SEPARATOR + TITLE_Y_OFFSET);
  }
}
