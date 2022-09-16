package de.mephisto.vpin.popper.overlay.util;

import de.mephisto.vpin.popper.overlay.ConfigWindow;
import de.mephisto.vpin.util.PropertiesStore;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class ColorDialog extends JDialog {

  public ColorDialog(JFrame parent, JLabel valueLabel, PropertiesStore store, String property) {
    super(parent);
    String value = store.getString(property, "#FFFFFF");
    final JColorChooser fontColorChooser = new JColorChooser();
    fontColorChooser.setLocale(Locale.ENGLISH);
    fontColorChooser.setColor(Color.decode(value));
    fontColorChooser.getSelectionModel().addChangeListener(e -> {
      Color color = fontColorChooser.getColor();
      String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
      store.set(property, hex);
      valueLabel.setText(hex);
    });
    fontColorChooser.setBackground(ConfigWindow.DEFAULT_BG_COLOR);


    this.setBackground(ConfigWindow.DEFAULT_BG_COLOR);
    this.setLayout(new BorderLayout());
    this.setModal(true);
    this.setSize(450, 380);
    this.setTitle("Color Selection");
    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
    int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
    setLocation(x, y);

    this.add(fontColorChooser, BorderLayout.CENTER);

    JToolBar tb = new JToolBar();
    tb.setBorder(BorderFactory.createEmptyBorder(4, 4, 8, 8));
    tb.setLayout(new BorderLayout());
    tb.setFloatable(false);
    JButton close = new JButton("Close");
    close.setMinimumSize(new Dimension(60, 30));
    close.addActionListener(e -> setVisible(false));
    tb.add(close, BorderLayout.EAST);
    this.add(tb, BorderLayout.SOUTH);
    this.setVisible(true);
  }
}
