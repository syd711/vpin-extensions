package de.mephisto.vpin.popper.overlay.table;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

public class GameTableColumnModel extends DefaultTableColumnModel {
  public GameTableColumnModel() {
    TableColumn column = new TableColumn(0);
    column.setHeaderValue("ID");
    addColumn(column);
    column = new TableColumn(1);
    column.setHeaderValue("Table");
    addColumn(column);
    column = new TableColumn(2);
    column.setHeaderValue("Emulator");
    addColumn(column);
    column = new TableColumn(3);
    column.setHeaderValue("ROM");
    addColumn(column);
    column = new TableColumn(4);
    column.setHeaderValue("#Played");
    addColumn(column);
    column = new TableColumn(5);
    column.setHeaderValue("Status");
    addColumn(column);
  }
}
