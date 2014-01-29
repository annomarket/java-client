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

import java.net.URL;

/**
 * A single input specification for an annotation job. One job can (and
 * usually will) have many inputs.
 * 
 * @author Ian Roberts
 */
public class InputDetails extends InputSummary {

  /**
   * Time-limited URL to which a file can be uploaded using HTTP PUT.
   */
  public URL putUrl;

  /**
   * For files loaded from a non-AnnoMarket S3 bucket, the AWS access
   * key that will be used to download them. The corresponding secret
   * key must be provided when creating such an input, but is
   * deliberately <i>not</i> returned by the API when querying.
   */
  public String accessKey;

  /**
   * Character encoding to use when reading entries from the archive. If
   * <code>null</code>, the ARC entry headers will be used to guess an
   * appropriate encoding for each entry (in the case or ARC files) or a
   * default of UTF-8 will be used (for other formats).
   */
  public String encoding;

  /**
   * MIME type to use when parsing entries from the archive. If
   * <code>null</code> the appropriate type will be guessed based on the
   * file name extension and (in the case of ARC files) the HTTP headers
   * from the ARC entry.
   */
  public String mimeTypeOverride;

  /**
   * Comma-separated list of file extensions that will be processed.
   * Entries that do not match any of these extensions will be ignored.
   * If <code>null</code> all entries that represent files (as opposed
   * to directories) will be processed. Should be left as
   * <code>null</code> for Twitter input types, and will always be
   * <code>null</code> for ARC inputs.
   */
  public String fileExtensions;

  /**
   * Space-separated list of MIME types used to filter the entries of
   * interest from ARC input files. Entries whose MIME type does not
   * match any of these will be ignored. Will be <code>null</code> for
   * non-ARC inputs.
   */
  public String mimeTypes;

  /**
   * Details of Common Crawl search parameters. Will be
   * <code>null</code> for inputs that are not Common Crawl searches.
   */
  public CommonCrawlDetails commonCrawl;

  /**
   * "Struct" class holding details of a Common Crawl search.
   */
  public static class CommonCrawlDetails {
    /**
     * Either "http" or "https". If <code>null</code>, pages for both
     * protocols will be included.
     */
    public String protocol;

    /**
     * A pattern representing the host name(s) to be selected from the
     * crawl. This is allowed to contain at most one "*" character,
     * which, if present, must be placed exactly before the first ".".
     * The pattern must contain at least two specified inter-dot
     * segments (e.g. "*.com" is not permitted as it would match too
     * many entries).
     */
    public String hostname;

    /**
     * A pattern which may contain "*" characters that is matched
     * against the path part of the URL for the crawled items. Only
     * crawled items for which the path starts as specified will be
     * returned. If <code>null</code>, all items that match the hostname
     * will be returned.
     */
    public String pathPrefix;

    /**
     * Number of items found so far (if {@link #complete} is
     * <code>false</code>) or in total (if <code>complete</code> is
     * <code>true</code>).
     */
    public long items;

    /**
     * Is the search complete?
     */
    public boolean complete;
  }

  /**
   * Refresh this input's state from the server, to update things like
   * Common Crawl search progress.
   */
  public void refresh() {
    client.getForUpdate(url, this);
  }

  /**
   * Delete this input specification.
   */
  public void delete() {
    client.delete(url);
  }

}
