package de.ggs.vpin.extensions.services;

import de.ggs.vpin.extensions.resources.B2SEvent;
import de.ggs.vpin.extensions.util.KeyUtil;
import de.ggs.vpin.extensions.util.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.event.KeyEvent;
import java.io.File;

@Service
public class GameService implements InitializingBean {
  private final static Logger LOG = LoggerFactory.getLogger(GameService.class);

  @Autowired
  private Startup startup;

  @Autowired
  private TableInfoService tableInfoService;

  private Game activeGame;

  public Game getGame() {
    return activeGame;
  }

  public void initGame(String table) {
    File gameFile = new File(table);
    TableInfo tableInfo = tableInfoService.getTableInfo(gameFile);
    this.activeGame = new Game(tableInfo);
  }

  public void

  notifyB2SEvent(B2SEvent event) {
    if (!this.activeGame.isInitializeStarted()) {
      initTable(event);
    }

    if (!this.activeGame.isInitializedFinished()) {
      return;
    }

    this.activeGame.trackEvent(event);

    if (event.toString().equals(activeGame.getTableInfo().getTerminationSignal())) {
      exitTable();
    }
  }

  public void exitGame() {
    if (this.activeGame != null) {
      this.activeGame.logEventStats();
      this.activeGame.exit();
    }
  }

  private void initTable(B2SEvent event) {
    this.activeGame.setInitializedStarted();

    LOG.info("First b2s event captured (" + event + "), initializing table.");
    new Thread() {
      public void run() {
        try {
          String initDelay = Settings.get(activeGame.getTableInfo().getRom() + ".credits.delay.ms");
          if (initDelay == null) {
            initDelay = Settings.get("game.credits.delay.ms");
          }
          LOG.info("Waiting " + initDelay + "ms for credits input.");
          int delay = Integer.parseInt(initDelay);
          Thread.sleep(delay);

          int credits = activeGame.getTableInfo().getCredits();
          if (credits > 0) {
            for (int i = 0; i < credits; i++) {
              KeyUtil.pressKey(KeyEvent.VK_3, 80);
              Thread.sleep(1500);
            }

            String startDelay = Settings.get(activeGame.getTableInfo().getRom() + ".start.delay.ms");
            if (startDelay == null) {
              startDelay = Settings.get("game.start.delay.ms");
            }
            LOG.info("Waiting " + startDelay + "ms for start input.");
            int d = Integer.parseInt(startDelay);
            Thread.sleep(d);
          }
          KeyUtil.pressKey(KeyEvent.VK_1, 1000);
          Thread.sleep(2000);
          activeGame.setInitializedFinished();
        } catch (InterruptedException e) {
          //ignore
        }
      }
    }.start();
  }

  private void exitTable() {
    LOG.info("Registered termination signal, existing " + activeGame.getTableInfo().getFilename());
    new Thread() {
      public void run() {
        try {
          String exitDelay = Settings.get(activeGame.getTableInfo().getRom() + ".exit.delay.ms");
          if (exitDelay == null) {
            exitDelay = Settings.get("game.exit.delay.ms");
          }
          int delay = Integer.parseInt(exitDelay);
          Thread.sleep(delay);
          KeyUtil.pressKey(KeyEvent.VK_ESCAPE, 500);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }


      }
    }.start();
    KeyUtil.pressKey(KeyEvent.VK_ESCAPE, 500);
  }

  @Override
  public void afterPropertiesSet() {
    startup.start();
  }
}
