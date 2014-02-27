package com.annomarket.online;

public enum ResponseFormat {
  JSON("application/gate+json"),
  
  GATE_XML("application/gate+xml");

  public final String acceptHeader;
  
  private ResponseFormat(String acceptHeader) {
    this.acceptHeader = acceptHeader;
  }
  
}
