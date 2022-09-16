package de.mephisto.vpin.popper.overlay.table;

import de.mephisto.vpin.GameInfo;
import de.mephisto.vpin.VPinService;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class GamesTable extends JTable {

  private final TablesTab tablesTab;
  private final VPinService service;

  private GameInfo selection;

  public GamesTable(TablesTab overviewTab, VPinService service, GameTableModel tableModel, GameTableColumnModel columnModel) {
    super(tableModel, columnModel);


    this.tablesTab = overviewTab;
    this.service = service;

    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    setColumnSelectionAllowed(false);
    getSelectionModel().addListSelectionListener(this);
    setRowHeight(20);

    getColumnModel().getColumn(0).setPreferredWidth(20);
    getColumnModel().getColumn(1).setPreferredWidth(160);
    getColumnModel().getColumn(2).setPreferredWidth(40);
    getColumnModel().getColumn(3).setPreferredWidth(40);
    getColumnModel().getColumn(4).setPreferredWidth(30);
    getColumnModel().getColumn(5).setPreferredWidth(240);

    List<GameInfo> games = service.getGameInfos();
    setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        c.setBackground(Color.WHITE);
        if (isSelected) {
          c.setBackground(Color.BLUE);
          return c;
        }

        GameInfo game = games.get(row);
        if (StringUtils.isEmpty(game.getRom())) {
          c.setBackground(Color.decode("#FF9999"));
        }
        else if (!game.hasHighscore()) {
          c.setBackground(Color.decode("#FFCC33"));
        }

        return c;
      }
    });

//    TableRowSorter<GameTableModel> sorter = new TableRowSorter<>(tableModel);
//    sorter.setComparator(0, (Comparator<Integer>) (o1, o2) -> o1 - o2);
//    sorter.setComparator(1, (Comparator<String>) (o1, o2) -> o1.compareTo(o2));
//    sorter.setComparator(2, (Comparator<String>) (o1, o2) -> o1.compareTo(o2));
//    sorter.setComparator(3, (Comparator<String>) (o1, o2) -> o1.compareTo(o2));
//    sorter.setComparator(4, (Comparator<Integer>) (o1, o2) -> o1 - o2);
//    sorter.setComparator(5, (Comparator<String>) (o1, o2) -> o1.compareTo(o2));
//    setRowSorter(sorter);
  }

  public GameInfo getSelection() {
    return selection;
  }

  public void valueChanged(ListSelectionEvent e) {
    this.repaint();
    List<GameInfo> gameInfos = service.getGameInfos();
    int[] selectedRow = getSelectedRows();
    if (selectedRow.length > 0) {
      int row = selectedRow[0];
      this.selection = gameInfos.get(row);
      tablesTab.highscoreButton.setEnabled(selection != null);
      tablesTab.scanButton.setEnabled(selection != null);
      tablesTab.highscoreButton.setEnabled(selection != null && selection.hasHighscore());
    }
  }
}
