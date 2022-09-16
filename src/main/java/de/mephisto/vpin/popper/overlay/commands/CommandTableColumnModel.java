package de.mephisto.vpin.popper.overlay.commands;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

public class CommandTableColumnModel extends DefaultTableColumnModel {
  public CommandTableColumnModel() {
    TableColumn column = new TableColumn(0);
    column.setHeaderValue("ID");
    addColumn(column);
    column = new TableColumn(1);
    column.setHeaderValue("Description");
    addColumn(column);
    column = new TableColumn(2);
    column.setHeaderValue("Board");
    addColumn(column);
    column = new TableColumn(3);
    column.setHeaderValue("Port");
    addColumn(column);
    column = new TableColumn(4);
    column.setHeaderValue("Value");
    addColumn(column);
    column = new TableColumn(5);
    column.setHeaderValue("Duration (ms)");
    addColumn(column);
    column = new TableColumn(6);
    column.setHeaderValue("Trigger");
    addColumn(column);
    column = new TableColumn(7);
    column.setHeaderValue("Key Binding");
    addColumn(column);
    column = new TableColumn(8);
    column.setHeaderValue("Toggle Button");
    addColumn(column);
  }
}
