package de.mephisto.vpin.extensions.cardsettings;

import de.mephisto.vpin.GameInfo;
import de.mephisto.vpin.VPinService;
import de.mephisto.vpin.extensions.util.ProgressModel;
import de.mephisto.vpin.extensions.util.ProgressResultModel;
import de.mephisto.vpin.popper.PopperScreen;
import de.mephisto.vpin.extensions.generator.CardGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

public class GeneratorProgressModel extends ProgressModel {
  private final static Logger LOG = LoggerFactory.getLogger(GeneratorProgressModel.class);
  private final Iterator<GameInfo> iterator;
  private final List<GameInfo> gameInfos;

  private VPinService service;
  private PopperScreen screen;

  public GeneratorProgressModel(VPinService service, PopperScreen screen, String title) {
    super(title);
    this.service = service;
    this.screen = screen;
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

  public String processNext(ProgressResultModel progressResultModel) {
    try {
      GameInfo gameInfo = iterator.next();
      if (gameInfo.resolveHighscore() != null) {
        CardGenerator.generateCard(service, gameInfo, screen);
      }
      else {
        progressResultModel.addSkipped();
      }

      progressResultModel.addProcessed();
      return gameInfo.getGameDisplayName();
    } catch (Exception e) {
      LOG.error("Generate card error: " + e.getMessage(), e);
    }
    return null;
  }
}
