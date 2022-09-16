package de.mephisto.vpin.popper.overlay.table;

import de.mephisto.vpin.GameInfo;
import de.mephisto.vpin.VPinService;
import de.mephisto.vpin.popper.overlay.util.ProgressModel;
import de.mephisto.vpin.popper.overlay.util.ProgressResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

public class TableScanProgressModel extends ProgressModel {
  private final static Logger LOG = LoggerFactory.getLogger(TableScanProgressModel.class);

  private final List<GameInfo> gameInfos;
  private final Iterator<GameInfo> iterator;
  private final VPinService service;

  public TableScanProgressModel(VPinService service, String title) {
    super(title);
    this.service = service;
    gameInfos = service.getGameInfos();
    iterator = gameInfos.iterator();
  }

  @Override
  public int getMax() {
    return gameInfos.size();
  }

  @Override
  public Iterator getIterator() {
    return iterator;
  }

  @Override
  public String processNext(ProgressResultModel progressResultModel) {
    try {
      GameInfo gameInfo = iterator.next();
      String rom = service.rescanRom(gameInfo);
      if(rom == null) {
        progressResultModel.addSkipped();
      }
      progressResultModel.addProcessed();
      return gameInfo.getGameDisplayName();
    } catch (Exception e) {
      LOG.error("Error scanning ROM: " + e.getMessage(), e);
    }
    return null;
  }
}
