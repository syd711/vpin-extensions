package de.mephisto.vpin.extensions.table;

import de.mephisto.vpin.GameInfo;
import de.mephisto.vpin.VPinService;
import de.mephisto.vpin.VPinServiceException;
import de.mephisto.vpin.extensions.ConfigWindow;
import de.mephisto.vpin.extensions.util.ProgressDialog;
import de.mephisto.vpin.extensions.util.ProgressResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class TablesTabActionListener implements ActionListener {
  private final static Logger LOG = LoggerFactory.getLogger(TablesTabActionListener.class);

  private final ConfigWindow configWindow;
  private final VPinService service;
  private final TablesTab tablesTab;

  public TablesTabActionListener(ConfigWindow configWindow, VPinService service, TablesTab tablesTab) {
    this.configWindow = configWindow;
    this.service = service;
    this.tablesTab = tablesTab;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("tableRescan")) {
      GameInfo selection = tablesTab.getGamesTable().getSelection();
      if (selection != null) {
        int option = JOptionPane.showConfirmDialog(this.configWindow, "Re-scan table '" + selection.getGameDisplayName() + "' for it's ROM name?", "Information", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.OK_OPTION) {
          tablesTab.scanButton.setEnabled(false);
          String rom = service.rescanRom(selection);
          if (rom == null) {
            JOptionPane.showMessageDialog(configWindow, "Finished table ROM scan, no ROM name could be resolved for '" + selection.getGameDisplayName() + "'.\n\n" +
                    "This means this table will be ignored for the overall highscore and highscore card generation.",
                "Scan Finished", JOptionPane.INFORMATION_MESSAGE);
          }
          else {
            JOptionPane.showMessageDialog(configWindow, "Finished table ROM scan, resolved ROM name '" + rom + "'.",
                "Scan Finished", JOptionPane.WARNING_MESSAGE);
          }
          service.refreshGameInfos();
          tablesTab.gameTableModel.fireTableDataChanged();
          tablesTab.scanButton.setEnabled(false);
          tablesTab.highscoreButton.setEnabled(false);
          tablesTab.showDirectB2SButton.setEnabled(false);
        }
      }
    }
    else if (e.getActionCommand().equals("rescanAll")) {
      int input = JOptionPane.showConfirmDialog(ConfigWindow.getInstance(),
          "Re-scan all tables for their ROM names? (This may take a while.)", "Table Scan", JOptionPane.OK_CANCEL_OPTION);
      if (input == JOptionPane.OK_OPTION) {
        scanAll();
      }
    }
    else if (e.getActionCommand().equals("showDirectB2S")) {
      GameInfo selection = tablesTab.getGamesTable().getSelection();
      if (selection != null) {
        try {
          File directB2SFileTemp = service.extractDirectB2SBackgroundImage(selection);
          if (directB2SFileTemp != null && directB2SFileTemp.exists()) {
            Desktop.getDesktop().open(directB2SFileTemp);
          }
        } catch (VPinServiceException ex) {
          JOptionPane.showMessageDialog(configWindow, ex.getMessage(), "Image Extraction Failed", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(configWindow, ex.getMessage(), "Cannot open image: " + ex.getMessage(), JOptionPane.WARNING_MESSAGE);
        }
      }
    }
    else if (e.getActionCommand().equals("tableHighscore")) {
      List<GameInfo> gameInfos = service.getGameInfos();
      GamesTable gamesTable = tablesTab.getGamesTable();
      int selectedRow = gamesTable.getSelectedRow();
      GameInfo gameInfo = gameInfos.get(selectedRow);
      if (gameInfo.resolveHighscore() != null) {
        new HighscoreDialog(this.tablesTab.configWindow, gameInfo, "Highscore for " + gameInfo.getGameDisplayName());
      }
    }
  }

  public void scanAll() {
    service.refreshGameInfos();
    tablesTab.scanAllButton.setEnabled(false);
    ProgressDialog d = new ProgressDialog(configWindow, new TableScanProgressModel(service, "Resolving ROM Names"));
    ProgressResultModel progressResultModel = d.showDialog();

    JOptionPane.showMessageDialog(configWindow, "Finished ROM scan, found ROM names of "
            + (progressResultModel.getProcessed() - progressResultModel.getSkipped()) + " from " + progressResultModel.getProcessed() + " tables.",
        "Generation Finished", JOptionPane.INFORMATION_MESSAGE);
    LOG.info("Finished global ROM scan.");
    service.refreshGameInfos();
    tablesTab.gameTableModel.fireTableDataChanged();
    tablesTab.scanAllButton.setEnabled(true);
  }
}
