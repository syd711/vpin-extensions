package de.ggs.vpin.extensions.resources;

import java.util.Date;

public class B2SEvent {

  private final String type;
  private final int value;
  private final boolean enabled;
  private Date creationDate;
  private Date lastTriggerDate;
  private int eventCount = 1;

  B2SEvent(String type, int value, boolean enabled) {
    this.type = type;
    this.value = value;
    this.enabled = enabled;
    this.creationDate = new Date();
    this.lastTriggerDate = new Date();
  }

  public int getEventCount() {
    return eventCount;
  }

  public void updateTracking(B2SEvent event) {
    this.eventCount++;
    this.lastTriggerDate = event.getCreationDate();
  }

  public Date getLastTriggerDate() {
    return lastTriggerDate;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public String getType() {
    return type;
  }

  public int getValue() {
    return value;
  }

  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public String toString() {
    return type + "-" + value + "-" + (enabled ? 1 : 0);
  }
}
