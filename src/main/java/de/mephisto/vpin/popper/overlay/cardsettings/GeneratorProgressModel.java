package de.mephisto.vpin.popper.overlay.cardsettings;

import de.mephisto.vpin.GameInfo;
import de.mephisto.vpin.VPinService;
import de.mephisto.vpin.popper.PopperScreen;
import de.mephisto.vpin.popper.overlay.generator.HighscoreCardGenerator;
import de.mephisto.vpin.popper.overlay.util.ProgressModel;
import de.mephisto.vpin.popper.overlay.util.ProgressResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

public class GeneratorProgressModel extends ProgressModel {
  private final static Logger LOG = LoggerFactory.getLogger(GeneratorProgressModel.class);
  private final Iterator<GameInfo> iterator;
  private final List<GameInfo> gameInfos;

  private PopperScreen screen;

  public GeneratorProgressModel(VPinService service, PopperScreen screen, String title) {
    super(title);
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
        HighscoreCardGenerator.generateCard(gameInfo, screen);
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
