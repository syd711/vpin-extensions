package com.coremedia.corepin.web.tables;

import com.coremedia.corepin.web.PinballProperties;
import com.coremedia.corepin.web.highscores.Highscore;
import com.coremedia.corepin.web.highscores.HighsoreResolver;
import com.coremedia.corepin.web.util.RomProperties;
import org.ini4j.Ini;
import org.ini4j.IniPreferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

@Service
public class TableResolver implements InitializingBean {
  private final static Logger LOG = LoggerFactory.getLogger(TableResolver.class);

  @Value("${visualpinball.folder}")
  private String visualPinballFolder;

  @Value("${pinemhi.command}")
  private String pinemhiCommand;

  @Value("${pinupsystem.wheelicons.folder}")
  private String wheelIconFolder;

  @Value("#{'${table.ignorelist}'.split(',')}")
  private List<String> tableIgnoreList;

  @Value("${server.home}")
  private String serverHome;

  @Autowired
  private HighsoreResolver highsoreResolver;

  private Preferences pinemhiTableNames;

  public List<TableInfo> getTables() {
    LOG.info("Scanning {}", visualPinballFolder);
    File pinballFolder = new File(visualPinballFolder);
    if(!pinballFolder.exists()) {
      LOG.error("Wrong pinball folder: " + visualPinballFolder);
      return Collections.emptyList();
    }

    File tablesFolder = new File(pinballFolder, "Tables");
    highsoreResolver.refreshVPReg();

    List<File> vpxTableNames = Arrays.asList(tablesFolder.listFiles((dir, name) -> name.endsWith(".vpx")));
    List<TableInfo> tables = new ArrayList<>();
    for (File vpxFile : vpxTableNames) {
      try {
        String tableName = vpxFile.getName().substring(0, vpxFile.getName().lastIndexOf("."));
        if (tableIgnoreList.contains(tableName)) {
          continue;
        }

        TableInfo tableInfo = new TableInfo(tableName);
        setRomName(tableInfo);

        if (tableInfo.getRomName() != null) {
          File wheelIcon = new File(wheelIconFolder, tableName + ".png");
          if (wheelIcon.exists()) {
            tableInfo.setWheelIconPath(wheelIcon.getAbsolutePath());
          } else {
            File defaultWheelIcon = new File(serverHome, "default.png");
            tableInfo.setWheelIconPath(defaultWheelIcon.getAbsolutePath());
            LOG.warn("No wheel icon found for table '{}', consider creating icon {}", tableName, wheelIcon.getAbsolutePath());
          }

          Highscore highscore = highsoreResolver.findHighscore(tableInfo);
          tableInfo.setHighscore(highscore);

          tables.add(tableInfo);
        }
      } catch (Exception e) {
        LOG.error("Failed to get all information on table {}: {}", vpxFile.getAbsolutePath(), e.getMessage());
      }
    }

    tables.sort(Comparator.comparing(TableInfo::getName));
    LOG.info("Scan returned {} tables", tables.size());
    return tables;
  }

  /**
   * Resolves the ROM name for the given table
   *
   * @param table
   * @throws BackingStoreException
   */
  private void setRomName(TableInfo table) throws BackingStoreException {
    String rom = findRomName(table);
    if (rom == null) {
      //some tables only store scores in the global user file, we have to configure the rom name manually for these!
      LOG.error("Did not find a matching rom name for table '{}', add the table to the roms.ini to find the matching nvram file or the highscore inside the VPReg.stg file. The table will be ignored.", table.getName());
      return;
    }

    table.setRomName(rom);
  }

  /**
   * We use the pinemhi.ini file and match the table name against the keys in this file.
   * May not be accurate.
   *
   * @param table
   * @return
   * @throws BackingStoreException
   */
  private String findRomName(TableInfo table) throws BackingStoreException {
    File pinballFolder = new File(visualPinballFolder);
    File mameFolder = new File(pinballFolder, PinballProperties.VPINMAME);
    File romFolder = new File(mameFolder, PinballProperties.ROMS);

    //first try to find the table name in the pinemhi mapping
    String name = table.getName();
    if (name.startsWith("The")) {
      name = name.replace("The", "").trim();
    }

    String[] keys = pinemhiTableNames.keys();
    //search all entries in the pinemhi.ini
    for (String key : keys) {
      if (key.contains(name) || key.toLowerCase().contains(name.toLowerCase())) {
        String nvRamName = pinemhiTableNames.get(key, null);
        String romName = nvRamName.substring(0, nvRamName.lastIndexOf("."));
        File romFile = new File(romFolder, romName + ".zip");
        if (romFile.exists()) {
          return romName;
        }
      }
    }

    //the rom name wasn't found in pinemhi, so look at the manual table
    return RomProperties.get(name);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    try {
      File commandFile = new File(serverHome + pinemhiCommand);
      File pinemhiIni = new File(commandFile.getParentFile(), "pinemhi.ini");

      Ini ini = new Ini(pinemhiIni);
      pinemhiTableNames = new IniPreferences(ini);
      pinemhiTableNames = pinemhiTableNames.node("romfind");
    } catch (IOException e) {
      LOG.error("Failed to init table resolver: {}", e.getMessage(), e);
    }
  }
}
