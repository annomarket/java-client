/*
 * Copyright (c) 2014 The University of Sheffield, Ontotext AD
 *
 * This file is part of the AnnoMarket.com REST client library, and is
 * licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.annomarket.online;

/**
 * Enumeration of the MIME types supported by the online API.
 * 
 * @author Petar Kostov
 */
public enum SupportedMimeType {

  PLAINTEXT("text/plain"),
  HTML("text/html"),
  XML_APPLICATION("application/xml"),
  XML_TEXT("text/xml"),
  PUBMED("text/x-pubmed"),
  COCHRANE("text/x-cochrane"),
  MEDIAWIKI("text/x-mediawiki"),
  TWITTER_JSON("text/x-json-twitter");

  private SupportedMimeType(String type) {
    this.value = type;
  }

  public final String value;
}
