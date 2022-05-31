package de.ggs.vpin.extensions.services;

import de.ggs.vpin.extensions.util.CommandResultParser;
import de.ggs.vpin.extensions.util.WindowsRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class SystemInfoService implements InitializingBean {
  private final static Logger LOG = LoggerFactory.getLogger(SystemInfoService.class);

  private final static String REG_KEY = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Classes\\Applications\\VPinballX.exe\\shell\\open\\command";
  private final static String POPPER_REG_KEY = "HKEY_LOCAL_MACHINE\\SYSTEM\\ControlSet001\\Control\\Session Manager\\Environment";

  @Override
  public void afterPropertiesSet() {
    File vpxInstallationFolder = this.getVPXInstallationFolder();
    LOG.info("Resolved VPX installation folder:\t\t\t\t" + vpxInstallationFolder.getAbsolutePath());
    File popperInstallationFolder = this.getPopperInstallationFolder();
    LOG.info("Resolved PinUP Popper installation folder:\t\t" + popperInstallationFolder.getAbsolutePath());
  }

  public File getVPXInstallationFolder() {
    try {
      String output = WindowsRegistry.readRegistry(REG_KEY, null);
      String path = CommandResultParser.extractStandardKeyValue(output);
      File folder = new File(path).getParentFile();
      if (folder.exists()) {
        return folder;
      }

      File popperFolder = new File("C:/Visual Pinball");
      if (popperFolder.exists()) {
        return popperFolder;
      }
    } catch (Exception e) {
      LOG.error("Failed to read installation folder: " + e.getMessage(), e);
    }
    return null;
  }

  public File getPopperInstallationFolder() {
    try {
      String output = WindowsRegistry.readRegistry(POPPER_REG_KEY, "PopperInstDir");
      if (output != null && output.trim().length() > 0) {
        String path = CommandResultParser.extractRegistryValue(output);
        File folder = new File(path, "PinUPSystem");
        if (folder.exists()) {
          return folder;
        }
      }

      File popperFolder = new File(getPopperInstallationFolder().getParentFile(), "PinUPSystem");
      if (popperFolder.exists()) {
        return popperFolder;
      }

      popperFolder = new File("C:/PinUPSystem");
      if (popperFolder.exists()) {
        return popperFolder;
      }
    } catch (Exception e) {
      LOG.error("Failed to read installation folder: " + e.getMessage(), e);
    }
    return null;
  }


  public static void main(String[] args) {
    new SystemInfoService().afterPropertiesSet();
  }
}
