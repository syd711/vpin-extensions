package de.mephisto.vpin.popper.overlay.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;

public class VPinGraphics {
  private final static Logger LOG = LoggerFactory.getLogger(VPinGraphics.class);

  /**
   * Enables the anti aliasing for fonts
   */
  private static void setRendingHints(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
  }

  static void registerFonts(java.util.List<Font> fonts) throws Exception {
    try {
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      for (Font font : fonts) {
        ge.registerFont(font);
      }
    } catch (Exception e) {
      LOG.error("Failed to register fonts: " + e.getMessage(), e);
      throw e;
    }
  }

  static void setDefaultColor(Graphics g, String fontColor) {
    Color decode = Color.decode(fontColor);
    g.setColor(decode);
    setRendingHints(g);
  }

  static void applyAlphaComposites(BufferedImage image, float alphaWhite, float alphaBlack) {
    Graphics g = image.getGraphics();
    int imageWidth = image.getWidth();
    int imageHeight = image.getHeight();

    if (alphaWhite > 0) {
      float value = alphaWhite / 100;
      Graphics2D g2d = (Graphics2D) g.create();
      g2d.setColor(Color.WHITE);
      Rectangle rect = new Rectangle(0, 0, imageWidth, imageHeight);
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, value));
      g2d.fill(rect);
      g2d.dispose();
    }

    if (alphaBlack > 0) {
      float value = alphaBlack / 100;
      Graphics2D g2d = (Graphics2D) g.create();
      g2d.setColor(Color.BLACK);
      Rectangle rect = new Rectangle(0, 0, imageWidth, imageHeight);
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, value));
      g2d.fill(rect);
      g2d.dispose();
    }
  }

  static void drawBorder(BufferedImage image, int strokeWidth) {
    if(strokeWidth <= 0) {
      return;
    }

    Graphics2D graphics = (Graphics2D) image.getGraphics();
    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    graphics.setStroke(new BasicStroke(strokeWidth));
    int width = image.getWidth();
    int height = image.getHeight();

    graphics.setColor(Color.WHITE);
    graphics.drawRect(strokeWidth/2, strokeWidth/2, width-strokeWidth, height-strokeWidth);
  }
}
