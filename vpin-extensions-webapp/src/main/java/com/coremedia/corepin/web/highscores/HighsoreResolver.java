package com.coremedia.corepin.web.highscores;

import com.coremedia.corepin.web.PinballProperties;
import com.coremedia.corepin.web.tables.TableInfo;
import com.coremedia.corepin.web.util.SystemCommandExecutor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class HighsoreResolver {
  private final static Logger LOG = LoggerFactory.getLogger(HighsoreResolver.class);

  @Value("${7zip.command}")
  private String unzipCommand;

  @Value("${visualpinball.folder}")
  private String visualPinballFolder;

  @Value("${server.home}")
  private String serverHome;

  @Value("${pinemhi.command}")
  private String pinemhiCommand;

  @Autowired
  private HighscoreParser highscoreParser;

  /**
   * Return a highscore object for the given table or null if no highscore has been achieved or created yet.
   *
   * @param table the table to find the highscore for
   */
  public Highscore findHighscore(TableInfo table) {
    try {
      Highscore highscore = parseNvHighscore(table);
      if (highscore == null) {
        highscore = parseVRegHighscore(table);
      }

      if(highscore == null) {
        LOG.warn("Read highscore for '" + table.getName() + "'" + getFiller(table.getName()) + "[No nvram highscore and no VPReg.stg entry found for table '" + table.getName() + "' with rom " +  table.getRomName() + "]");
      }
      else {
        LOG.info("Read highscore for '{}'" + getFiller(table.getName()) + "[ OK ]", table.getName());
      }

      return highscore;
    } catch (Exception e) {
      LOG.error("Failed to find highscore for table {}: {}", table.getName(), e.getMessage(), e);
    }

    return null;
  }

  private String getFiller(String name) {
    int length = name.length();
    if(length >= 40) {
      return "";
    }
    StringBuilder b = new StringBuilder();
    while(length < 40) {
      b.append(" ");
      length++;
    }
    return b.toString();
  }

  /**
   * Refreshes the extraction of the VPReg.stg file.
   */
  public void refreshVPReg() {
    File pinballFolder = new File(visualPinballFolder);
    File userFolder = new File(pinballFolder, PinballProperties.USER);
    File mameHighscoreFile = new File(userFolder, "VPReg.stg");
    File targetFolder = new File(serverHome, "VPReg");
    if (!targetFolder.exists()) {
      targetFolder.mkdirs();
    }

    //check if we have to unzip the score file using the modified date of the target folder
    updateUserScores(mameHighscoreFile, targetFolder);
  }

  /**
   * We use the manual setted rom name to find the highscore in the "/User/VPReg.stg" file.
   *
   * @param table
   * @return
   * @throws IOException
   */
  private Highscore parseVRegHighscore(TableInfo table) throws IOException {
    File targetFolder = new File(serverHome, "VPReg");
    File tableHighscoreFolder = new File(targetFolder, table.getRomName());

    if (tableHighscoreFolder.exists()) {
      table.setLastModified(tableHighscoreFolder.lastModified());

      File tableHighscoreFile = new File(tableHighscoreFolder, "HighScore1");
      File tableHighscoreNameFile = new File(tableHighscoreFolder, "HighScore1Name");
      if (tableHighscoreFile.exists() && tableHighscoreNameFile.exists()) {
        String highScoreValue = readFileString(tableHighscoreFile);
        highScoreValue = HighscoreParser.formatScore(highScoreValue);
        String initials = readFileString(tableHighscoreNameFile);

        Highscore highscore = new Highscore();
        highscore.setPosition(0);
        highscore.setUserInitials(initials);
        highscore.setScore(highScoreValue);

        for (int i = 1; i <= 4; i++) {
          tableHighscoreFile = new File(tableHighscoreFolder, "HighScore" + i);
          tableHighscoreNameFile = new File(tableHighscoreFolder, "HighScore" + i + "Name");
          if (tableHighscoreFile.exists() && tableHighscoreNameFile.exists()) {
            highScoreValue = readFileString(tableHighscoreFile);
            highScoreValue = HighscoreParser.formatScore(highScoreValue);
            initials = readFileString(tableHighscoreNameFile);

            Score score = new Score(initials, highScoreValue, i - 1);
            highscore.getScores().add(score);
          }
        }

        return highscore;
      }
    }

    return null;
  }

  /**
   * Uses 7zip to unzip the stg file into the configured target folder
   *
   * @param mameHighscoreFile
   * @param targetFolder
   */
  private void updateUserScores(File mameHighscoreFile, File targetFolder) {
    try {
      List<String> commands = Arrays.asList("\"" + unzipCommand + "\"", "-aoa", "x", "\"" + mameHighscoreFile.getAbsolutePath() + "\"", "-o\"" + targetFolder.getAbsolutePath() + "\"");
      SystemCommandExecutor executor = new SystemCommandExecutor(commands, false);
      executor.setDir(targetFolder);
      executor.executeCommand();

      StringBuilder standardOutputFromCommand = executor.getStandardOutputFromCommand();
      StringBuilder standardErrorFromCommand = executor.getStandardErrorFromCommand();
      if (!StringUtils.isEmpty(standardErrorFromCommand.toString())) {
        LOG.error("7zip command failed: {}", standardErrorFromCommand.toString());
      }
    } catch (Exception e) {
      LOG.error("Failed to parse {}: {}", mameHighscoreFile.getAbsolutePath(), e);
    }
  }

  private Highscore parseNvHighscore(TableInfo info) {
    File pinballFolder = new File(visualPinballFolder);
    File mameFolder = new File(pinballFolder, PinballProperties.VPINMAME);
    File nvRamFolder = new File(mameFolder, PinballProperties.NVRAM);
    File nvRam = new File(nvRamFolder, info.getRomName() + ".nv");
    File commandFile = new File(serverHome + pinemhiCommand);

    if (!nvRam.exists()) {
      return null;
    }

    info.setLastModified(nvRam.lastModified());

    SystemCommandExecutor executor = new SystemCommandExecutor(Arrays.asList(commandFile.getName(), nvRam.getName()));
    executor.setDir(commandFile.getParentFile());
    try {
      executor.executeCommand();
      StringBuilder standardOutputFromCommand = executor.getStandardOutputFromCommand();
      StringBuilder standardErrorFromCommand = executor.getStandardErrorFromCommand();
      if (!StringUtils.isEmpty(standardErrorFromCommand.toString())) {
        LOG.error("Pinemhi command failed: {}", standardErrorFromCommand.toString());
      } else {
        String s = standardOutputFromCommand.toString();
        return highscoreParser.parseHighscore(s);
      }
    } catch (Exception e) {
      LOG.error("Failed to parse {}: {}", nvRam.getAbsolutePath(), e);
    }
    return null;
  }

  /**
   * Reads the first line of the given file
   */
  private String readFileString(File file) throws IOException {
    BufferedReader brTest = new BufferedReader(new FileReader(file));
    String text = brTest.readLine();
    brTest.close();
    return text.replace("\0", "").trim();
  }
}
