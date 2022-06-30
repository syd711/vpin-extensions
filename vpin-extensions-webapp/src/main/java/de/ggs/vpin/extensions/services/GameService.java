package de.ggs.vpin.extensions.services;

import de.ggs.vpin.extensions.resources.B2SEvent;
import de.ggs.vpin.extensions.util.KeyUtil;
import de.ggs.vpin.extensions.util.Settings;
import de.ggs.vpin.extensions.util.SystemCommandExecutor;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.Queue;

@Service
public class GameService implements InitializingBean {
  private final static Logger LOG = LoggerFactory.getLogger(GameService.class);

  @Autowired
  private Startup startup;

  @Autowired
  private TableInfoService tableInfoService;

  private Game activeGame;

  private Queue<String> eventQueue;

  private String[] terminationSignals;
  private String terminationSignal;

  public Game getGame() {
    return activeGame;
  }

  public void initGame(String table) {
    File gameFile = new File(table);
    TableInfo tableInfo = tableInfoService.getTableInfo(gameFile);
    this.activeGame = new Game(tableInfo);
    this.terminationSignal = activeGame.getTableInfo().getTerminationSignal();
    this.terminationSignals = this.terminationSignal.split(",");
    this.eventQueue = new CircularFifoQueue<>(terminationSignals.length);
  }

  public void

  notifyB2SEvent(B2SEvent event) {
    if (!this.activeGame.isInitializeStarted()) {
      initTable(event);
    }

    if (!this.activeGame.isInitializedFinished() || this.activeGame.isTerminating()) {
      return;
    }

    this.activeGame.trackEvent(event);

    if (isTerminationSignal(event)) {
      this.activeGame.setTerminating(true);
      LOG.info("Registered termination signal '" + event + "', existing " + activeGame.getTableInfo().getFilename());
      if (Settings.get("debug.autoExit").equals("1")) {
        LOG.info("Auto exit is enabled, triggering exit call for " + activeGame.getTableInfo().getFilename());
        exitTable();
      }

      String dofCommand = Settings.get("doftest.parameters").trim();
      if (dofCommand.length() > 0) {
        LOG.info("Executing DOF Command Test: " + dofCommand);
        SystemCommandExecutor executor = new SystemCommandExecutor(Arrays.asList("./DOFTest/DirectOutputTest.exe", dofCommand), false);
        executor.executeCommandAsync();
      }
    }
  }

  private boolean isTerminationSignal(B2SEvent event) {
    if (!terminationSignal.contains(",") && terminationSignal.equals(event.toString())) {
      return true;
    }
    this.eventQueue.add(event.toString());

    String eventCSVList = String.join(",", this.eventQueue);
    return this.terminationSignal.equals(eventCSVList);
  }

  /**
   * Called by Pinup Popper exit script
   */
  public void exitGame() {
    if (this.activeGame != null) {
      if (Settings.get("debug.logEventStats").equals("1")) {
        this.activeGame.logEventStats();
      }
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
              KeyUtil.pressKey(Settings.get("button.insertCoin.key"), 80);
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
          KeyUtil.pressKey(Settings.get("button.start.key"), 1000);
          Thread.sleep(2000);
          activeGame.setInitializedFinished();
        } catch (InterruptedException e) {
          //ignore
        }
      }
    }.start();
  }

  private void exitTable() {
    new Thread() {
      public void run() {
        try {
          String exitDelay = Settings.get(activeGame.getTableInfo().getRom() + ".exit.delay.ms");
          if (exitDelay == null) {
            exitDelay = Settings.get("game.exit.delay.ms");
          }
          LOG.info("Waiting " + exitDelay + "ms before table exit of " + activeGame.getTableInfo());
          int delay = Integer.parseInt(exitDelay);
          Thread.sleep(delay);
          KeyUtil.pressKey(Settings.get("button.tableExit.key"), 500);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }.start();
  }

  @Override
  public void afterPropertiesSet() {
    startup.start();
  }
}
