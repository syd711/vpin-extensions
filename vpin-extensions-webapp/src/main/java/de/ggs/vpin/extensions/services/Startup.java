package de.ggs.vpin.extensions.services;

import de.ggs.vpin.extensions.sqllite.SqliteConnector;
import de.ggs.vpin.extensions.util.FileUtils;
import de.ggs.vpin.extensions.util.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class Startup {
  private final static Logger LOG = LoggerFactory.getLogger(Startup.class);

  private final static String CURL_COMMAND_TABLE_START = "curl -X POST --data-urlencode \"table=[GAMEFULLNAME]\" http://localhost/pinpup/tablestart";
  private final static String CURL_COMMAND_TABLE_EXIT = "curl -X POST --data-urlencode \"table=[GAMEFULLNAME]\" http://localhost/pinpup/tableexit";

  public static final String EMU_VISUAL_PINBALL_X = "Visual Pinball X";
  public static final String CURL_PLUGIN_DLL_NAME = "B2SCurlPluginVBNet.dll";
  public static final String CURL_PLUGIN_DLL_FOLDER_NAME = "B2SCurlPlugin";

  @Autowired
  private SqliteConnector sqliteConnector;

  @Autowired
  private SystemInfo systemInfo;

  @Autowired
  private TableInfoService tableInfoService;


  public void start() {
    LOG.info("*******************************************************************************************************");
    LOG.info("************************* Startup Checks **************************************************************");
    LOG.info("*******************************************************************************************************");
    File vpxInstallationFolder = systemInfo.getVPXInstallationFolder();
    LOG.info("Resolved VPX installation folder: " + vpxInstallationFolder.getAbsolutePath());
    File popperInstallationFolder = systemInfo.getPopperInstallationFolder();
    LOG.info("Resolved PinUP Popper installation folder: " + popperInstallationFolder.getAbsolutePath());

    //install table start curl
    String startupScript = sqliteConnector.getEmulatorStartupScript(EMU_VISUAL_PINBALL_X);
    if (!startupScript.contains(CURL_COMMAND_TABLE_START)) {
      LOG.info(SqliteConnector.LAUNCH_SCRIPT + " update not installed...");
      String update = startupScript + "\n\n" + CURL_COMMAND_TABLE_START;
      sqliteConnector.updateScript(EMU_VISUAL_PINBALL_X, SqliteConnector.LAUNCH_SCRIPT, update);
    }
    else {
      LOG.info(SqliteConnector.LAUNCH_SCRIPT + " is installed");
    }

    //install table end curl
    String exitScript = sqliteConnector.getEmulatorExitScript(EMU_VISUAL_PINBALL_X);
    if (!exitScript.contains(CURL_COMMAND_TABLE_EXIT)) {
      LOG.info(SqliteConnector.POST_SCRIPT + " update not installed...");
      String update = exitScript + "\n" + CURL_COMMAND_TABLE_EXIT;
      sqliteConnector.updateScript(EMU_VISUAL_PINBALL_X, SqliteConnector.POST_SCRIPT, update);
    }
    else {
      LOG.info(SqliteConnector.POST_SCRIPT + " is installed");
    }

    //install dll plugin
    File pluginTargetFile = new File(new File(systemInfo.getB2SPluginFolder(), CURL_PLUGIN_DLL_FOLDER_NAME), CURL_PLUGIN_DLL_NAME);
    if (!pluginTargetFile.exists()) {
      LOG.info(pluginTargetFile.getAbsolutePath() + " not installed, copying DLL.");
      FileUtils.copyResource(CURL_PLUGIN_DLL_NAME, pluginTargetFile);
    }
    else{
      LOG.info(pluginTargetFile.getAbsolutePath() + " is installed");
    }

    //scan for rom names to map table events
    tableInfoService.loadTableInfos();

    LOG.info("************************* Startup Check Finished ******************************************************");
  }
}
