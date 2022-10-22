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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CardGraphics {
  private final static Logger LOG = LoggerFactory.getLogger(CardGraphics.class);

  private final int ROW_SEPARATOR = Config.getCardGeneratorConfig().getInt("card.highscores.row.separator");
  private final int WHEEL_PADDING = Config.getCardGeneratorConfig().getInt("card.highscores.row.padding.left");

  private final String TITLE_TEXT = Config.getCardGeneratorConfig().getString("card.title.text");

  private final String SCORE_FONT_NAME = Config.getCardGeneratorConfig().getString("card.score.font.name");
  private final int SCORE_FONT_STYLE = Config.getCardGeneratorConfig().getInt("card.score.font.style");
  private final int SCORE_FONT_SIZE = Config.getCardGeneratorConfig().getInt("card.score.font.size");

  private final String TITLE_FONT_NAME = Config.getCardGeneratorConfig().getString("card.title.font.name");
  private final int TITLE_FONT_STYLE = Config.getCardGeneratorConfig().getInt("card.title.font.style");
  private final int TITLE_FONT_SIZE = Config.getCardGeneratorConfig().getInt("card.title.font.size");

  private final String TABLE_FONT_NAME = Config.getCardGeneratorConfig().getString("card.table.font.name");
  private final int TABLE_FONT_STYLE = Config.getCardGeneratorConfig().getInt("card.table.font.style");
  private final int TABLE_FONT_SIZE = Config.getCardGeneratorConfig().getInt("card.table.font.size");

  private final int PADDING = Config.getCardGeneratorConfig().getInt("card.title.y.offset");

  private final boolean RAW_HIGHSCORE = Config.getCardGeneratorConfig().getBoolean("card.rawHighscore");

  private final boolean USE_DIRECTB2S = Config.getCardGeneratorConfig().getBoolean("card.useDirectB2S");
  private final B2SImageRatio DIRECTB2S_RATIO = B2SImageRatio.valueOf(Config.getCardGeneratorConfig().getString("card.ratio", B2SImageRatio.RATIO_16x9.name()));
  private final int BLUR_PIXELS = Config.getCardGeneratorConfig().getInt("card.blur");

  public BufferedImage drawHighscores(VPinService service, GameInfo game) throws Exception {
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
    Highscore highscore = game.resolveHighscore();
    if (highscore != null) {
      Graphics g = image.getGraphics();
      ImageUtil.setDefaultColor(g, Config.getCardGeneratorConfig().getString("card.font.color"));
      int imageWidth = image.getWidth();

      g.setFont(new Font(TITLE_FONT_NAME, TITLE_FONT_STYLE, TITLE_FONT_SIZE));

      String title = TITLE_TEXT;
      int titleWidth = g.getFontMetrics().stringWidth(title);
      int titleY = TITLE_FONT_SIZE + PADDING;
      g.drawString(title, imageWidth / 2 - titleWidth / 2, titleY);

      g.setFont(new Font(TABLE_FONT_NAME, TABLE_FONT_STYLE, TABLE_FONT_SIZE));
      String tableName = game.getGameDisplayName();
      int width = g.getFontMetrics().stringWidth(tableName);
      int tableNameY = titleY + TABLE_FONT_SIZE + TABLE_FONT_SIZE / 2;
      g.drawString(tableName, imageWidth / 2 - width / 2, tableNameY);

      if (RAW_HIGHSCORE) {
        int yStart = tableNameY + TABLE_FONT_SIZE + TABLE_FONT_SIZE / 2;
        renderRawScore(game, image.getHeight(), image.getWidth(), highscore, g, yStart);
      }
      else {
        renderScorelist(game, highscore, g, title, tableNameY);
      }
    }
  }

  private void renderScorelist(GameInfo game,
                               Highscore highscore,
                               Graphics g, String title, int tableNameY) throws IOException {
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

  private void renderRawScore(GameInfo game, int imageHeight, int imageWidth, Highscore highscore, Graphics g, int yStart) throws IOException {
    int remainingHeight = imageHeight - yStart - PADDING;
    int remainingWidth = imageWidth - 2 * PADDING;
    String raw = highscore.getRaw().trim();
    String[] lines = raw.split("\n");

    int fontSize = remainingHeight / lines.length;
    if (fontSize > SCORE_FONT_SIZE) {
      fontSize = SCORE_FONT_SIZE;
    }
    else if (fontSize < 20) {
      fontSize = 20;
    }
    g.setFont(new Font(SCORE_FONT_NAME, SCORE_FONT_STYLE, fontSize));


    LOG.info("Card rendering initialized with font size " + g.getFont().getSize());
    List<TextBlock> textBlocks = createTextBlocks(Arrays.asList(lines), g, imageHeight - PADDING);
    LOG.info("Blocks are downscaling font size to " + g.getFont().getSize());

    List<TextColumn> textColumns = createTextColumns(textBlocks, g, remainingHeight);
    scaleDownToWidth(remainingWidth, g, textColumns);
    LOG.info("Columns are downscaling font size to " + g.getFont().getSize());

    yStart = centerYToRemainingSpace(textColumns, yStart, remainingHeight);
    int columnsWidth = getColumnsWidth(textColumns);
    int remainingXSpace = remainingWidth - columnsWidth;

    int x = PADDING;
    int wheelWidth = PADDING * 2 + TITLE_FONT_SIZE * 2;
    File wheelIconFile = game.getWheelIconFile();

    if (wheelIconFile.exists() && (remainingXSpace + 2 * PADDING) > TITLE_FONT_SIZE * 2) {
      x = (remainingWidth - wheelWidth - columnsWidth) / 2;
      int wheelSize = 2 * TITLE_FONT_SIZE;
      BufferedImage wheelImage = ImageIO.read(wheelIconFile);
      g.drawImage(wheelImage, x, yStart, wheelSize, wheelSize, null);
      x = x + wheelWidth;
    }
    else {
      x = (remainingWidth - columnsWidth) / 2;
    }

    for (TextColumn textColumn : textColumns) {
      textColumn.renderAt(g, x, yStart);
      x = x + textColumn.getWidth();
    }
  }

  private int getColumnsWidth(List<TextColumn> textColumns) {
    int width = 0;
    for (TextColumn textColumn : textColumns) {
      width = width + textColumn.getWidth();
    }
    return width;
  }

  private int centerYToRemainingSpace(List<TextColumn> textColumns, int yStart, int remainingHeight) {
    int maxHeight = computeMaxHeight(textColumns);
    return yStart + ((remainingHeight - maxHeight) / 2);
  }

  private void scaleDownToWidth(int imageWidth, Graphics g, List<TextColumn> textColumns) {
    int width = computeTotalWidth(textColumns);
    while (width >= imageWidth) {
      int fontSize = g.getFont().getSize() - 1;
      g.setFont(new Font(SCORE_FONT_NAME, SCORE_FONT_STYLE, fontSize));
      width = computeTotalWidth(textColumns);
    }
  }

  private int computeMaxHeight(List<TextColumn> columns) {
    int height = 0;
    for (TextColumn column : columns) {
      if (column.getHeight() > height) {
        height = column.getHeight();
      }
    }
    return height;
  }

  private int computeTotalWidth(List<TextColumn> columns) {
    int width = 0;
    for (TextColumn column : columns) {
      int columnWidth = column.getWidth();
      width = width + columnWidth;
    }
    return width;
  }

  List<TextColumn> createTextColumns(List<TextBlock> blocks, Graphics g, int remainingHeight) {
    List<TextColumn> columns = new ArrayList<>();

    //scale down block until every one is matching
    for (TextBlock block : blocks) {
      while (block.getHeight() > remainingHeight) {
        int fontSize = g.getFont().getSize() - 1;
        g.setFont(new Font(SCORE_FONT_NAME, SCORE_FONT_STYLE, fontSize));
      }
    }

    TextColumn column = new TextColumn();
    int columnHeight = remainingHeight;
    for (TextBlock block : blocks) {
      int height = block.getHeight();
      if ((columnHeight - height) < 0) {
        columns.add(column);
        column = new TextColumn();
        columnHeight = remainingHeight;
      }

      columnHeight = columnHeight - height;
      column.addBlock(block);
    }
    columns.add(column);
    return columns;
  }

  List<TextBlock> createTextBlocks(List<String> lines, Graphics g, int maxY) {
    List<TextBlock> result = new ArrayList<>();

    TextBlock textBlock = new TextBlock(g, maxY);
    for (String line : lines) {
      if (line.trim().equals("")) {
        if (!textBlock.isEmpty()) {
          result.add(textBlock);
        }
        textBlock = new TextBlock(g, maxY);
      }
      else {
        textBlock.addLine(line);
      }
    }

    if (!textBlock.isEmpty()) {
      result.add(textBlock);
    }

    return result;
  }

  class TextColumn {
    private final List<TextBlock> blocks = new ArrayList<>();

    TextColumn() {
    }

    public void addBlock(TextBlock block) {
      this.blocks.add(block);
    }

    public int getWidth() {
      int width = 0;
      for (TextBlock block : blocks) {
        if (block.getWidth() > width) {
          width = block.getWidth();
        }
      }
      return width;
    }


    public void renderAt(Graphics g, int x, int y) {
      int startY = y;
      for (TextBlock block : blocks) {
        startY = block.renderAt(g, x, startY);
      }
    }

    public int getHeight() {
      int height = 0;
      for (TextBlock block : blocks) {
        height = height + block.getHeight();
      }
      return height;
    }
  }

  class TextBlock {
    private List<String> lines = new ArrayList<>();
    private final Graphics g;
    private final int maxY;

    TextBlock(Graphics g, int maxY) {
      this(new ArrayList<>(), g, maxY);
    }

    TextBlock(List<String> lines, Graphics g, int maxY) {
      this.lines = lines;
      this.g = g;
      this.maxY = maxY;
    }

    public void addLine(String line) {
      this.lines.add(line);
    }

    public int renderAt(Graphics g, int x, int y) {
      for (String line : lines) {
        //we add a whitespace for every line, nicer formatting for multi-column
        g.drawString(line, x + g.getFontMetrics().stringWidth(" "), y);
        y = y + g.getFont().getSize();
      }
      y = y + g.getFont().getSize(); //render extra blank line
      return y;
    }

    public int getHeight() {
      return (this.lines.size() + 1) * g.getFont().getSize(); //render extra blank line
    }

    public int getWidth() {
      int maxWidth = 0;
      for (String line : lines) {
        int width = g.getFontMetrics().stringWidth(line + "   ");
        if (width > maxWidth) {
          maxWidth = width;
        }
      }

      return maxWidth;
    }

    public boolean isEmpty() {
      return lines.isEmpty();
    }
  }
}
