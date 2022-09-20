package de.mephisto.vpin.extensions;

import de.mephisto.vpin.GameInfo;
import de.mephisto.vpin.VPinService;
import de.mephisto.vpin.extensions.cardsettings.CardSettingsTabActionListener;
import de.mephisto.vpin.extensions.resources.ResourceLoader;
import de.mephisto.vpin.extensions.table.TableScanProgressModel;
import de.mephisto.vpin.extensions.util.ProgressDialog;
import de.mephisto.vpin.extensions.util.ProgressResultModel;
import de.mephisto.vpin.extensions.util.Updater;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static de.mephisto.vpin.extensions.ConfigWindow.setUIFont;

class Splash extends JWindow {
  private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(Splash.class);

  static JProgressBar progressBar = new JProgressBar();
  private VPinService vPinService;

  public Splash() {
    try {
      Container container = getContentPane();
      setUIFont(new javax.swing.plaf.FontUIResource("Tahoma", Font.PLAIN, 14));
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

      setIconImage(ResourceLoader.getResource("logo.png"));

      this.setSize(new Dimension(700, 450));

      Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
      int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
      int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
      setLocation(x, y);

      JPanel panel = new SplashCenter();
      panel.setLayout(null);

      container.add(panel, BorderLayout.CENTER);

      JLabel label = new JLabel("VPin Extensions");
      label.setBounds(30, 50, 600, 200);
      Font font = new Font("Tahoma", Font.PLAIN, 34);
      label.setFont(font);
      label.setForeground(Color.WHITE);
      panel.add(label);

      label = new JLabel("Version "+ Updater.getCurrentVersion());
      label.setBounds(30, 80, 600, 200);
      font = new Font("Tahoma", Font.PLAIN, 14);
      label.setFont(font);
      label.setForeground(Color.WHITE);
      panel.add(label);

      progressBar.setIndeterminate(true);
      progressBar.setMaximum(100);
      container.add(progressBar, BorderLayout.SOUTH);
      setVisible(true);

      new Thread(() -> {
        checkForUpdates();

        vPinService = VPinService.create(false);
        runInitialCheck();
        startMain();
      }).start();
    } catch (Exception e) {
      LOG.error("Failed to launch: " + e.getMessage(), e);
      JOptionPane.showMessageDialog(this.getContentPane(), "Failed to start VPin Extension configuration window: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      System.exit(0);
    }
  }



  private void runInitialCheck() {
    List<GameInfo> gameInfos = vPinService.getGameInfos();
    boolean romFound = false;
    for (GameInfo gameInfo : gameInfos) {
      if (!StringUtils.isEmpty(gameInfo.getRom())) {
        romFound = true;
        break;
      }
    }
    if (!romFound) {
      Splash.this.setVisible(false);
      int option = JOptionPane.showConfirmDialog(this, "It seems that no ROM scan has been performed yet.\n" +
          "The ROM name of each table is required in order to scan the highscore information.\n\nScan for ROM names? (This may take a while)", "Table Scan", JOptionPane.YES_NO_OPTION);
      if(option == JOptionPane.YES_OPTION) {
        ProgressDialog d = new ProgressDialog(null, new TableScanProgressModel(vPinService, "Resolving ROM Names"));
        ProgressResultModel progressResultModel = d.showDialog();

        JOptionPane.showMessageDialog(this, "Finished ROM scan, found ROM names of "
                + (progressResultModel.getProcessed()-progressResultModel.getSkipped()) + " from " + progressResultModel.getProcessed() + " tables.",
            "Generation Finished", JOptionPane.INFORMATION_MESSAGE);
        LOG.info("Finished global ROM scan.");
        Splash.this.setVisible(true);
        vPinService.refreshGameInfos();
      }
    }
  }

  private void checkForUpdates() {
    try {
      String nextVersion = Updater.checkForUpdate();
      if(!StringUtils.isEmpty(nextVersion)) {
        Splash.this.setVisible(false);
        int option = JOptionPane.showConfirmDialog(this, "New version " + nextVersion + " found. Download and install update?", "New Update Found", JOptionPane.YES_NO_OPTION);
        if(option == JOptionPane.YES_OPTION) {
          try {
            Updater.update(nextVersion);
            JOptionPane.showMessageDialog(null, "Update downloaded successfully. Please restart application.", "Information", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
          } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Update Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          }
        }
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, "Error checking for updates: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void startMain() {
    ConfigWindow configWindow = new ConfigWindow(vPinService);
    Splash.this.setVisible(false);
    configWindow.setVisible(true);
  }
}