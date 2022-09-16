package de.mephisto.vpin.popper.overlay.commands;

import de.mephisto.vpin.VPinService;
import de.mephisto.vpin.dof.DOFCommand;
import de.mephisto.vpin.dof.Trigger;
import de.mephisto.vpin.popper.overlay.ConfigWindow;
import de.mephisto.vpin.popper.overlay.util.WidgetFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CommandsTab extends JPanel implements ActionListener {
  private final static Logger LOG = LoggerFactory.getLogger(CommandsTab.class);

  private final ConfigWindow configWindow;
  private final CommandTable commandTable;
  private final CommandTableModel commandTableModel;
  private final VPinService service;

  JButton editButton;
  JButton deleteButton;

  public CommandsTab(ConfigWindow configWindow, VPinService service) {
    this.configWindow = configWindow;
    this.service = service;

    this.setLayout(new BorderLayout());
    setBackground(ConfigWindow.DEFAULT_BG_COLOR);
    this.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

    JPanel toolBar = new JPanel();
    toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
    toolBar.setBackground(ConfigWindow.DEFAULT_BG_COLOR);
    toolBar.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 4));
    this.addButtons(toolBar);
    this.add(toolBar, BorderLayout.NORTH);

    commandTableModel = new CommandTableModel(service);
    commandTable = new CommandTable(this, service, commandTableModel, new CommandTableColumnModel());
    JScrollPane sp = new JScrollPane(commandTable);
    this.add(sp, BorderLayout.CENTER);
  }

  private void addButtons(JPanel toolBar) {
    WidgetFactory.createButton(toolBar, "createRule", "Create Rule", this);
    editButton = WidgetFactory.createButton(toolBar, "editRule", "Edit Rule", this);
    deleteButton = WidgetFactory.createButton(toolBar, "deleteRule", "Delete Rule", this);
    editButton.setEnabled(false);
    deleteButton.setEnabled(false);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    switch (cmd) {
      case "createRule":
        DOFCommand dofCommand = new DOFCommand(getNextId(), 1, 1, 0, 0, Trigger.TableStart, null, false, "");
        RuleDialog ruleDialog = new RuleDialog(configWindow, service, dofCommand);
        int result = ruleDialog.showDialog();
        if (result == 1) {
          service.addDOFCommand(dofCommand);
          commandTableModel.fireTableDataChanged();
        }

        break;
      case "editRule": {
        editRule();
        break;
      }
      case "deleteRule": {
        DOFCommand selection = commandTable.getSelection();
        int delete = JOptionPane.showConfirmDialog(configWindow, "Delete selected rule?", "Delete Rule2", JOptionPane.YES_NO_OPTION);
        if (delete == JOptionPane.YES_OPTION) {
          service.removeDOFCommand(selection);
          commandTableModel.fireTableDataChanged();
        }
        break;
      }
    }
  }

  void editRule() {
    DOFCommand selection = commandTable.getSelection();
    RuleDialog editDialog = new RuleDialog(configWindow, service, selection);
    int resultEdit = editDialog.showDialog();
    if (resultEdit == 1) {
      commandTableModel.fireTableDataChanged();
      service.updateDOFCommand(selection);
    }
  }

  private int getNextId() {
    int id = 0;
    List<DOFCommand> dofCommands = service.getDOFCommands();
    for (DOFCommand dofCommand : dofCommands) {
      if (dofCommand.getId() > id) {
        id = dofCommand.getId();
      }
    }
    id++;
    return id;
  }
}
