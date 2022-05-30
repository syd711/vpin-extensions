package com.coremedia.corepin.web.tables;

import com.coremedia.corepin.web.highscores.Highscore;

public class TableInfo {
  private String name;
  private String romName;
  private Highscore highscore;
  private long lastModified;
  private String wheelIconPath;

  public TableInfo(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public String getRomName() {
    return romName;
  }

  public void setRomName(String romName) {
    this.romName = romName;
  }

  public Highscore getHighscore() {
    return highscore;
  }

  public void setHighscore(Highscore highscore) {
    this.highscore = highscore;
  }

  public long getLastModified() {
    return lastModified;
  }

  public void setLastModified(long lastModified) {
    this.lastModified = lastModified;
  }

  @Override
  public boolean equals(Object obj) {
    TableInfo that = (TableInfo) obj;
    return this.name.equals(that.name);
  }

  public String getWheelIconPath() {
    return wheelIconPath;
  }

  public void setWheelIconPath(String wheelIconPath) {
    this.wheelIconPath = wheelIconPath;
  }
}
