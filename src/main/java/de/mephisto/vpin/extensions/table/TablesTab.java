package de.mephisto.vpin.extensions.table;

import de.mephisto.vpin.VPinService;
import de.mephisto.vpin.extensions.ConfigWindow;
import de.mephisto.vpin.extensions.util.WidgetFactory;

import javax.swing.*;
import java.awt.*;

public class TablesTab extends JPanel {


  public TablesTabActionListener actionListener;

  final GameTableModel gameTableModel;
  GamesTable gamesTable;
  ConfigWindow configWindow;
  JButton highscoreButton;
  JButton scanButton;
  JButton scanAllButton;
  JButton showDirectB2SButton;

  public TablesTab(ConfigWindow configWindow, VPinService service) {
    super(new BorderLayout());
    this.configWindow = configWindow;

    setBackground(ConfigWindow.DEFAULT_BG_COLOR);
    this.actionListener = new TablesTabActionListener(configWindow, service, this);
    this.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

    JPanel toolBar = new JPanel();
    toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
    toolBar.setBackground(ConfigWindow.DEFAULT_BG_COLOR);
    toolBar.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 4));
    this.addButtons(toolBar);
    this.add(toolBar, BorderLayout.NORTH);

    gameTableModel = new GameTableModel(service);
    gamesTable = new GamesTable(this, service, gameTableModel, new GameTableColumnModel());
    JScrollPane sp = new JScrollPane(gamesTable);
    this.add(sp, BorderLayout.CENTER);
  }

  private void addButtons(JPanel toolBar) {
    scanAllButton = WidgetFactory.createButton(toolBar, "rescanAll", "Re-scan All Tables", this.actionListener);

    scanButton = WidgetFactory.createButton(toolBar, "tableRescan", "Re-scan Table", this.actionListener);
    scanButton.setEnabled(false);
    toolBar.add(scanButton);

    highscoreButton = WidgetFactory.createButton(toolBar, "tableHighscore", "Show Highscore", this.actionListener);
    highscoreButton.setEnabled(false);
    toolBar.add(highscoreButton);

    showDirectB2SButton = WidgetFactory.createButton(toolBar, "showDirectB2S", "Show DirectB2S Background", this.actionListener);
    showDirectB2SButton.setEnabled(false);
    toolBar.add(showDirectB2SButton);
  }

  public GamesTable getGamesTable() {
    return gamesTable;
  }
}
