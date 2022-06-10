package de.ggs.vpin.extensions.sqllite;

import de.ggs.vpin.extensions.services.SystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.sql.*;

@Component
public class SqliteConnector implements InitializingBean {
  private final static Logger LOG = LoggerFactory.getLogger(SqliteConnector.class);

  public static final String POST_SCRIPT = "PostScript";
  public static final String LAUNCH_SCRIPT = "LaunchScript";
  public static final String ROM = "ROM";

  private Connection conn;

  @Autowired
  private SystemInfo systemInfoService;

  @Override
  public void afterPropertiesSet() {
    File dbFile = new File(this.systemInfoService.getPopperInstallationFolder(), "PUPDatabase.db");
    String dbFilePath = dbFile.getAbsolutePath().replaceAll("\\\\", "/");
    this.connect(dbFilePath);
  }

  /**
   * Connect to a database
   */
  private void connect(String dbFilePath) {
    try {
      // db parameters
      String url = "jdbc:sqlite:" + dbFilePath;
      // create a connection to the database
      conn = DriverManager.getConnection(url);
      LOG.info("Connection to SQLite (" + dbFilePath + ") has been established.");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public String getEmulatorStartupScript(String emuName) {
    String script = null;
    try {
      Statement statement = conn.createStatement();
      ResultSet rs = statement.executeQuery("SELECT * FROM Emulators where EmuName = '" + emuName + "';");
      rs.next();
      script = rs.getString(LAUNCH_SCRIPT);
      rs.close();
      statement.close();
    } catch (SQLException e) {
      LOG.error("Failed to read startup script or " + emuName + ": " + e.getMessage(), e);
    }
    return script;
  }

  public String getEmulatorExitScript(String emuName) {
    String script = null;
    try {
      Statement statement = conn.createStatement();
      ResultSet rs = statement.executeQuery("SELECT * FROM Emulators where EmuName = '" + emuName + "';");
      rs.next();
      script = rs.getString(POST_SCRIPT);
      rs.close();
      statement.close();
    } catch (SQLException e) {
      LOG.error("Failed to read exit script or " + emuName + ": " + e.getMessage(), e);
    }
    return script;
  }

  public String getRomName(String tableFileName) {
    String rom = null;
    try {
      Statement statement = conn.createStatement();
      ResultSet rs = statement.executeQuery("SELECT * FROM Games where GameFileName = '" + tableFileName + "';");
      if (rs.next()) {
        rom = rs.getString(ROM);
      }
      rs.close();
      statement.close();
    } catch (SQLException e) {
      LOG.error("Failed to read rom info for " + tableFileName + ": " + e.getMessage(), e);
    }
    return rom;
  }

  public void updateScript(String emuName, String scriptName, String content) {
    try {
      Statement stmt = conn.createStatement();
      String sql = "UPDATE Emulators SET '" + scriptName + "'='" + content + "' WHERE EmuName = '" + emuName + "';";
      stmt.executeUpdate(sql);
      stmt.close();
      LOG.info("Update of " + scriptName + " successful.");
    } catch (Exception e) {
      LOG.error("Failed to update script script " + scriptName + ": " + e.getMessage(), e);
    }
  }

  public void updateRomName(String gameFileName, String romName) {
    try {
      Statement stmt = conn.createStatement();
      String sql = "UPDATE Games SET 'ROM'='" + romName.trim() + "' WHERE GameFileName = '" + gameFileName + "';";
      stmt.executeUpdate(sql);
      stmt.close();
      LOG.info("Update of " + gameFileName + " successful, written ROM name '"+  romName + "'");
    } catch (Exception e) {
      LOG.error("Failed to update script script " + gameFileName + ": " + e.getMessage(), e);
    }
  }
}
