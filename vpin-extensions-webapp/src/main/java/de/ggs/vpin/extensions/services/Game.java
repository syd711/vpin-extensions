package de.ggs.vpin.extensions.services;

import java.util.Date;

public class Game {

  private TableInfo tableInfo;
  private Date startTime;
  private Date endTime;

  private boolean initialized = false;

  public Game(TableInfo tableInfo) {
    this.tableInfo = tableInfo;
    this.startTime = new Date();
  }

  public TableInfo getTableInfo() {
    return tableInfo;
  }

  public boolean isInitialized() {
    return initialized;
  }

  public void initialize() {
    this.initialized = true;
  }

  public void exit() {
    this.endTime = new Date();
  }
}
