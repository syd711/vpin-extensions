package de.ggs.vpin.extensions.sqllite;

import de.ggs.vpin.extensions.services.SystemInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.*;

@Service
public class SqliteConnector implements InitializingBean {
  private final static Logger LOG = LoggerFactory.getLogger(SqliteConnector.class);

  private Connection conn;

  @Autowired
  private SystemInfoService systemInfoService;

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
      script = rs.getString("LaunchScript");
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
      script = rs.getString("PostScript");
      rs.close();
      statement.close();
    } catch (SQLException e) {
      LOG.error("Failed to read exit script or " + emuName + ": " + e.getMessage(), e);
    }
    return script;
  }

  public void updateScript(String scriptName, String content) {
    try {
      Statement stmt = conn.createStatement();
      String sql = "INSERT INTO Emulators (" + scriptName + ") VALUES ('" + content + "');";
      stmt.executeUpdate(sql);
      stmt.close();
    } catch (Exception e) {
      LOG.error("Failed to update script script " + scriptName + ": " + e.getMessage(), e);
    }
  }
}
