package de.mephisto.vpin.extensions;

import de.mephisto.vpin.VPinService;
import de.mephisto.vpin.extensions.resources.ResourceLoader;
import de.mephisto.vpin.extensions.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ServiceRunnerTray {
  private final static Logger LOG = LoggerFactory.getLogger(ServiceRunnerTray.class);

  public ServiceRunnerTray(VPinService service) {
    //Check the SystemTray is supported
    if (!SystemTray.isSupported()) {
      LOG.info("SystemTray is not supported");
      return;
    }
    final PopupMenu popup = new PopupMenu();
    final TrayIcon trayIcon = new TrayIcon(ResourceLoader.getResource("logo-small.png"));
    final SystemTray tray = SystemTray.getSystemTray();
    MenuItem restartItem = new MenuItem("Restart");
    restartItem.addActionListener(e -> {
      try {
        Config.reloadAll();
        restartItem.setEnabled(false);
        service.restart();
      } catch (Exception ex) {
        LOG.error("Failed to restart VPin Extension Service: " + ex.getMessage());
      }
      finally {
        restartItem.setEnabled(true);
      }
    });
    MenuItem logsItem = new MenuItem("Show Logs");
    logsItem.addActionListener(e -> {
      try {
        File file = new File("./vpin-extensions.log");
        if(file.exists()) {
          Desktop.getDesktop().open(file);
        }
      } catch (IOException ex) {
        LOG.error("Failed to open log file: " + ex.getMessage());
      }
    });
    MenuItem exitItem = new MenuItem("Terminate");
    exitItem.addActionListener(e -> System.exit(0));
    popup.add(restartItem);
    popup.add(logsItem);
    popup.addSeparator();
    popup.add(exitItem);

    trayIcon.setPopupMenu(popup);
    try {
      tray.add(trayIcon);
    } catch (AWTException e) {
      LOG.error("TrayIcon could not be added: " + e.getMessage(), e);
    }
  }
}
