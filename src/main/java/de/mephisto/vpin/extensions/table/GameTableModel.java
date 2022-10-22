package de.mephisto.vpin.extensions.table;

import de.mephisto.vpin.GameInfo;
import de.mephisto.vpin.VPinService;
import org.apache.commons.lang3.StringUtils;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class GameTableModel extends AbstractTableModel {

  private VPinService service;

  public GameTableModel(VPinService service) {
    this.service = service;
  }

  @Override
  public int getRowCount() {
    return service.getGameInfos().size();
  }

  @Override
  public int getColumnCount() {
    return 6;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    List<GameInfo> gameInfos = service.getGameInfos();
    GameInfo gameInfo = gameInfos.get(rowIndex);
    if (columnIndex == 0) {
      return gameInfo.getId();
    }
    if (columnIndex == 1) {
      return gameInfo.getGameDisplayName();
    }
    if (columnIndex == 2) {
      return gameInfo.getEmulatorName();
    }
    if (columnIndex == 3) {
      if(!StringUtils.isEmpty(gameInfo.getOriginalRom())) {
        return gameInfo.getRom() + " / " + gameInfo.getOriginalRom();
      }
      return gameInfo.getRom();
    }
    if (columnIndex == 4) {
      return "         " + gameInfo.getNvOffset();
    }
    if (columnIndex == 5) {
      return "         " + gameInfo.getNumberPlays();
    }
    if (columnIndex == 6) {
      if (gameInfo.getDirectB2SFile().exists()) {
        return "         X";
      }
      return "";
    }
    if (columnIndex == 7) {
      if (!StringUtils.isEmpty(gameInfo.getHsFileName())) {
        return gameInfo.getHsFileName();
      }
      return "";
    }
    if (columnIndex == 8) {
      if (StringUtils.isEmpty(gameInfo.getRom())) {
        return "No rom information found for table.";
      }
      else if (!gameInfo.hasHighscore()) {
        return "No highscore files found.";
      }
      return "";
    }

    return "";
  }
}
