package de.mephisto.vpin.extensions;

import de.mephisto.vpin.GameInfo;
import de.mephisto.vpin.VPinService;
import de.mephisto.vpin.extensions.cardsettings.CardSettingsTab;
import de.mephisto.vpin.extensions.commands.CommandsTab;
import de.mephisto.vpin.extensions.resources.ResourceLoader;
import de.mephisto.vpin.extensions.util.Updater;
import de.mephisto.vpin.extensions.util.ProgressDialog;
import de.mephisto.vpin.extensions.util.ProgressResultModel;
import de.mephisto.vpin.extensions.overlaysettings.OverlaySettingsTab;
import de.mephisto.vpin.extensions.table.TableScanProgressModel;
import de.mephisto.vpin.extensions.table.TablesTab;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

public class ConfigWindow extends JFrame {
  private final static Logger LOG = LoggerFactory.getLogger(ConfigWindow.class);

  private static ConfigWindow instance;
  private final VPinService service;

  public static final Color DEFAULT_BG_COLOR = Color.WHITE;// Color.decode("#EEEEEE");

  public static ConfigWindow getInstance() {
    return instance;
  }

  public ConfigWindow() throws Exception {
    ConfigWindow.instance = this;

    setUIFont(new javax.swing.plaf.FontUIResource("Tahoma", Font.PLAIN, 14));
//    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");


    service = VPinService.create(false);


    setSize(1346, 990);
    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
    int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
    setLocation(x, y);
    setResizable(false);

    // setting the title of Frame
    setTitle("VPin Extensions (" + Updater.getCurrentVersion() + ")");
    setIconImage(ResourceLoader.getResource("logo.png"));

    checkForUpdates();
    runInitialCheck();

    try {
      this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

      JTabbedPane tabbedPane = new JTabbedPane();
      tabbedPane.addTab("Highscore Overlay Settings", null, new OverlaySettingsTab(this, service), "Table Challenge, Key-Bindings, etc.");
      tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

      tabbedPane.addTab("Highscore Cards Settings", null, new CardSettingsTab(this, service), "Highscore Generation Settings");
      tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

      tabbedPane.addTab("DOF Event Rules (Experimental)", null, new CommandsTab(this, service), "Create custom DOF rules");
      tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

      TablesTab tablesTab = new TablesTab(this, service);
      tabbedPane.addTab("Table Overview", null, tablesTab, "Status of all tables");
      tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);


      tabbedPane.setBackground(DEFAULT_BG_COLOR);
      tabbedPane.setBackgroundAt(0, DEFAULT_BG_COLOR);
      tabbedPane.setBackgroundAt(1, DEFAULT_BG_COLOR);
      tabbedPane.setBackgroundAt(2, DEFAULT_BG_COLOR);
      tabbedPane.setBackgroundAt(3, DEFAULT_BG_COLOR);
      add(tabbedPane);

      setVisible(true);

      Action escapeAction = new AbstractAction() {
        private static final long serialVersionUID = 5572504000935312338L;

        @Override
        public void actionPerformed(ActionEvent e) {
          System.exit(0);
        }
      };

      this.getRootPane().getInputMap().put(KeyStroke.getKeyStroke("F2"),
          "pressed");
      this.getRootPane().getActionMap().put("pressed", escapeAction);
    } catch (Exception e) {
      LOG.error("Failed to create UI: " + e.getMessage(), e);
      JOptionPane.showMessageDialog(null, "Failed to create VPinExtension window: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void checkForUpdates() {
    try {
      String nextVersion = Updater.checkForUpdate();
      if(!StringUtils.isEmpty(nextVersion)) {
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

  private void runInitialCheck() {
    List<GameInfo> gameInfos = service.getGameInfos();
    boolean romFound = false;
    for (GameInfo gameInfo : gameInfos) {
      if (!StringUtils.isEmpty(gameInfo.getRom())) {
        romFound = true;
        break;
      }
    }
    if (!romFound) {
      int option = JOptionPane.showConfirmDialog(this, "It seems that no ROM scan has been performed yet.\n" +
          "The ROM name of each table is required in order to scan the highscore information.\n\nScan for ROM names? (This may take a while)", "Table Scan", JOptionPane.YES_NO_OPTION);
      if(option == JOptionPane.YES_OPTION) {
        ProgressDialog d = new ProgressDialog(this, new TableScanProgressModel(service, "Resolving ROM Names"));
        ProgressResultModel progressResultModel = d.showDialog();

        JOptionPane.showMessageDialog(this, "Finished ROM scan, found ROM names of "
                + (progressResultModel.getProcessed()-progressResultModel.getSkipped()) + " from " + progressResultModel.getProcessed() + " tables.",
            "Generation Finished", JOptionPane.INFORMATION_MESSAGE);
        LOG.info("Finished global ROM scan.");
        service.refreshGameInfos();
      }
    }
  }

  public static void setUIFont(javax.swing.plaf.FontUIResource f) {
    java.util.Enumeration keys = UIManager.getDefaults().keys();
    while (keys.hasMoreElements()) {
      Object key = keys.nextElement();
      Object value = UIManager.get(key);
      if (value instanceof javax.swing.plaf.FontUIResource)
        UIManager.put(key, f);
    }
  }

  public static void main(String[] args) throws Exception {
    new ConfigWindow();
  }
}
