package de.ggs.vpin.extensions.services;

import de.ggs.vpin.extensions.resources.B2SEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Game {
  private final static Logger LOG = LoggerFactory.getLogger(Game.class);

  private TableInfo tableInfo;
  private Date startTime;
  private Date endTime;

  private Map<String, B2SEvent> eventMap;
  private List<B2SEvent> eventList;

  private boolean initializedStarted = false;
  private boolean initializedFinished = false;
  private boolean terminating = false;

  public Game(TableInfo tableInfo) {
    this.tableInfo = tableInfo;
    this.startTime = new Date();
    this.eventMap = new HashMap<>();
    this.eventList = new ArrayList<>();
  }

  public TableInfo getTableInfo() {
    return tableInfo;
  }

  public boolean isInitializeStarted() {
    return initializedStarted;
  }

  public boolean isInitializedFinished() {
    return initializedFinished;
  }

  public void setInitializedStarted() {
    this.initializedStarted = true;
  }

  public void setInitializedFinished() {
    this.initializedFinished = true;
  }

  public boolean isTerminating() {
    return terminating;
  }

  public void setTerminating(boolean terminating) {
    this.terminating = terminating;
  }

  public void exit() {
    this.endTime = new Date();
  }

  public void trackEvent(B2SEvent event) {
    String key = event.toString();
    if (eventMap.containsKey(key)) {
      B2SEvent existingEvent = eventMap.get(key);
      existingEvent.updateTracking(event);
    }
    else {
      eventMap.put(key, event);
    }
    eventList.add(event);
  }

  public void logEventStats() {
    LOG.info("************* Event Stats for " + this.tableInfo.getFilename() + " ************************************");
    List<B2SEvent> values = new ArrayList<>(eventMap.values());
    values.sort(Comparator.comparing(B2SEvent::getEventCount));
    for (B2SEvent value : values) {
      LOG.info(value.toString() + " [" + value.getCreationDate() + " -> " + value.getLastTriggerDate() + "]: " + value.getEventCount());
    }

    LOG.info("************ /Event Stats for " + this.tableInfo.getFilename() + " ************************************");
    try {
      File file = new File("./" + this.tableInfo.getRom() + ".log");
      file.deleteOnExit();
      if (file.exists()) {
        file.delete();
      }
      BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));

      for (B2SEvent event : this.eventList) {
        writer.append(event.getCreationDate().toString());
        writer.append(' ');
        writer.append(event.toString());
        writer.append("\n");
      }

      writer.close();
      LOG.info("Written table log " + file.getAbsolutePath());
    } catch (IOException e) {
      LOG.error("Failed to write event log file: " + e.getMessage(), e);
    }
  }
}
