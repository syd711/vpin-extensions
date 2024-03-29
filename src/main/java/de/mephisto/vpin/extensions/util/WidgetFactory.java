package de.mephisto.vpin.extensions.util;

import de.mephisto.vpin.GameInfo;
import de.mephisto.vpin.VPinService;
import de.mephisto.vpin.extensions.ConfigWindow;
import de.mephisto.vpin.util.PropertiesStore;
import de.mephisto.vpin.util.SystemInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class WidgetFactory {
  private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(WidgetFactory.class);

  public static JButton createConfigButton(JPanel parent,
                                           String actionCommand,
                                           String text,
                                           String label,
                                           ActionListener actionListener) {
    parent.add(new JLabel(label));
    JButton button = new JButton(text);
    button.setActionCommand(actionCommand);
    button.setToolTipText(text);
    button.addActionListener(actionListener);
    parent.add(button, "span 3");
    parent.add(new JLabel(""), "wrap");
    return button;
  }

  public static JButton createButton(JPanel parent,
                                     String actionCommand,
                                     String text,
                                     ActionListener actionListener) {
    JButton button = new JButton(text);
    button.setActionCommand(actionCommand);
    button.setToolTipText(text);
    button.addActionListener(actionListener);
    parent.add(button);
    return button;
  }

  public static JComboBox createTableSelector(VPinService service, JPanel parent, String title, PropertiesStore store, String property, boolean filterForHighscores, boolean useActiveGamesOnly) {
    List<GameInfo> gameInfos = service.getGameInfos();
    if(useActiveGamesOnly) {
      gameInfos = service.getActiveGameInfos();
    }

    if (filterForHighscores) {
      gameInfos = gameInfos.stream().filter(g -> g.hasHighscore()).collect(Collectors.toList());
    }
    Vector<GameInfo> data = new Vector<>(gameInfos);
    data.insertElementAt(null, 0);
    final JComboBox tableSelection = new JComboBox(data);
    tableSelection.addActionListener(e -> {
      GameInfo selectedItem = (GameInfo) tableSelection.getSelectedItem();
      String value = "";
      if (selectedItem != null) {
        value = String.valueOf(selectedItem.getId());
      }
      store.set(property, value);
    });
    int selection = store.getInt(property);
    if (selection > 0) {
      GameInfo gameInfo = service.getGameInfo(selection);
      tableSelection.setSelectedItem(gameInfo);
    }

    parent.add(new JLabel(title));
    parent.add(tableSelection, "span 3");
    parent.add(new JLabel(""), "width 30:200:200");
    parent.add(new JLabel(""), "wrap");

    return tableSelection;
  }

  public static JComboBox createCombobox(JPanel parent, List<String> values, String title, PropertiesStore store, String property) {
    Vector<String> data = new Vector<>(values);
    final JComboBox combo = new JComboBox(data);
    combo.addActionListener(e -> {
      String selectedItem = (String) combo.getSelectedItem();
      if (selectedItem == null) {
        selectedItem = "";
      }
      store.set(property, selectedItem);
    });
    String selection = store.getString(property);
    if (!StringUtils.isEmpty(selection)) {
      combo.setSelectedItem(selection);
    }

    parent.add(new JLabel(title));
    parent.add(combo, "span 3");
    parent.add(new JLabel(""), "width 30:200:200");
    parent.add(new JLabel(""), "wrap");

    return combo;
  }

  public static JComboBox createCombobox(JPanel parent, File folder, String title, PropertiesStore store, String property) {
    String[] files = folder.list((dir, name) -> name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg"));
    Vector<String> data = new Vector<>(Arrays.asList(files));
    final JComboBox combo = new JComboBox(data);
    combo.addActionListener(e -> {
      String selectedItem = (String) combo.getSelectedItem();
      if (selectedItem == null) {
        selectedItem = "";
      }
      store.set(property, selectedItem);
    });
    String selection = store.getString(property);
    if (!StringUtils.isEmpty(selection)) {
      combo.setSelectedItem(selection);
    }
    else {
      combo.setSelectedItem(files[0]);
      store.set(property, files[0]);
    }

    parent.add(new JLabel(title));
    parent.add(combo, "span 3");
    JButton openButton = new JButton("Upload Background");
    parent.add(openButton);
    openButton.addActionListener(e -> {
      final JFileChooser field = new JFileChooser();
      field.setCurrentDirectory(new File("./"));
      field.setFileFilter(new FileFilter() {
        @Override
        public boolean accept(File f) {
          return f.isDirectory() || f.getName().endsWith("png") || f.getName().endsWith(".jpg") || f.getName().endsWith(".jpeg");
        }

        @Override
        public String getDescription() {
          return "Pictures";
        }
      });
      int returnCode = field.showOpenDialog(parent);
      if (returnCode == JFileChooser.APPROVE_OPTION) {
        File selectedFile = field.getSelectedFile();
        String name = selectedFile.getName();
        File target = new File(SystemInfo.RESOURCES + "backgrounds", name);
        Path normalizedSource = Paths.get(selectedFile.getAbsolutePath()).normalize();
        Path normalizedTarget = Paths.get(target.getAbsolutePath()).normalize();
        if (!normalizedSource.toString().equals(normalizedTarget.toString())) {
          if (target.exists()) {
            target.delete();
          }
          try {
            FileUtils.copyFile(selectedFile, target);
            LOG.info("Written " + target.getAbsolutePath());
            combo.addItem(target.getName());
            combo.setSelectedItem(target.getName());
          } catch (IOException ex) {
            LOG.error("Error selecting file: " + ex.getMessage(), ex);
          }
        }

        store.set(property, field.getSelectedFile().getName());
      }
    });
    parent.add(new JLabel(""), "wrap");

    return combo;
  }

  public static JLabel createLabel(JPanel parent, String title, Color color) {
    parent.add(new JLabel(""));
    JLabel label = new JLabel(title);
    label.setForeground(color);
    parent.add(label, "span 4");
    parent.add(new JLabel(""), "wrap");
    return label;
  }

  public static JLabel createLabel(JPanel parent, String title, String value) {
    parent.add(new JLabel(title));
    JLabel label = new JLabel(value);
    parent.add(label, "span 4");
    parent.add(new JLabel(""), "wrap");
    return label;
  }

  public static JCheckBox createCheckbox(JPanel parent, String title, String label, PropertiesStore store, String key) {
    parent.add(new JLabel(label));
    final JCheckBox field = new JCheckBox(title);
    field.setMinimumSize(new Dimension(330, 26));
    field.setBackground(ConfigWindow.DEFAULT_BG_COLOR);
    field.addActionListener(e -> {
      boolean checked = field.isSelected();
      store.set(key, String.valueOf(checked));
    });
    field.setSelected(store.getBoolean(key));
    parent.add(field, "span 4");
    parent.add(new JLabel(""), "wrap");
    return field;
  }

  public static JCheckBox createCheckbox(JPanel parent, String title, PropertiesStore store, String key) {
    parent.add(new JLabel(""));
    final JCheckBox field = new JCheckBox(title);
    field.setMinimumSize(new Dimension(330, 26));
    field.setBackground(ConfigWindow.DEFAULT_BG_COLOR);
    field.addActionListener(e -> {
      boolean checked = field.isSelected();
      store.set(key, String.valueOf(checked));
    });
    field.setSelected(store.getBoolean(key));
    parent.add(field, "span 4");
    parent.add(new JLabel(""), "wrap");
    return field;
  }

  public static void createTextField(JPanel parent, String title, PropertiesStore store, String property, String defaultValue) {
    parent.add(new JLabel(title));
    String value = store.getString(property, defaultValue);
    final JTextField field = new JTextField(value);
    field.setMinimumSize(new Dimension(330, 26));
    parent.add(field, "span 5");
    parent.add(new JLabel(""), "wrap");
    field.getDocument().addDocumentListener(new DocumentListener() {
                                              @Override
                                              public void insertUpdate(DocumentEvent e) {
                                                String text = field.getText();
                                                store.set(property, text);
                                              }

                                              @Override
                                              public void removeUpdate(DocumentEvent e) {
                                                String text = field.getText();
                                                store.set(property, text);
                                              }

                                              @Override
                                              public void changedUpdate(DocumentEvent e) {
                                                String text = field.getText();
                                                store.set(property, text);
                                              }
                                            }
    );
  }

  public static JSpinner createSpinner(JPanel parent, String title, String unit, PropertiesStore store, String property, int defaultValue) {
    parent.add(new JLabel(title));
    int value = store.getInt(property, defaultValue);
    final JSpinner field = new JSpinner(new SpinnerNumberModel(0, 0, 999999, 1));
    field.setValue(value);
    field.setMinimumSize(new Dimension(100, 26));
    parent.add(field);
    parent.add(new JLabel(unit), "span 2");
    parent.add(new JLabel(""), "wrap");
    field.addChangeListener(e -> {
      int fieldValue = (int) field.getValue();
      store.set(property, fieldValue);
    });
    return field;
  }

  public static void createFileChooser(JPanel parent, String label, String buttonLabel, PropertiesStore store, String property, String defaultValue) {
    parent.add(new JLabel(label));
    String value = store.getString(property, defaultValue);
    JLabel fileNameLabel = new JLabel(value);
    parent.add(fileNameLabel, "span 2");
    JButton openButton = new JButton(buttonLabel);
    parent.add(openButton, "span 1");
    parent.add(new JLabel(""), "wrap");
    openButton.addActionListener(e -> {
      final JFileChooser field = new JFileChooser();
      field.setCurrentDirectory(new File("./"));
      field.setFileFilter(new FileFilter() {
        @Override
        public boolean accept(File f) {
          return f.isDirectory() || f.getName().endsWith("png") || f.getName().endsWith(".jpg") || f.getName().endsWith(".jpeg");
        }

        @Override
        public String getDescription() {
          return "Pictures";
        }
      });
      int returnCode = field.showOpenDialog(parent);
      if (returnCode == JFileChooser.APPROVE_OPTION) {
        File selectedFile = field.getSelectedFile();
        String name = selectedFile.getName();
        File target = new File(SystemInfo.RESOURCES, name);
        Path normalizedSource = Paths.get(selectedFile.getAbsolutePath()).normalize();
        Path normalizedTarget = Paths.get(target.getAbsolutePath()).normalize();
        if (!normalizedSource.toString().equals(normalizedTarget.toString())) {
          if (target.exists()) {
            target.delete();
          }
          try {
            FileUtils.copyFile(selectedFile, target);
          } catch (IOException ex) {
            LOG.error("Error selecting file: " + ex.getMessage(), ex);
          }
        }

        store.set(property, field.getSelectedFile().getName());
        fileNameLabel.setText(field.getSelectedFile().getName());
      }
    });
  }

  public static void createFontSelector(JPanel parent, String label, PropertiesStore store, String property, int defaultSize) {
    parent.add(new JLabel(label));
    String name = store.getString(property + ".name", "Arial");
    int size = store.getInt(property + ".size", defaultSize);
    final JLabel titleFontLabel = new JLabel(name + " / " + size);
    parent.add(titleFontLabel, "span 3");
    JButton fontChooserButton = new JButton("Choose Font");
    fontChooserButton.addActionListener(e -> {
      JFontChooser fontChooser = new JFontChooser();
      fontChooser.setSelectedFontSize(store.getInt(property + ".size", 48));
      fontChooser.setSelectedFontFamily(store.getString(property + ".name", "Arial"));
      fontChooser.setSelectedFontStyle(store.getInt(property + ".font.style", 0));
      int result = fontChooser.showDialog(parent);
      if (result == JFontChooser.OK_OPTION) {
        Font font = fontChooser.getSelectedFont();
        store.set(property + ".size", font.getSize());
        store.set(property + ".name", font.getFamily());
        store.set(property + ".style", font.getStyle());
        titleFontLabel.setText(font.getFamily() + " / " + font.getSize());
        LOG.info("Selected Title Font : " + font);
      }
    });
    parent.add(fontChooserButton);
    parent.add(new JLabel(""), "wrap");
  }

  public static void createSlider(JPanel parent, String title, PropertiesStore store, String property) {
    parent.add(new JLabel(title));
    int value = store.getInt(property);
    if (value < 0) {
      value = 0;
    }
    JSlider slider = new JSlider(0, 100, value);
    slider.setBackground(ConfigWindow.DEFAULT_BG_COLOR);
    slider.setMajorTickSpacing(50);
    slider.setMinorTickSpacing(1);
    slider.setPaintLabels(true);
    parent.add(slider, "span 3");
    parent.add(new JLabel(""), "wrap");
    slider.addChangeListener(e -> {
      int s = slider.getValue();
      store.set(property, s);
    });
  }

  public static void createColorChooser(JFrame frame, JPanel parent, String label, PropertiesStore store, String property) {
    String value = store.getString(property, "#ffffff");
    parent.add(new JLabel(label));
    JButton open = new JButton("Select Color");
    JLabel valueLabel = new JLabel(value);
    open.addActionListener(e -> {
      new ColorDialog(frame, valueLabel, store, property);
    });
    parent.add(valueLabel, "span 3");
    parent.add(open, "span 2");
    parent.add(new JLabel(""), "wrap");
  }
}
