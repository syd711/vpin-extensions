package de.mephisto.vpin.extensions;

import de.mephisto.vpin.GameInfo;
import de.mephisto.vpin.VPinService;
import de.mephisto.vpin.VPinServiceException;
import de.mephisto.vpin.extensions.generator.CardGenerator;
import de.mephisto.vpin.extensions.generator.OverlayGenerator;
import de.mephisto.vpin.extensions.util.Config;
import de.mephisto.vpin.popper.PopperLaunchListener;
import de.mephisto.vpin.popper.TableStatusChangeListener;
import de.mephisto.vpin.popper.TableStatusChangedEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceRunner implements TableStatusChangeListener {
  private final static Logger LOG = LoggerFactory.getLogger(ServiceRunner.class);
  private VPinService service;

  public ServiceRunner() {
    try {
      service = VPinService.create(true);
      LOG.info("ServiceRunner started.");

      LOG.info("Added VPin service listener for highscore changes.");
      service.addTableStatusChangeListener(this);

      String targetScreen = Config.getCardGeneratorConfig().get("popper.screen");
      if (StringUtils.isEmpty(targetScreen)) {
        LOG.info("Skipped starting highscore card generator, no PinUP popper target screen configured.");
      }

      try {
        LOG.info("Executing highscore overlay generation.");
        OverlayGenerator.generateOverlay(service);
      } catch (Exception e) {
        LOG.error("Initial overlay generation failed: " + e.getMessage(), e);
      }

      new ServiceRunnerTray(service);

      LOG.info("Overlay window listener started.");
      OverlayWindowFX.launch(OverlayWindowFX.class);
    } catch (VPinServiceException e) {
      LOG.error("Failed to start service runner: " + e.getMessage(), e);
    }
  }

  @Override
  public void tableLaunched(TableStatusChangedEvent tableStatusChangedEvent) {

  }

  @Override
  public void tableExited(TableStatusChangedEvent tableStatusChangedEvent) {
    try {
      String targetScreen = Config.getCardGeneratorConfig().get("popper.screen");
      if (!StringUtils.isEmpty(targetScreen)) {
        GameInfo gameInfo = tableStatusChangedEvent.getGameInfo();
        LOG.info("Executing highscore card generation for '" + gameInfo + "'");
        CardGenerator.generateCard(service, gameInfo);
      }

      LOG.info("Executing highscore overlay generation.");
      OverlayGenerator.generateOverlay(service);
    } catch (Exception e) {
      LOG.error("Failed to generate highscore card for " + tableStatusChangedEvent.getGameInfo() + ": " + e.getMessage(), e);
    }
  }
}
