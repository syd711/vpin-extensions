package de.mephisto.vpin.extensions.cardsettings;

import de.mephisto.vpin.GameInfo;
import de.mephisto.vpin.VPinService;
import de.mephisto.vpin.b2s.B2SImageRatio;
import de.mephisto.vpin.extensions.ConfigWindow;
import de.mephisto.vpin.extensions.generator.CardGenerator;
import de.mephisto.vpin.extensions.util.Config;
import de.mephisto.vpin.extensions.util.ProgressDialog;
import de.mephisto.vpin.extensions.util.ProgressResultModel;
import de.mephisto.vpin.extensions.util.WidgetFactory;
import de.mephisto.vpin.popper.PopperScreen;
import de.mephisto.vpin.util.PropertiesStore;
import de.mephisto.vpin.util.SystemInfo;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CardSettingsTab extends JPanel {
  private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(CardSettingsTab.class);
  private final JLabel previewLabel;
  private final JComboBox backgroundSelector;
  private final JComboBox ratioCombo;
  private final JCheckBox useB2SCheckbox;
  private final JPanel previewPanel;
  private final JCheckBox rawHighscoreCheckbox;
  private final JSpinner rowSeparatorSpinner;
  private final JSpinner wheelPaddingSpinner;

  private VPinService service;

  private final ConfigWindow configWindow;

  private final CardSettingsTabActionListener actionListener;
  private final JButton generateAllButton;
  final JLabel iconLabel;
  final JButton generateButton;

  public CardSettingsTab(ConfigWindow configWindow, VPinService service) {
    this.configWindow = configWindow;
    actionListener = new CardSettingsTabActionListener(this, service);
    this.service = service;
    PropertiesStore store = Config.getCardGeneratorConfig();

    setBackground(ConfigWindow.DEFAULT_BG_COLOR);

    setLayout(new BorderLayout());

    JPanel settingsPanel = new JPanel();
    settingsPanel.setBackground(ConfigWindow.DEFAULT_BG_COLOR);
    settingsPanel.setLayout(new MigLayout("gap rel 8 insets 10", "left", "center"));
    this.add(settingsPanel, BorderLayout.WEST);


    List<String> values = Arrays.stream(PopperScreen.values()).sequential().map(e -> e.toString()).collect(Collectors.toList());
    values.add(0, null);
    JComboBox screenCombo = WidgetFactory.createCombobox(settingsPanel, values, "PinUP Popper Screen:", store, "popper.screen");
    JLabel warnLabel = WidgetFactory.createLabel(settingsPanel, getScreenStatusMessage(store.getString("popper.screen")), Color.RED);

    JLabel separator = new JLabel("");
    separator.setPreferredSize(new Dimension(1, 24));
    settingsPanel.add(separator, "wrap");

    JComboBox tableSelector = WidgetFactory.createTableSelector(service, settingsPanel, "Sample Table:", store, "card.sampleTable", true, false);
    tableSelector.addActionListener(e -> {
      GameInfo selectedItem = (GameInfo) tableSelector.getSelectedItem();
      if(selectedItem != null) {
        generateSampleCard(selectedItem);
      }
    });

    separator = new JLabel("");
    separator.setPreferredSize(new Dimension(1, 12));
    settingsPanel.add(separator, "wrap");

    /******************************** Generator Fields ****************************************************************/
    rawHighscoreCheckbox = WidgetFactory.createCheckbox(settingsPanel, "", "Render Raw Higscore:", store, "card.rawHighscore");
    useB2SCheckbox = WidgetFactory.createCheckbox(settingsPanel, "", "Prefer DirectB2S Background (if available):", store, "card.useDirectB2S");
    useB2SCheckbox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ratioCombo.setEnabled(useB2SCheckbox.isSelected());
      }
    });

    ratioCombo = WidgetFactory.createCombobox(settingsPanel, Arrays.asList(B2SImageRatio.RATIO_16x9.toString(), B2SImageRatio.RATIO_4x3.toString()), "Image Ratio:", store, "card.ratio");
    ratioCombo.setEnabled(store.getBoolean("card.useDirectB2S"));

    backgroundSelector = WidgetFactory.createCombobox(settingsPanel, new File(SystemInfo.RESOURCES + "backgrounds/"), "Default Background:", store, "card.background");
    backgroundSelector.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ImageIcon previewIcon = getPreviewIcon();
        if (previewIcon != null) {
          previewLabel.setIcon(previewIcon);
        }
      }
    });
    previewLabel = WidgetFactory.createLabel(settingsPanel, "", "");
    ImageIcon previewIcon = getPreviewIcon();
    if (previewIcon != null) {
      previewLabel.setIcon(previewIcon);
    }


    WidgetFactory.createTextField(settingsPanel, "Card Title:", store, "card.title.text", "Highscore");
    WidgetFactory.createFontSelector(settingsPanel, "Title Font:", store, "card.title.font", 120);
    WidgetFactory.createFontSelector(settingsPanel, "Table Name Font:", store, "card.table.font", 100);
    WidgetFactory.createFontSelector(settingsPanel, "Score Font:", store, "card.score.font", 80);
    WidgetFactory.createColorChooser(configWindow, settingsPanel, "Font Color:", store, "card.font.color");
    WidgetFactory.createSpinner(settingsPanel, "Padding:", "px", store, "card.title.y.offset", 80);
    wheelPaddingSpinner = WidgetFactory.createSpinner(settingsPanel, "Wheel Image Padding:", "px", store, "card.highscores.row.padding.left", 60);
    rowSeparatorSpinner = WidgetFactory.createSpinner(settingsPanel, "Row Separator:", "px", store, "card.highscores.row.separator", 10);
    WidgetFactory.createSlider(settingsPanel, "Blur Background:", store, "card.blur");
    WidgetFactory.createSlider(settingsPanel, "Brighten Background:", store, "card.alphacomposite.white");
    WidgetFactory.createSlider(settingsPanel, "Darken Background:", store, "card.alphacomposite.black");
    WidgetFactory.createSlider(settingsPanel, "Border Size:", store, "card.border.width");

    rawHighscoreCheckbox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        rowSeparatorSpinner.setEnabled(!rawHighscoreCheckbox.isSelected());
        wheelPaddingSpinner.setEnabled(!rawHighscoreCheckbox.isSelected());
      }
    });
    rowSeparatorSpinner.setEnabled(!rawHighscoreCheckbox.isSelected());
    wheelPaddingSpinner.setEnabled(!rawHighscoreCheckbox.isSelected());

    settingsPanel.add(new JLabel(""));
    generateButton = new JButton("Generate Sample Card");
    generateButton.setEnabled(false);
    generateButton.setActionCommand("generateCard");
    generateButton.addActionListener(this.actionListener);

    JButton showActiveCardButton = new JButton("Open Sample Card Image");
    showActiveCardButton.setActionCommand("showCard");
    showActiveCardButton.addActionListener(this.actionListener);

    settingsPanel.add(generateButton, "span 3");
    settingsPanel.add(showActiveCardButton);
    settingsPanel.add(new JLabel(""), "wrap");


    settingsPanel.add(new JLabel(""));
    generateAllButton = new JButton("Pre-Generate All Cards (!)");
    generateAllButton.setEnabled(false);
    generateAllButton.setActionCommand("generateAllCards");
    generateAllButton.addActionListener(this.actionListener);
    settingsPanel.add(generateAllButton, "span 3");
    settingsPanel.add(new JLabel(""), "wrap");


    String selection = (String) screenCombo.getSelectedItem();
    generateButton.setEnabled(!StringUtils.isEmpty(selection));
    generateAllButton.setEnabled(!StringUtils.isEmpty(selection));
    showActiveCardButton.setEnabled(!StringUtils.isEmpty(selection));
    warnLabel.setVisible(!StringUtils.isEmpty(selection));
    screenCombo.addActionListener(e -> {
      String s = (String) screenCombo.getSelectedItem();
      generateButton.setEnabled(!StringUtils.isEmpty(s));
      generateAllButton.setEnabled(!StringUtils.isEmpty(s));
      showActiveCardButton.setEnabled(!StringUtils.isEmpty(s));
      warnLabel.setText(getScreenStatusMessage(s));
      warnLabel.setVisible(!StringUtils.isEmpty(s));
    });

    /******************************** Preview *************************************************************************/


    previewPanel = new JPanel();
    previewPanel.setMinimumSize(new Dimension(780, configWindow.getHeight() - 20));
    previewPanel.setBackground(Color.BLACK);
    TitledBorder preview = BorderFactory.createTitledBorder("Sample Preview");
    preview.setTitleColor(Color.WHITE);
    previewPanel.setBorder(preview);
    add(previewPanel, BorderLayout.CENTER);
    previewPanel.setLayout(new MigLayout("gap rel 8 insets 10", "left"));
    iconLabel = new JLabel("");
    iconLabel.setBackground(Color.BLACK);

    iconLabel.setIcon(getPreviewImage());
    previewPanel.add(iconLabel);
  }

  private ImageIcon getPreviewIcon() {
    File file = new File(SystemInfo.RESOURCES + "backgrounds/", (String) backgroundSelector.getSelectedItem());
    try {
      BufferedImage image = ImageIO.read(file);
      int percentage = 10;
      Image newimg = image.getScaledInstance(image.getWidth() * percentage / 100, image.getHeight() * percentage / 100, Image.SCALE_SMOOTH); // scale it the smooth way
      return new ImageIcon(newimg);
    } catch (IOException e) {
      LOG.error("Failed to read background preview image " + file.getAbsolutePath() + ": " + e.getMessage(), e);
    }
    return null;
  }

  private String getScreenStatusMessage(String screenName) {
    if (!StringUtils.isEmpty(screenName)) {
      PopperScreen screen = PopperScreen.valueOf(screenName);
      String validationMsg = service.validateScreenConfiguration(screen);

      String warning = "Selecting a PinUP Popper screen will enable highscore card generation.<br/>Highscore cards will be generated on table exit.<br/>Existing media will be overwritten for this screen!";
      if (validationMsg != null) {
        warning += "<br/><br/>Configuration Error:<br/><b>" + validationMsg + "</b>";
      }
      return "<html><body>" + warning + "</body></html>";
    }
    return "";
  }

  private ImageIcon getPreviewImage() {
    try {
      GameInfo gameInfo = this.getSampleGame();
      if (gameInfo != null && getScreen() != null) {
        File file = new File(SystemInfo.RESOURCES, Config.getCardGeneratorConfig().get("card.background"));
        File sampleFile = CardGenerator.SAMPLE_FILE;
        if (sampleFile.exists()) {
          file = sampleFile;
        }
        BufferedImage image = ImageIO.read(file);
        int maxWidth = previewPanel.getWidth();
        if(maxWidth == 0) {
          maxWidth = 740;
        }

        int percentage = (maxWidth * 100 / image.getWidth()) - 7;

        int newWidth = image.getWidth() * percentage / 100;
        int newHeight = image.getHeight() * percentage / 100;

        if(newWidth < image.getWidth() && newHeight < image.getHeight()) {
          Image newimg = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH); // scale it the smooth way
          ImageIcon imageIcon = new ImageIcon(newimg);
          return imageIcon;
        }

        return new ImageIcon(image);
      }
    } catch (Exception e) {
      LOG.error("Error loading card preview: " + e.getMessage());
    }
    return null;
  }

  private PopperScreen getScreen() {
    String screen = Config.getCardGeneratorConfig().getString("popper.screen");
    if (!StringUtils.isEmpty(screen)) {
      return PopperScreen.valueOf(screen);
    }
    return null;
  }

  public GameInfo getSampleGame() {
    int gameId = Config.getCardGeneratorConfig().getInt("card.sampleTable");
    if (gameId > 0) {
      return service.getGameInfo(gameId);
    }

    List<GameInfo> gameInfos = service.getGameInfos();
    for (GameInfo gameInfo : gameInfos) {
      if (gameInfo.resolveHighscore() != null) {
        return gameInfo;
      }
    }
    return null;
  }

  public void generateSampleCard(GameInfo sampleGame) {
    try {
      if (sampleGame.resolveHighscore() == null) {
        JOptionPane.showMessageDialog(this, "No highscore files found for " + sampleGame.toString() + ".\nCheck the 'Table Overview' tab for tables with existing highscore files.", "Error", JOptionPane.INFORMATION_MESSAGE);
        return;
      }

      iconLabel.setVisible(false);
      generateButton.setEnabled(false);

      File directB2SImage = sampleGame.getDirectB2SImage();
      if (directB2SImage.exists()) {
        directB2SImage.delete();
      }
      CardGenerator.generateCard(service, sampleGame, CardGenerator.SAMPLE_FILE);
      iconLabel.setIcon(getPreviewImage());
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this.configWindow, "Error generating overlay: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
      generateButton.setEnabled(true);
      iconLabel.setVisible(true);
    }
  }

  public void generateAllCards() {
    try {
      int warning = JOptionPane.showConfirmDialog(this.configWindow, "This will overwrite all existing media for screen '" + getScreen()
          + "'.\nThese files will be updated or created once a highscore of a table has been updated.\n\nStart Card Generation?", "Warning", JOptionPane.YES_NO_OPTION);
      if (warning == JOptionPane.OK_OPTION) {
        service.refreshGameInfos();
        generateButton.setEnabled(false);

        ProgressDialog d = new ProgressDialog(configWindow, new GeneratorProgressModel(service, this.getScreen(), "Generating Cards"));
        ProgressResultModel progressResultModel = d.showDialog();

        JOptionPane.showMessageDialog(configWindow, "Finished highscore card pre-generation, generated cards for "
                + (progressResultModel.getProcessed() - progressResultModel.getSkipped()) + " of " + progressResultModel.getProcessed() + " tables.",
            "Generation Finished", JOptionPane.INFORMATION_MESSAGE);
        LOG.info("Finished highscore card generation.");
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this.configWindow, "Error generating overlay: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
      generateButton.setEnabled(true);
    }
  }

  public void showGeneratedCard() {
    try {
      File file = CardGenerator.SAMPLE_FILE;
      if (file.exists()) {
        Desktop.getDesktop().open(file);
      }
    } catch (IOException ex) {
      LOG.error("Failed to open card file: " + ex.getMessage(), ex);
    }
  }
}
