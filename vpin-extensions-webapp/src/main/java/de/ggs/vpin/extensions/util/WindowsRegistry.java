package de.ggs.vpin.extensions.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class WindowsRegistry {
  private final static Logger LOG = LoggerFactory.getLogger(WindowsRegistry.class);


  public static final String readRegistry(String location, String key){
    try {
      // Run reg query, then read output with StreamReader (internal class)
      String cmd = "reg query " + '"'+ location;
      if(key != null) {
        cmd = "reg query " + '"'+ location + "\" /v " + key;
      }
      Process process = Runtime.getRuntime().exec(cmd);
      StreamReader reader = new StreamReader(process.getInputStream());
      reader.start();
      process.waitFor();
      reader.join();
      String output = reader.getResult();
      return output;
    }
    catch (Exception e) {
      LOG.error("Failed to read registry key " + location);
      return null;
    }
  }

  static class StreamReader extends Thread {
    private InputStream is;
    private StringWriter sw= new StringWriter();

    public StreamReader(InputStream is) {
      this.is = is;
    }

    public void run() {
      try {
        int c;
        while ((c = is.read()) != -1)
          sw.write(c);
      }
      catch (IOException e) {
      }
    }

    public String getResult() {
      return sw.toString();
    }
  }
}
