package de.ggs.vpin.extensions.services;

import de.ggs.vpin.extensions.sqllite.SqliteConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InstallationChecker implements InitializingBean {
  private final static Logger LOG = LoggerFactory.getLogger(InstallationChecker.class);

  private final static String CURL_COMMAND_TABLE_START = "curl -X POST --data-urlencode \"table=[GAMEFULLNAME]\" http://localhost/pinpup/tablestart";
  private final static String CURL_COMMAND_TABLE_EXIT = "curl -X POST --data-urlencode \"table=[GAMEFULLNAME]\" http://localhost/pinpup/tableexit";

  @Autowired
  private SqliteConnector sqliteConnector;

  @Override
  public void afterPropertiesSet() throws Exception {
    String startupScript = sqliteConnector.getEmulatorStartupScript("Visual Pinball X");
    if (!startupScript.contains(CURL_COMMAND_TABLE_START)) {
      LOG.warn("Table Start Command not installed");
    }

    String exitScript = sqliteConnector.getEmulatorExitScript("Visual Pinball X");
    if (!exitScript.contains(CURL_COMMAND_TABLE_EXIT)) {
      LOG.warn("Table Exit Command not installed");
    }
  }
}
