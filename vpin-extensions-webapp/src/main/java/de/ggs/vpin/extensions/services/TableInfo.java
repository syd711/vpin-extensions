package de.ggs.vpin.extensions.services;

import de.ggs.vpin.extensions.util.PropertiesStore;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

public class TableInfo {

  private final File tableFile;
  private final String rom;

  public TableInfo(File tableFile, String rom) {
    this.tableFile = tableFile;
    this.rom = rom;
  }

  public int getCredits() {
    String credits = PropertiesStore.get(this.rom + ".credits");
    if(StringUtils.isEmpty(credits)) {
      return 2;
    }
    return Integer.parseInt(credits.trim());
  }

  public String getFilename() {
    return this.tableFile.getName();
  }
}
