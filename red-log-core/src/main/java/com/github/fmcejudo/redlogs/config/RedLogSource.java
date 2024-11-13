package com.github.fmcejudo.redlogs.config;

public class RedLogSource {

  private String type;

  private RedLogFileProperties file;

  private RedLogGithubProperties github;

  public RedLogFileProperties getFile() {
    return file;
  }

  public void setFile(RedLogFileProperties file) {
    this.file = file;
  }

  public RedLogGithubProperties getGithub() {
    return github;
  }

  public void setGithub(RedLogGithubProperties github) {
    this.github = github;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
