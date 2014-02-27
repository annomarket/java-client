package com.annomarket.online;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.annomarket.common.ApiObject;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.JsonNode;

public class AnnotatedDocument extends ApiObject {
  public String text;
  
  public Map<String, List<Annotation>> entities;
  
  public Map<String, JsonNode> otherFeatures;
  
  @JsonAnySetter
  public void addFeature(String name, JsonNode value) {
    if(otherFeatures == null) otherFeatures = new HashMap<String, JsonNode>();
    otherFeatures.put(name, value);
  }
}
