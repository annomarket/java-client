/*
 * Copyright (c) 2014 The University of Sheffield
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
package com.annomarket.job;

/**
 * Enumeration of valid output types.
 * 
 * @author Ian Roberts
 */
public enum OutputType {
  /**
   * The "GATE XML" standoff XML format.
   */
  GATE_XML,

  /**
   * Annotations stored as inline XML tags.
   */
  INLINE_XML,

  /**
   * Standoff XML format that stores the document plain text in one file
   * and the annotations in another.
   */
  XCES,

  /**
   * JSON format based on that used by Twitter, storing the document
   * text as a "text" property and the annotations as an "entities"
   * property in a JSON object.
   */
  JSON,

  /**
   * Send documents to a M&iacute;mir indexing server.
   */
  MIMIR,
}
