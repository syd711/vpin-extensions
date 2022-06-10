package de.ggs.vpin.extensions.services;

import de.ggs.vpin.extensions.resources.B2SEvent;
import de.ggs.vpin.extensions.resources.PinUpPlayerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Game {
  private final static Logger LOG = LoggerFactory.getLogger(Game.class);

  private TableInfo tableInfo;
  private Date startTime;
  private Date endTime;

  private Map<String, B2SEvent> eventMap;

  private boolean initializedStarted = false;
  private boolean initializedFinished = false;

  public Game(TableInfo tableInfo) {
    this.tableInfo = tableInfo;
    this.startTime = new Date();
    this.eventMap = new HashMap<>();
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

  public void exit() {
    this.endTime = new Date();
  }

  public void trackEvent(B2SEvent event) {
    String key = event.toString();
    if(eventMap.containsKey(key)) {
      B2SEvent existingEvent = eventMap.get(key);
      existingEvent.updateTracking(event);
    }
    else {
      eventMap.put(key, event);
    }
  }

  public void logEventStats() {
    LOG.info("************* Event Stats for " + this.tableInfo.getFilename() + " ************************************");
    List<B2SEvent> values = new ArrayList<>(eventMap.values());
    values.sort(Comparator.comparing(B2SEvent::getEventCount));
    for (B2SEvent value : values) {
      LOG.info(value.toString() + " [" + value.getCreationDate() + " -> " + value.getLastTriggerDate() + "]: " + value.getEventCount());
    }
  }

}