package de.mephisto.vpin.popper.overlay.util;

import de.mephisto.vpin.popper.overlay.ConfigWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

public class ProgressDialog extends JDialog implements ActionListener {
  private final static Logger LOG = LoggerFactory.getLogger(ProgressDialog.class);

  private final JLabel statusLabel;
  private final ProgressWorker progressWorker;
  private final JLabel progressLabel;

  private int progressPercentage = 0;
  private String displayValue;
  private ProgressResultModel progressResultModel;

  public ProgressDialog(ConfigWindow parent, ProgressModel model) {
    super(parent, model.getTitle(), true);
    setSize(500, 200);
    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(4, 4, 4, 4);
    gbc.gridx = 0;
    gbc.gridy = 0;

    JProgressBar progressBar = new JProgressBar(0, 100);
    progressBar.setValue(0);
    progressBar.setBorderPainted(false);
    progressBar.setStringPainted(true);
    progressBar.setPreferredSize(new Dimension(460, 30));
    progressBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    add(progressBar, gbc);
    gbc.gridy++;

    statusLabel = new JLabel(" ");
    add(statusLabel, gbc);
    gbc.gridy++;

    progressLabel = new JLabel("...");
    add(progressLabel, gbc);
    gbc.gridy++;

    JButton cancelButton = new JButton("Cancel");
    cancelButton.setActionCommand("cancel");
    cancelButton.addActionListener(this);
    add(cancelButton, gbc);
    gbc.gridy++;

    setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    setLocationRelativeTo(ConfigWindow.getInstance());

    progressWorker = new ProgressWorker(model);
    progressWorker.addPropertyChangeListener(evt -> {
      String name = evt.getPropertyName();
      if (name.equals("progress")) {
        progressBar.setValue(progressPercentage);
      }
      else if (name.equals("state")) {
        LOG.info("Progress dialog get done event at " + evt.getNewValue());
        SwingWorker.StateValue state = (SwingWorker.StateValue) evt.getNewValue();
        switch (state) {
          case DONE:
            setCursor(null); //turn off the wait cursor
            this.setVisible(false);
            break;
        }
      }
    });
    progressWorker.execute();

    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
  }

  public ProgressResultModel showDialog() {
    progressResultModel = new ProgressResultModel();
    this.setVisible(true);
    return progressResultModel;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("cancel")) {
      progressWorker.cancel(false);
      setCursor(null); //turn off the wait cursor
      this.setVisible(false);
    }
  }


  public class ProgressWorker extends SwingWorker<Object, Object> {

    private ProgressModel model;

    public ProgressWorker(ProgressModel model) {
      this.model = model;
    }

    @Override
    protected Object doInBackground() throws Exception {
      int count = 0;
      Iterator iterator = model.getIterator();
      while (iterator.hasNext() && !this.isCancelled()) {
        displayValue = model.processNext(progressResultModel);
        statusLabel.setText("Processing " + (count + 1) + " of " + model.getMax());
        progressLabel.setText("Finished '" + displayValue + "'");
        progressLabel.repaint();
        count++;

        int progress = getProgress();
        progressPercentage = count * 100 / model.getMax();
        if (progress != progressPercentage) {
          setProgress(progressPercentage);
        }
      }

      return null;
    }
  }
}
