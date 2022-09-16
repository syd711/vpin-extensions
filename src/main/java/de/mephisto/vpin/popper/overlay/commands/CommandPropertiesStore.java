package de.mephisto.vpin.popper.overlay.commands;

import de.mephisto.vpin.dof.DOFCommand;
import de.mephisto.vpin.dof.Trigger;
import de.mephisto.vpin.util.PropertiesStore;

public class CommandPropertiesStore extends PropertiesStore {

  private DOFCommand command;

  public CommandPropertiesStore(DOFCommand command) {
    this.command = command;
    set("command." + command.getId() + ".id", command.getId());
    set("command." + command.getId() + ".unit", command.getUnit());
    set("command." + command.getId() + ".output", command.getPortNumber());
    set("command." + command.getId() + ".value", command.getValue());
    set("command." + command.getId() + ".duration", command.getDurationMs());
    set("command." + command.getId() + ".trigger", command.getTrigger().name());
    set("command." + command.getId() + ".keyBinding", command.getKeyBinding());
    set("command." + command.getId() + ".toggle", String.valueOf(command.isToggle()));
    set("command." + command.getId() + ".description", command.getDescription());
  }

  @Override
  public void set(String key, String value) {
    if (value == null) {
      value = "";
    }
    properties.setProperty(key, value);
  }

  public void save() {
    command.setUnit(getInt("command." + command.getId() + ".unit"));
    command.setValue(getInt("command." + command.getId() + ".value"));
    command.setPortNumber(getInt("command." + command.getId() + ".output"));
    command.setDurationMs(getInt("command." + command.getId() + ".duration"));
    command.setTrigger(Trigger.valueOf(get("command." + command.getId() + ".trigger")));
    command.setKeyBinding(get("command." + command.getId() + ".keyBinding"));
    command.setDescription(get("command." + command.getId() + ".description"));
    command.setToggle(getBoolean("command." + command.getId() + ".toggle"));
  }
}
