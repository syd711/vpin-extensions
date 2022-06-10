package de.ggs.vpin.extensions.services;

import de.ggs.vpin.extensions.sqllite.SqliteConnector;
import de.ggs.vpin.extensions.util.Settings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class TableInfoService {
  private final static Logger LOG = LoggerFactory.getLogger(TableInfoService.class);

  @Autowired
  private SystemInfo systemInfo;

  @Autowired
  private SqliteConnector sqliteConnector;

  private Map<String,TableInfo> tableInfoByFilename;

  public TableInfo getTableInfo(File file) {
    return tableInfoByFilename.get(file.getName());
  }

  public void loadTableInfos() {
    tableInfoByFilename = new HashMap<>();

    LOG.info("*********************** Executing ROM Checks **********************************************************");
    File[] vpxTables = systemInfo.getVPXTables();
    File romFolder = systemInfo.getMameRomFolder();
    for (File vpxTable : vpxTables) {
      String romName = sqliteConnector.getRomName(vpxTable.getName());
      if(StringUtils.isAllEmpty(romName)) {
        LOG.info("No rom found in database, checking ROM name for " + vpxTable.getAbsolutePath());
        romName = extractRomName(vpxTable);
        if (romName != null && romName.length() > 0) {
          sqliteConnector.updateRomName(vpxTable.getName(), romName);
        }

        if (romName == null) {
          LOG.error("Failed to determine ROM name of " + vpxTable.getAbsolutePath());
          continue;
        }
      }

      File romFile = new File(romFolder, romName + ".zip");
      if(!romFile.exists()) {
        LOG.warn("No rom file '" + romFile.getAbsolutePath() + " found.");
      }


      LOG.info("Loaded ROM name for " + vpxTable.getAbsolutePath() + ": [" + romName+ "]");
      String terminationSignal = Settings.get(romName + ".terminationSignal");
      TableInfo tableInfo = new TableInfo(vpxTable, romName, terminationSignal);
      tableInfoByFilename.put(vpxTable.getName(), tableInfo);
    }
  }

  private String extractRomName(File vpxTable) {
    String romName = null;
    try {
      BufferedReader br = new BufferedReader(new FileReader(vpxTable));
      String line;
      while ((line = br.readLine()) != null) {
        if (line.contains("cGameName") && line.contains("=") && line.length() < 160) {
          romName = line.substring(line.indexOf("\"") + 1);
          romName = romName.substring(0, romName.indexOf("\""));
          break;
        }
      }
      br.close();
    } catch (Exception e) {


      LOG.error("Failed to read " + vpxTable.getAbsolutePath() + ": " + e.getMessage(), e);
    }
    return romName;
  }
}
