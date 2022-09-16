package de.mephisto.vpin.popper.overlay.overlaysettings;

import de.mephisto.vpin.VPinService;
import de.mephisto.vpin.popper.overlay.generator.OverlayGenerator;
import de.mephisto.vpin.popper.overlay.util.Config;
import de.mephisto.vpin.popper.overlay.util.Keys;
import de.mephisto.vpin.util.SystemInfo;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class OverlaySettingsTabActionListener implements ActionListener {
  private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(OverlaySettingsTabActionListener.class);

  private final OverlaySettingsTab overlaySettingsTab;
  private final VPinService service;

  public OverlaySettingsTabActionListener(OverlaySettingsTab overlaySettingsTab, VPinService service) {
    this.overlaySettingsTab = overlaySettingsTab;
    this.service = service;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if (cmd.equals("modifierCombo")) {
      this.saveOverlayKeyBinding();
    }
    else if (cmd.equals("keyCombo")) {
      this.saveOverlayKeyBinding();
    }
    else if (cmd.equals("generateOverlay")) {
      this.overlaySettingsTab.generateOverlay();
    }
    else if (cmd.equals("showOverlay")) {
      try {
        File file = OverlayGenerator.GENERATED_OVERLAY_FILE;
        if (!OverlayGenerator.GENERATED_OVERLAY_FILE.exists()) {
          file = new File(SystemInfo.RESOURCES, Config.getOverlayGeneratorConfig().get("overlay.background"));
        }
        Desktop.getDesktop().open(file);
      } catch (IOException ex) {
        LOG.error("Failed to open overlay file: " + ex.getMessage(), ex);
      }
    }
  }

  private void saveOverlayKeyBinding() {
    String key = (String) overlaySettingsTab.keyCombo.getSelectedItem();
    String modifier = (String) overlaySettingsTab.modifierCombo.getSelectedItem();

    if (key.length() == 1) {
      key = key.toLowerCase();
    }

    if (modifier != null) {
      int modifierNum = Keys.getModifier(modifier);
      key = modifierNum + "+" + key;
    }

    Config.getOverlayGeneratorConfig().set("overlay.hotkey", key);
  }

}
