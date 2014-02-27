package com.annomarket.online;

import java.util.HashMap;
import java.util.Map;

import com.annomarket.common.ApiObject;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Annotation extends ApiObject {
  public long startOffset;
  
  public long endOffset;
  
  public Map<String, Object> features = new HashMap<String, Object>();

  @JsonCreator
  public Annotation(@JsonProperty("indices") long[] indices) {
    startOffset = indices[0];
    endOffset = indices[1];
  }
  
  @JsonAnySetter
  public void addFeature(String name, Object value) {
    features.put(name, value);
  }

}
