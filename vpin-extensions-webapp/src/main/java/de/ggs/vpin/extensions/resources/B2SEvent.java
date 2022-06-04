package de.ggs.vpin.extensions.resources;

public class B2SEvent {

  private final String type;
  private final int value;
  private final boolean enabled;

  B2SEvent(String type, int value, boolean enabled) {
    this.type = type;
    this.value = value;
    this.enabled = enabled;
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
