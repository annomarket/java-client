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

import com.annomarket.common.ApiObject;

/**
 * A single output specification for an annotation job. One job can have
 * many outputs.
 * 
 * @author Ian Roberts
 */
public class Output extends ApiObject {
  /**
   * Detail URL for this output specification.
   */
  public String url;

  /**
   * Type of the output, either a file-based type or M&iacute;mir.
   */
  public OutputType type;

  // for Mimir
  /**
   * For M&iacute;mir outputs, the URL of the target index.
   */
  public String indexUrl;

  /**
   * For M&iacute;mir outputs, the username used to authenticate to the
   * index. May be <code>null</code> if authentication is not required.
   */
  public String username;

  // for others
  /**
   * For file-based outputs, the extension to add to each output file
   * name.
   */
  public String fileExtension;

  /**
   * Comma-separated list of "annotation selector expressions" denoting
   * the annotations to be saved. Each selector consists of an
   * annotation set name and an annotation type separated by a colon,
   * e.g. <code>NER:Person</code> would denote annotations of type
   * "Person" from the "NER" annotation set. An empty annotation set
   * name denotes the default annotation set, and an empty annotation
   * type includes <i>all</i> annotations from the selected set (so in
   * particular a single colon ":" on its own is a valid selector
   * expression, denoting all annotations from the default set).
   */
  public String annotationSelectors;

  /**
   * Delete this output specification.
   */
  public void delete() {
    client.delete(url);
  }
}
