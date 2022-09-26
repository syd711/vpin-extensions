package de.mephisto.vpin.extensions.util;

import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;

public class MessageWithLink extends JEditorPane {
  private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(MessageWithLink.class);
  private static final long serialVersionUID = 1L;

  public MessageWithLink(String htmlBody) {
    super("text/html", "<html><body style=\"" + getStyle() + "\">" + htmlBody + "</body></html>");
    addHyperlinkListener(new HyperlinkListener() {
      @Override
      public void hyperlinkUpdate(HyperlinkEvent e) {
        try {
          if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
            Desktop.getDesktop().browse(e.getURL().toURI());
          }
        } catch (Exception ex) {
         LOG.error("Failed to open hyperlink: " + ex.getMessage(), ex);
        }
      }
    });
    setEditable(false);
    setBorder(null);
  }

  static StringBuffer getStyle() {
    // for copying style
    JLabel label = new JLabel();
    Font font = label.getFont();
    Color color = label.getBackground();

    // create some css from the label's font
    StringBuffer style = new StringBuffer("font-family:" + font.getFamily() + ";");
    style.append("font-weight:" + (font.isBold() ? "bold" : "normal") + ";");
    style.append("font-size:" + font.getSize() + "pt;");
    style.append("background-color: rgb("+color.getRed()+","+color.getGreen()+","+color.getBlue()+");");
    return style;
  }
}