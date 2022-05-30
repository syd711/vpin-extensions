package com.coremedia.corepin.web.highscores;

import java.util.ArrayList;
import java.util.List;

public class Highscore {
  private List<Score> scores = new ArrayList<>();

  private String userInitials;
  private String score;
  private int position;

  public List<Score> getScores() {
    return scores;
  }

  public void setScores(List<Score> scores) {
    this.scores = scores;
  }

  public String getUserInitials() {
    return userInitials;
  }

  public void setUserInitials(String userInitials) {
    this.userInitials = userInitials;
  }

  public String getScore() {
    return score;
  }

  public void setScore(String score) {
    this.score = score;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }
}
