package de.ggs.vpin.extensions.services;

import de.ggs.vpin.extensions.util.Settings;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

public class TableInfo {

  private final File tableFile;
  private final String rom;
  private final String terminationSignal;

  public TableInfo(File tableFile, String rom, String terminationSignal) {
    this.tableFile = tableFile;
    this.rom = rom;
    this.terminationSignal = terminationSignal;
  }

  public String getRom() {
    return rom;
  }

  public String getTerminationSignal() {
    return terminationSignal;
  }

  public int getCredits() {
    String credits = Settings.get(this.rom + ".credits");
    if(StringUtils.isEmpty(credits)) {
      return 2;
    }
    return Integer.parseInt(credits.trim());
  }

  public String getFilename() {
    return this.tableFile.getName();
  }

  @Override
  public String toString() {
    return this.tableFile.getAbsolutePath();
  }
}
