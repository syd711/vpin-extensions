package de.mephisto.vpin.extensions.commands;

import de.mephisto.vpin.VPinService;
import de.mephisto.vpin.dof.DOFCommandExecutor;
import de.mephisto.vpin.dof.DOFCommandResult;
import de.mephisto.vpin.dof.Unit;
import de.mephisto.vpin.extensions.ConfigWindow;
import de.mephisto.vpin.extensions.util.WidgetFactory;
import de.mephisto.vpin.util.PropertiesStore;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class DOFCommandTestDialog extends JDialog implements ActionListener {
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

  private final PropertiesStore store;
  private final JButton high;
  private final JButton low;
  private final JComboBox outputCombo;

  private int result = 0;
  private JComboBox<Unit> boardSelector;

  public DOFCommandTestDialog(ConfigWindow configWindow, VPinService service) {
    super(configWindow);
    this.service = service;
    this.store = PropertiesStore.createInMemory();

    this.setBackground(ConfigWindow.DEFAULT_BG_COLOR);
    this.setLayout(new BorderLayout());
    this.setModal(true);
    this.setSize(300, 260);
    this.setTitle("DOF Command Test");
    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
    int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
    setLocation(x, y);


    JPanel rootPanel = new JPanel();
    rootPanel.setBackground(ConfigWindow.DEFAULT_BG_COLOR);
    rootPanel.setLayout(new MigLayout("gap rel 8 insets 10", "left", "center"));
    this.add(rootPanel, BorderLayout.CENTER);

    addBoardCombo(rootPanel, "board", store);

    outputCombo = WidgetFactory.createCombobox(rootPanel, OUTPUTS, "Output:", store, "output");

    high = new JButton("HIGH");
    high.setActionCommand("high");
    high.addActionListener(this);
    low = new JButton("LOW");
    low.setActionCommand("low");
    low.addActionListener(this);
    rootPanel.add(new JLabel(""));
    rootPanel.add(high);
    rootPanel.add(low, "span 2");
    rootPanel.add(new JLabel(""), "wrap");

    JToolBar tb = new JToolBar();
    tb.setLayout(new FlowLayout(FlowLayout.RIGHT));
    tb.setBorder(BorderFactory.createEmptyBorder(4, 4, 8, 8));
    tb.setFloatable(false);
    JButton close = new JButton("Close");
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

  private void addBoardCombo(JPanel rootPanel, String key, PropertiesStore store) {
    Vector<Unit> data = new Vector<>(service.getUnits());
    boardSelector = new JComboBox<Unit>(data);
    boardSelector.addActionListener(e -> {
      Unit selectedItem = (Unit) boardSelector.getSelectedItem();
      String value = "";
      if (selectedItem != null) {
        value = String.valueOf(selectedItem.getId());
      }
      store.set(key, value);
    });
    String selection = store.getString(key);
    if (!StringUtils.isEmpty(selection)) {
      boardSelector.setSelectedItem(selection);
    }
    rootPanel.add(new JLabel("Board:"));
    rootPanel.add(boardSelector, "span 3");
    rootPanel.add(new JLabel(""));
    rootPanel.add(new JLabel(""), "wrap");
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String actionCommand = e.getActionCommand();
    switch (actionCommand) {
      case "low": {
        executeDOFCommand(0);
        break;
      }
      case "high": {
        executeDOFCommand(255);
        break;
      }
    }
  }

  private void executeDOFCommand(int value) {
    try {
      Unit unit = (Unit) boardSelector.getSelectedItem();
      String port = (String) outputCombo.getSelectedItem();
      if(unit != null) {
        List<String> commands = Arrays.asList(String.valueOf(unit.getId()), String.valueOf(port), String.valueOf(value));
        DOFCommandResult dofCommandResult = DOFCommandExecutor.executeDOFTester(commands);
        JOptionPane.showMessageDialog(this, "Command Result: " + dofCommandResult.getOut(), "Result", JOptionPane.INFORMATION_MESSAGE);
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Command Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
}
