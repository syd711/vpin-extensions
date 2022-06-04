package de.ggs.vpin.extensions.util;

import de.ggs.vpin.extensions.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileUtils {
  private final static Logger LOG = LoggerFactory.getLogger(FileUtils.class);

  public static void copyResource(String name, File target) {
    target.getParentFile().mkdirs();
    InputStream in = Application.class.getResourceAsStream(name);
    try {
      //create FileOutputStream object for destination file
      FileOutputStream fout = new FileOutputStream(target);

      byte[] b = new byte[1024];
      int noOfBytes = 0;
      //read bytes from source file and write to destination file
      while ((noOfBytes = in.read(b)) != -1) {
        fout.write(b, 0, noOfBytes);
      }
      in.close();
      fout.close();
      LOG.info("Created file " + target.getAbsolutePath());
    } catch (Exception e) {
      LOG.error("Error copying resource file " + name + " to " + target.getAbsolutePath() + " :" + e);
    }
  }
}
