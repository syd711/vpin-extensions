package de.mephisto.vpin.popper.overlay.commands;

import de.mephisto.vpin.VPinService;
import de.mephisto.vpin.dof.DOFCommand;
import de.mephisto.vpin.dof.Unit;
import de.mephisto.vpin.popper.overlay.util.Keys;
import org.apache.commons.lang3.StringUtils;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class CommandTableModel extends AbstractTableModel {

  private VPinService service;

  public CommandTableModel(VPinService service) {
    this.service = service;
  }

  @Override
  public int getRowCount() {
    return service.getDOFCommands().size();
  }

  @Override
  public int getColumnCount() {
    return 9;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    List<DOFCommand> dofCommands = service.getDOFCommands();
    DOFCommand command = dofCommands.get(rowIndex);
    if (columnIndex == 0) {
      return command.getId();
    }
    if (columnIndex == 1) {
      return command.getDescription();
    }
    if (columnIndex == 2) {
      Unit unit = service.getUnit(command.getUnit());
      if(unit == null) {
        return "UNKNOWN UNIT";
      }
      return unit.toString();
    }
    if (columnIndex == 3) {
      return command.getPortNumber();
    }
    if (columnIndex == 4) {
      return command.getValue();
    }
    if (columnIndex == 5) {
      return command.getDurationMs();
    }
    if (columnIndex == 6) {
      return command.getTrigger();
    }
    if (columnIndex == 7) {
      String keyBinding = command.getKeyBinding();
      if(!StringUtils.isEmpty(keyBinding)) {
        if (keyBinding.contains("+")) {
          String[] split = keyBinding.split("\\+");
          if(split.length > 1) {
            String key = split[1];
            String modifier = Keys.getModifierName(Integer.parseInt(split[0]));
            return modifier + " + " + key;
          }
          else {
            return Keys.getModifierName(Integer.parseInt(split[0]));
          }
        }
        return keyBinding;
      }
      return "";
    }
    if (columnIndex == 8) {
      return command.isToggle();
    }
    return "";
  }
}
