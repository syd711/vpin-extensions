package de.mephisto.vpin.popper.overlay.commands;

import de.mephisto.vpin.VPinService;
import de.mephisto.vpin.dof.DOFCommand;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class CommandTable extends JTable {

  private final CommandsTab commandsTab;
  private final VPinService service;

  private DOFCommand selection;

  public CommandTable(CommandsTab commandsTab, VPinService service, CommandTableModel tableModel, CommandTableColumnModel columnModel) {
    super(tableModel, columnModel);

    this.commandsTab = commandsTab;
    this.service = service;

    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    setColumnSelectionAllowed(false);
    getSelectionModel().addListSelectionListener(this);
    setRowHeight(20);
    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent mouseEvent) {
        JTable table =(JTable) mouseEvent.getSource();
        Point point = mouseEvent.getPoint();
        if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
          commandsTab.editRule();
        }
      }
    });

    getColumnModel().getColumn(0).setPreferredWidth(16);
    getColumnModel().getColumn(1).setPreferredWidth(160);
    getColumnModel().getColumn(1).setPreferredWidth(260);
    getColumnModel().getColumn(2).setPreferredWidth(40);
    getColumnModel().getColumn(3).setPreferredWidth(30);
    getColumnModel().getColumn(4).setPreferredWidth(40);
    getColumnModel().getColumn(5).setPreferredWidth(70);
    getColumnModel().getColumn(6).setPreferredWidth(140);
    getColumnModel().getColumn(7).setPreferredWidth(60);
    getColumnModel().getColumn(8).setPreferredWidth(50);

    setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        c.setBackground(Color.WHITE);
        if (isSelected) {
          c.setBackground(Color.BLUE);
        }
        else if (row % 2 == 0) {
          c.setBackground(Color.decode("#EEEEEE"));
        }
        return c;
      }
    });
  }

  public DOFCommand getSelection() {
    return selection;
  }

  public void valueChanged(ListSelectionEvent e) {
    repaint();
    List<DOFCommand> commandList = service.getDOFCommands();
    int[] selectedRow = getSelectedRows();
    if (selectedRow.length > 0) {
      int row = selectedRow[0];
      this.selection = commandList.get(row);
      commandsTab.editButton.setEnabled(selection != null);
      commandsTab.deleteButton.setEnabled(selection != null);
    }
  }
}
