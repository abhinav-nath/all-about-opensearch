package com.codecafe.search.helper;

public enum SearchTemplate {

  TEXT_SEARCH_TEMPLATE("text-search-template", "mustache-template/text-search-template.mustache");

  private final String id;
  private final String sourcePath;

  SearchTemplate(String id, String sourcePath) {
    this.id = id;
    this.sourcePath = sourcePath;
  }

  public String getId() {
    return id;
  }

  public String getSourcePath() {
    return sourcePath;
  }

}