package de.mephisto.vpin.extensions.table;

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
    column.setHeaderValue("ROM / Original");
    addColumn(column);
    column = new TableColumn(4);
    column.setHeaderValue("NVOffset");
    addColumn(column);
    column = new TableColumn(5);
    column.setHeaderValue("#Played");
    addColumn(column);
    column = new TableColumn(6);
    column.setHeaderValue("directb2s File");
    addColumn(column);
    column = new TableColumn(7);
    column.setHeaderValue("Status");
    addColumn(column);
  }
}
