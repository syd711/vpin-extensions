package de.mephisto.vpin.extensions.overlaysettings;

import de.mephisto.vpin.VPinService;
import de.mephisto.vpin.extensions.ConfigWindow;
import de.mephisto.vpin.extensions.generator.OverlayGenerator;
import de.mephisto.vpin.extensions.util.Config;
import de.mephisto.vpin.extensions.util.Keys;
import de.mephisto.vpin.extensions.util.WidgetFactory;
import de.mephisto.vpin.util.ImageUtil;
import de.mephisto.vpin.util.PropertiesStore;
import de.mephisto.vpin.util.SystemInfo;
import net.miginfocom.swing.MigLayout;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Locale;
import java.util.Vector;

public class OverlaySettingsTab extends JPanel {
  private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(OverlaySettingsTab.class);

  private final ConfigWindow configWindow;
  private final OverlaySettingsTabActionListener actionListener;
  private final VPinService service;

  final JComboBox modifierCombo;
  final JComboBox keyCombo;

  final JLabel iconLabel;
  final JButton generateButton;
  private final JPanel previewPanel;

  public OverlaySettingsTab(ConfigWindow configWindow, VPinService service) {
    this.configWindow = configWindow;
    actionListener = new OverlaySettingsTabActionListener(this, service);
    this.service = service;
    PropertiesStore store = Config.getOverlayGeneratorConfig();

    setBackground(ConfigWindow.DEFAULT_BG_COLOR);

    setLayout(new BorderLayout());

    JPanel settingsPanel = new JPanel();
    settingsPanel.setBackground(ConfigWindow.DEFAULT_BG_COLOR);
    settingsPanel.setLayout(new MigLayout("gap rel 8 insets 10", "left", "center"));
    this.add(settingsPanel, BorderLayout.WEST);


    Vector<String> modifierNames = new Vector<>(Keys.getModifierNames());
    modifierNames.insertElementAt(null, 0);
    modifierCombo = new JComboBox(new DefaultComboBoxModel(modifierNames));
    modifierCombo.setActionCommand("modifierCombo");
    modifierCombo.addActionListener(this.actionListener);

    keyCombo = new JComboBox(new DefaultComboBoxModel(new Vector(Keys.getKeyNames())));
    keyCombo.setActionCommand("keyCombo");
    keyCombo.addActionListener(this.actionListener);


    String hotkey = Config.getOverlayGeneratorConfig().get("overlay.hotkey");
    if (hotkey != null) {
      if (hotkey.contains("+")) {
        String[] split = hotkey.split("\\+");
        String key = split[1];
        modifierCombo.setSelectedItem(Keys.getModifierName(Integer.parseInt(split[0])));
        keyCombo.setSelectedItem(key.toUpperCase(Locale.ROOT));
      }
      else {
        modifierCombo.setSelectedIndex(0);
        keyCombo.setSelectedItem(hotkey.toUpperCase(Locale.ROOT));
      }
    }

    settingsPanel.add(new JLabel("Overlay Shortcut:"));
    settingsPanel.add(modifierCombo);
    settingsPanel.add(new JLabel("+"));
    settingsPanel.add(keyCombo, "wrap");
    JCheckBox startupCheckbox = WidgetFactory.createCheckbox(settingsPanel, "", "Show after Popper Menu Launch:", store, "overlay.launchOnStartup");
    JSpinner delaySpinner = WidgetFactory.createSpinner(settingsPanel, "Launch Delay:", "seconds", store, "overlay.launchDelay", 0);
    delaySpinner.setEnabled(store.getBoolean("overlay.launchOnStartup"));
    startupCheckbox.addActionListener(e -> {
      boolean checked = startupCheckbox.isSelected();
      delaySpinner.setEnabled(checked);
    });



    JLabel separator = new JLabel("");
    separator.setPreferredSize(new Dimension(1, 30));
    settingsPanel.add(separator, "wrap");

    WidgetFactory.createTableSelector(service, settingsPanel, "Challenged Table:", store, "overlay.challengedTable", false, true);

    /******************************** Generator Fields ****************************************************************/
    WidgetFactory.createFileChooser(settingsPanel, "Background Image:", "Select File", store, "overlay.background", "background4k.jpg");
    WidgetFactory.createTextField(settingsPanel, "Challenge Title:", store, "overlay.title.text", "Table of the Month");
    WidgetFactory.createTextField(settingsPanel, "Highscores Title:", store, "overlay.highscores.text", "Latest Highscores");
    WidgetFactory.createFontSelector(settingsPanel, "Title Font:", store, "overlay.title.font", 130);
    WidgetFactory.createFontSelector(settingsPanel, "Table Name Font:", store, "overlay.table.font", 120);
    WidgetFactory.createFontSelector(settingsPanel, "Score Font:", store, "overlay.score.font", 100);
    WidgetFactory.createColorChooser(configWindow, settingsPanel, "Font Color:", store, "overlay.font.color");
    WidgetFactory.createSpinner(settingsPanel, "Padding Top:", "px", store, "overlay.title.y.offset", 80);
    WidgetFactory.createSpinner(settingsPanel, "Padding Left:", "px", store, "overlay.highscores.row.padding.left", 60);
    WidgetFactory.createSpinner(settingsPanel, "Row Separator:", "px", store, "overlay.highscores.row.separator", 32);
    WidgetFactory.createSlider(settingsPanel, "Blur Background:", store, "overlay.blur");
    WidgetFactory.createSlider(settingsPanel, "Brighten Background:", store, "overlay.alphacomposite.white");
    WidgetFactory.createSlider(settingsPanel, "Darken Background:", store, "overlay.alphacomposite.black");
    WidgetFactory.createCheckbox(settingsPanel, "Ignore Tables without Wheel Icon", store, "overlay.skipWithMissingWheels");


    settingsPanel.add(new JLabel(""));
    generateButton = new JButton("Generate Overlay");
    generateButton.setActionCommand("generateOverlay");
    generateButton.addActionListener(this.actionListener);

    JButton showOverlayButton = new JButton("Show Overlay Image");
    showOverlayButton.setActionCommand("showOverlay");
    showOverlayButton.addActionListener(this.actionListener);

    settingsPanel.add(generateButton, "span 3");
    settingsPanel.add(showOverlayButton);
    settingsPanel.add(new JLabel(""), "wrap");


    /******************************** Preview *************************************************************************/


    previewPanel = new JPanel();
    previewPanel.setLayout(new BorderLayout());
    previewPanel.setBackground(Color.BLACK);
    TitledBorder b = BorderFactory.createTitledBorder("Overlay Preview");
    b.setTitleColor(Color.WHITE);
    previewPanel.setBorder(b);
    add(previewPanel, BorderLayout.CENTER);
    iconLabel = new JLabel(getPreviewImage());
    previewPanel.add(iconLabel, BorderLayout.CENTER);
  }

  private ImageIcon getPreviewImage() {
    try {
      File file = OverlayGenerator.GENERATED_OVERLAY_FILE;
      if (!OverlayGenerator.GENERATED_OVERLAY_FILE.exists()) {
        file = new File(SystemInfo.RESOURCES, Config.getOverlayGeneratorConfig().get("overlay.background"));
      }
      BufferedImage image = ImageIO.read(file);
      image = ImageUtil.rotateRight(image);

      int maxHeight = previewPanel.getHeight();
      if(maxHeight == 0) {
        maxHeight = 900;
      }
      int percentage = (maxHeight * 100 / image.getHeight()) - 2;

      int newWidth = image.getWidth() * percentage / 100;
      int newHeight = image.getHeight() * percentage / 100;

      if(newWidth < image.getWidth() && newHeight < image.getHeight()) {
        Image newimg = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH); // scale it the smooth way
        ImageIcon imageIcon = new ImageIcon(newimg);
        return imageIcon;
      }

      return new ImageIcon(image);

    } catch (Exception e) {
      LOG.error("Error loading overlay preview: " + e.getMessage(), e);
    }
    return null;
  }

  public void generateOverlay() {
    try {
      iconLabel.setVisible(false);
      generateButton.setEnabled(false);
      OverlayGenerator.generateOverlay(service);
      iconLabel.setIcon(getPreviewImage());
      generateButton.setEnabled(true);
      iconLabel.setVisible(true);
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this.configWindow, "Error generating overlay: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
}
