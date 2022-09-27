package de.mephisto.vpin.extensions.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;

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
  protected BufferedImage loadBackground(File file) throws Exception {
//    ImageIcon imageIcon = new ImageIcon(file.getAbsolutePath());
//    Image tmpImage = imageIcon.getImage();
//    BufferedImage bufferedImage = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_ARGB);
//    bufferedImage.getGraphics().drawImage(tmpImage, 0, 0, null);
//    tmpImage.flush();
//    return bufferedImage;
    if(!file.exists()) {
      throw new FileNotFoundException("File not found " + file.getAbsolutePath());
    }
    return ImageIO.read(file);
  }

  public static BufferedImage rotateLeft(BufferedImage image) {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice gd = ge.getDefaultScreenDevice();
    GraphicsConfiguration gc = gd.getDefaultConfiguration();
    return create(image, -Math.PI / 2, gc);
  }

  public static BufferedImage rotateRight(BufferedImage image) {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice gd = ge.getDefaultScreenDevice();
    GraphicsConfiguration gc = gd.getDefaultConfiguration();
    return create(image, Math.PI / 2, gc);
  }

  private static BufferedImage create(BufferedImage image, double angle, GraphicsConfiguration gc) {
    double sin = Math.abs(Math.sin(angle)), cos = Math.abs(Math.cos(angle));
    int w = image.getWidth(), h = image.getHeight();
    int neww = (int) Math.floor(w * cos + h * sin), newh = (int) Math.floor(h
        * cos + w * sin);
    int transparency = image.getColorModel().getTransparency();
    BufferedImage result = gc.createCompatibleImage(neww, newh, transparency);
    Graphics2D g = result.createGraphics();
    g.translate((neww - w) / 2, (newh - h) / 2);
    g.rotate(angle, w / 2, h / 2);
    g.drawRenderedImage(image, null);
    return result;
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
    if (strokeWidth <= 0) {
      return;
    }

    Graphics2D graphics = (Graphics2D) image.getGraphics();
    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    graphics.setStroke(new BasicStroke(strokeWidth));
    int width = image.getWidth();
    int height = image.getHeight();

    graphics.setColor(Color.WHITE);
    graphics.drawRect(strokeWidth / 2, strokeWidth / 2, width - strokeWidth, height - strokeWidth);
  }
}
