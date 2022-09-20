package de.mephisto.vpin.extensions;

import de.mephisto.vpin.extensions.resources.ResourceLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SplashCenter extends JPanel {

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    BufferedImage bufferedImage = ResourceLoader.getResource("splash.jpg");
    g.drawImage(bufferedImage, 0, 0, this);
  }
}
