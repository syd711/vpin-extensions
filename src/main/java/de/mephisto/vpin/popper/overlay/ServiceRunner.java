package de.mephisto.vpin.popper.overlay;

import de.mephisto.vpin.GameInfo;
import de.mephisto.vpin.VPinService;
import de.mephisto.vpin.popper.TableStatusChangeListener;
import de.mephisto.vpin.popper.TableStatusChangedEvent;
import de.mephisto.vpin.popper.overlay.generator.HighscoreCardGenerator;
import de.mephisto.vpin.popper.overlay.util.Config;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceRunner implements TableStatusChangeListener {
  private final static Logger LOG = LoggerFactory.getLogger(ServiceRunner.class);

  public ServiceRunner() {
    VPinService service = VPinService.create(true);
    LOG.info("ServiceRunner started.");

    String targetScreen = Config.getCardGeneratorConfig().get("popper.screen");
    if(!StringUtils.isEmpty(targetScreen)) {
      LOG.info("Added VPin service listener for highscore changes.");
      service.addTableStatusChangeListener(this);
    }
    else {
      LOG.info("Skipped starting highscore card generator, no PinUP popper target screen configured.");
    }

    LOG.info("Overlay window listener started.");
    OverlayWindowFX.launch(OverlayWindowFX.class);
  }

  @Override
  public void tableLaunched(TableStatusChangedEvent tableStatusChangedEvent) {

  }

  @Override
  public void tableExited(TableStatusChangedEvent tableStatusChangedEvent) {
    try {
      GameInfo gameInfo = tableStatusChangedEvent.getGameInfo();
      LOG.info("Executing highscore card generation for '" + gameInfo + "'");
      HighscoreCardGenerator.generateCard(gameInfo);
    } catch (Exception e) {
      LOG.error("Failed to generate highscore card for " + tableStatusChangedEvent.getGameInfo() + ": " + e.getMessage(), e);
    }
  }
}
