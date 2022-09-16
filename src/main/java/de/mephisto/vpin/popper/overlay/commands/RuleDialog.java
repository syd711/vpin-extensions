package de.mephisto.vpin.popper.overlay.commands;

import de.mephisto.vpin.VPinService;
import de.mephisto.vpin.dof.DOFCommand;
import de.mephisto.vpin.dof.Trigger;
import de.mephisto.vpin.dof.Unit;
import de.mephisto.vpin.popper.overlay.ConfigWindow;
import de.mephisto.vpin.popper.overlay.util.Keys;
import de.mephisto.vpin.popper.overlay.util.WidgetFactory;
import de.mephisto.vpin.util.PropertiesStore;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Vector;

public class RuleDialog extends JDialog {

  private static final java.util.List<String> OUTPUTS = new ArrayList<>();
  private static final java.util.List<String> VALUES = new ArrayList<>();

  static {
    for (int i = 1; i < 129; i++) {
      OUTPUTS.add(String.valueOf(i));
    }
    for (int i = 0; i < 256; i++) {
      VALUES.add(String.valueOf(i));
    }
  }

  private final VPinService service;
  private final JPanel keySelectionPanel;
  private final JSpinner timeSpinner;
  private final JCheckBox toggleBtnCheckbox;
  private final JComboBox triggerCombo;

  private JComboBox keyCombo;
  private JComboBox modifierCombo;

  private final CommandPropertiesStore store;

  private DOFCommand dofCommand;

  private int result = 0;

  public RuleDialog(ConfigWindow configWindow, VPinService service, DOFCommand dofCommand) {
    super(configWindow);
    this.service = service;
    this.store = new CommandPropertiesStore(dofCommand);
    this.dofCommand = dofCommand;

    this.setBackground(ConfigWindow.DEFAULT_BG_COLOR);
    this.setLayout(new BorderLayout());
    this.setModal(true);
    this.setSize(500, 360);
    this.setTitle("DOF Rule");
    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
    int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
    setLocation(x, y);


    JPanel rootPanel = new JPanel();
    rootPanel.setBackground(ConfigWindow.DEFAULT_BG_COLOR);
    rootPanel.setLayout(new MigLayout("gap rel 8 insets 10", "left", "center"));
    this.add(rootPanel, BorderLayout.CENTER);


    String key = "command." + dofCommand.getId();

    WidgetFactory.createTextField(rootPanel, "Description:", store, key + ".description", "");

    addBoardCombo(rootPanel, key + ".board", store);

    WidgetFactory.createCombobox(rootPanel, OUTPUTS, "set output number", store, key + ".output");

    WidgetFactory.createCombobox(rootPanel, VALUES, "to value", store, key + ".value");

    timeSpinner = WidgetFactory.createSpinner(rootPanel, "for", "ms", store, key + ".duration", 0);

    triggerCombo = addTriggerCombo(rootPanel, key + ".trigger", store);
    triggerCombo.addActionListener(e -> this.updateViewState());

    toggleBtnCheckbox = WidgetFactory.createCheckbox(rootPanel, "Toggle Button (keep value until pressed again)", store, key + ".toggle");
    toggleBtnCheckbox.addActionListener(e -> this.updateViewState());

    keySelectionPanel = addKeySelection(rootPanel, key + ".keyBinding", store);

    this.updateViewState();

    JToolBar tb = new JToolBar();
    tb.setLayout(new FlowLayout(FlowLayout.RIGHT));
    tb.setBorder(BorderFactory.createEmptyBorder(4, 4, 8, 8));
    tb.setFloatable(false);
    JButton save = new JButton("Save");
    save.setMinimumSize(new Dimension(60, 30));
    save.addActionListener(e -> {
      store.save();
      result = 1;
      setVisible(false);
    });
    tb.add(save);
    JButton close = new JButton("Cancel");
    close.setMinimumSize(new Dimension(60, 30));
    close.addActionListener(e -> {
      result = 0;
      setVisible(false);
    });
    tb.add(close);


    this.add(tb, BorderLayout.SOUTH);
  }

  public int showDialog() {
    this.setVisible(true);
    return result;
  }

  private void updateViewState() {
    Trigger selectedItem = (Trigger) triggerCombo.getSelectedItem();
    boolean keyTrigger = selectedItem.equals(Trigger.KeyEvent);

    keySelectionPanel.setVisible(keyTrigger);
    toggleBtnCheckbox.setVisible(keyTrigger);
    timeSpinner.setEnabled(!keyTrigger);
    if (!timeSpinner.isEnabled()) {
      timeSpinner.setValue(0);
    }

    if(!keyTrigger) {
      this.keyCombo.setSelectedIndex(0);
      this.modifierCombo.setSelectedIndex(0);
    }
  }

  private JComboBox addTriggerCombo(JPanel rootPanel, String key, PropertiesStore store) {
    Vector<Trigger> data = new Vector<>(Arrays.asList(Trigger.values()));
    final JComboBox triggerTypeSelector = new JComboBox(data);
    String selection = store.getString(key);
    if (!StringUtils.isEmpty(selection)) {
      triggerTypeSelector.setSelectedItem(Trigger.valueOf(selection));
    }
    triggerTypeSelector.addActionListener(e -> {
      Trigger selectedItem = (Trigger) triggerTypeSelector.getSelectedItem();
      String value = "";
      if (selectedItem != null) {
        value = String.valueOf(selectedItem);
      }
      store.set(key, value);
      updateViewState();
    });
    rootPanel.add(new JLabel("when"));
    rootPanel.add(triggerTypeSelector, "span 3");
    rootPanel.add(new JLabel(""));
    rootPanel.add(new JLabel(""), "wrap");
    return triggerTypeSelector;
  }

  private JPanel addKeySelection(JPanel rootPanel, String pKey, PropertiesStore store) {
    JPanel panel = new JPanel();
    panel.setBackground(ConfigWindow.DEFAULT_BG_COLOR);
    Vector<String> modifierNames = new Vector<>(Keys.getModifierNames());
    modifierNames.insertElementAt(null, 0);
    modifierCombo = new JComboBox(new DefaultComboBoxModel(modifierNames));
    modifierCombo.setActionCommand("modifierCombo");
    modifierCombo.addActionListener(e -> saveOverlayKeyBinding());

    Vector keys = new Vector(Keys.getKeyNames());
    keys.insertElementAt("", 0);
    keyCombo = new JComboBox(new DefaultComboBoxModel(keys));
    keyCombo.setActionCommand("keyCombo");
    keyCombo.addActionListener(e -> saveOverlayKeyBinding());


    String hotkey = store.get(pKey);
    if (hotkey != null) {
      if (hotkey.contains("+")) {
        String[] split = hotkey.split("\\+");
        if(split.length > 1) {
          String key = split[1];
          modifierCombo.setSelectedItem(Keys.getModifierName(Integer.parseInt(split[0])));
          keyCombo.setSelectedItem(key.toUpperCase(Locale.ROOT));
        }
        else {
          modifierCombo.setSelectedItem(Keys.getModifierName(Integer.parseInt(split[0])));
        }
      }
      else {
        modifierCombo.setSelectedIndex(0);
        keyCombo.setSelectedItem(hotkey.toUpperCase(Locale.ROOT));
      }
    }

    panel.add(modifierCombo);
    panel.add(new JLabel("+"));
    panel.add(keyCombo);

    rootPanel.add(new JLabel(""));
    rootPanel.add(panel, "span 3");
    rootPanel.add(new JLabel(""));
    rootPanel.add(new JLabel(""), "wrap");

    saveOverlayKeyBinding();
    return panel;
  }


  private void saveOverlayKeyBinding() {
    String key = (String) keyCombo.getSelectedItem();
    String modifier = (String) modifierCombo.getSelectedItem();

    if (key.length() == 1) {
      key = key.toLowerCase();
    }

    if (modifier != null) {
      int modifierNum = Keys.getModifier(modifier);
      key = modifierNum + "+" + key;
    }

    this.store.set("command." + this.dofCommand.getId() + ".keyBinding", key);
  }

  private void addBoardCombo(JPanel rootPanel, String key, PropertiesStore store) {
    String property = key + ".trigger";
    Vector<Unit> data = new Vector<>(service.getUnits());
    final JComboBox boardSelector = new JComboBox(data);
    boardSelector.addActionListener(e -> {
      Unit selectedItem = (Unit) boardSelector.getSelectedItem();
      String value = "";
      if (selectedItem != null) {
        value = String.valueOf(selectedItem.getId());
      }
      store.set(property, value);
    });
    String selection = store.getString(property);
    if (!StringUtils.isEmpty(selection)) {
      boardSelector.setSelectedItem(selection);
    }
    rootPanel.add(new JLabel("From board"));
    rootPanel.add(boardSelector, "span 3");
    rootPanel.add(new JLabel(""));
    rootPanel.add(new JLabel(""), "wrap");
  }
}
