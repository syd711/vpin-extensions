package de.mephisto.vpin.extensions.service;

import de.mephisto.vpin.StateManager;
import de.mephisto.vpin.VPinService;
import de.mephisto.vpin.extensions.ConfigWindow;
import de.mephisto.vpin.extensions.util.WidgetFactory;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServiceTab extends JPanel implements ActionListener {
  private final static Logger LOG = LoggerFactory.getLogger(ServiceTab.class);

  private final ConfigWindow configWindow;
  private final VPinService service;
  private final JButton startButton;
  private final JButton installButton;
  private final StateManager stateManager;


  public ServiceTab(ConfigWindow configWindow, VPinService service) {
    this.configWindow = configWindow;
    this.service = service;

    stateManager = new StateManager();

    this.setLayout(new BorderLayout());
    setBackground(ConfigWindow.DEFAULT_BG_COLOR);
    this.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

    JPanel settingsPanel = new JPanel();
    settingsPanel.setBackground(ConfigWindow.DEFAULT_BG_COLOR);
    settingsPanel.setLayout(new MigLayout("gap rel 8 insets 10", "left", "center"));
    this.add(settingsPanel, BorderLayout.CENTER);

    installButton = WidgetFactory.createConfigButton(settingsPanel, "install", "Install Autostart", "VPin Service Installation:", this);
    startButton = WidgetFactory.createConfigButton(settingsPanel, "start", "Start Test Service", "Service Test Instance:", this);

    this.updateStatus();
  }

  private void updateStatus() {
    installButton.setEnabled(true);
    startButton.setEnabled(true);

    if (stateManager.isInstalled()) {
      installButton.setText("Uninstall Autostart");
      installButton.setActionCommand("uninstall");
    }
    else {
      installButton.setText("Install Autostart");
      installButton.setActionCommand("install");
    }

    if (stateManager.isRunning()) {
      startButton.setText("Stop Test Service");
      startButton.setActionCommand("stop");
    }
    else {
      startButton.setText("Start Test Service");
      startButton.setActionCommand("start");
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    installButton.setEnabled(false);
    startButton.setEnabled(false);

    String cmd = e.getActionCommand();
    switch (cmd) {
      case "start": {
        try {
          boolean running = stateManager.isRunning();
          if(running) {
            stateManager.shutdown();
          }

          configWindow.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          stateManager.start();
          stateManager.waitForRunState();
          configWindow.setCursor(null);
          running = stateManager.isRunning();
          if (running) {
            JOptionPane.showMessageDialog(this, "Service start successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
          }
          else {
            JOptionPane.showMessageDialog(this, "Service start failed, check log for details.", "Error", JOptionPane.WARNING_MESSAGE);
          }
        } catch (Exception ex) {
          configWindow.setCursor(null);
          LOG.error("Failed start the VPin Service. " + ex.getMessage());
          JOptionPane.showMessageDialog(this, "Failed to start the VPin Service. " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        break;
      }
      case "stop": {
        configWindow.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        stateManager.shutdown();
        configWindow.setCursor(null);
        JOptionPane.showMessageDialog(this, "Service shutdown successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
        break;
      }
      case "install": {
        try {
          configWindow.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          stateManager.install();
          configWindow.setCursor(null);
          JOptionPane.showMessageDialog(this, "Installation successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
          configWindow.setCursor(null);
          LOG.error("Failed to install the VPin Service. " + ex.getMessage());
          JOptionPane.showMessageDialog(this, "Failed to install the VPin Service. " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        break;
      }
      case "uninstall": {
        try {
          configWindow.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          stateManager.uninstall();
          configWindow.setCursor(null);
          JOptionPane.showMessageDialog(this, "De-Installation successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
          configWindow.setCursor(null);
          LOG.error("Failed to uninstall the VPin Service: " + ex.getMessage());
          JOptionPane.showMessageDialog(this, "Failed to uninstall the VPin Service: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        break;
      }
    }

    this.updateStatus();
  }
}
