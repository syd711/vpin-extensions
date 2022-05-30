package com.coremedia.corepin.web;

import com.coremedia.corepin.web.tables.TableInfo;

import java.util.Date;
import java.util.List;

public class PinballInfo {
  private String modificationDate;
  private List<TableInfo> tables;
  private TableInfo pinballOfTheMonth;

  public PinballInfo(List<TableInfo> tables) {
    this.tables = tables;
    this.modificationDate = new Date().toString();
  }

  public List<TableInfo> getTables() {
    return tables;
  }

  public void setTables(List<TableInfo> tables) {
    this.tables = tables;
  }

  public TableInfo getPinballOfTheMonth() {
    return pinballOfTheMonth;
  }

  public void setPinballOfTheMonth(TableInfo pinballOfTheMonth) {
    this.pinballOfTheMonth = pinballOfTheMonth;
  }

  public String getModificationDate() {
    return modificationDate;
  }

  public void setModificationDate(String modificationDate) {
    this.modificationDate = modificationDate;
  }
}
