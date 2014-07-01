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
package com.annomarket.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.annomarket.client.RestClientException;
import com.annomarket.common.InputType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Full details of a data bundle, and methods to configure and download
 * the bundle contents. Note that while many of the fields on this
 * object are public, direct modifications to the field values will not
 * be reflected in the server-side state - use the manipulation methods
 * to modify the details on the server.
 * 
 * @author Ian Roberts
 */
public class DataBundle extends DataBundleSummary {
  /**
   * Date that this bundle was initially created.
   */
  public String dateCreated;

  /**
   * The total size of this bundle, in bytes, or 0 if the size is not
   * known.
   */
  public long totalSize;

  /**
   * The price charged per month for storage of this bundle's data (may
   * be zero).
   */
  public double monthlyPrice;

  /**
   * The type of this input (ZIP, TAR, ARC, ARC_RECORDS, TWITTER_SEARCH
   * or TWITTER_STREAM). May be null if this bundle is not suitable for
   * use as input to an annotation job.
   */
  public InputType type;

  /**
   * Character encoding to use when reading entries from the archives in
   * this bundle. If <code>null</code>, the (W)ARC entry headers will be
   * used to guess an appropriate encoding for each entry (in the case
   * of ARC or WARC files) or a default of UTF-8 will be used (for other
   * formats).
   */
  public String encoding;

  /**
   * MIME type to use when parsing entries from the archive. If
   * <code>null</code> the appropriate type will be guessed based on the
   * file name extension and (in the case of (W)ARC files) the HTTP
   * headers from the (W)ARC entry.
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
   * interest from ARC and WARC input files. Entries whose MIME type
   * does not match any of these will be ignored. Will be
   * <code>null</code> for non-(W)ARC inputs.
   */
  public String mimeTypeFilters;

  /**
   * Files in this bundle.
   * 
   * @return a list of files, call <code>urlToDownload</code> on each
   *         entry to get a (time-limited) URL from which the file can
   *         be downloaded.
   */
  public List<BundleEntry> files;

  /**
   * Change the name of this data bundle.
   * 
   * @param newName the new name
   */
  public void rename(String newName) {
    ObjectNode request = JsonNodeFactory.instance.objectNode();
    request.put("name", newName);
    client.postForUpdate(url, this, request);
  }

  /**
   * Upload a file to an open bundle.
   * 
   * @param inputFile the file to upload. The last component of the
   *          File's path will be used as the name of the file in the
   *          bundle.
   */
  public void addFile(File inputFile) {
    InputStream source = null;
    try {
      source = new FileInputStream(inputFile);
      addFile(inputFile.getName(), (int)inputFile.length(), source);
    } catch(IOException e) {
      throw new RestClientException(e);
    } finally {
      IOUtils.closeQuietly(source);
    }
  }

  /**
   * Upload a file to an open bundle.
   * 
   * @param fileName the name to use for the bundle entry
   * @param contentLength the number of bytes to upload
   * @param source an input stream from which the file's content can be
   *          read. It must provide exactly <code>contentLength</code>
   *          bytes up to end-of-file. The stream will be read to EOF
   *          but will not be closed by this method, the caller is
   *          responsible for ensuring the stream is properly closed.
   */
  public void addFile(String fileName, int contentLength, InputStream source) {
    ObjectNode request = JsonNodeFactory.instance.objectNode();
    request.put("fileName", fileName);
    // create the input
    AddResult addRes =
            client.post(url + "/add", new TypeReference<AddResult>() {
            }, request);
    try {
      // upload the file
      HttpURLConnection putConnection =
              (HttpURLConnection)addRes.putUrl.openConnection();
      putConnection.setDoOutput(true);
      putConnection.setRequestMethod("PUT");
      putConnection.setRequestProperty("Content-Type",
              "application/octet-stream");
      putConnection.setFixedLengthStreamingMode(contentLength);
      OutputStream out = putConnection.getOutputStream();
      try {
        IOUtils.copy(source, out);
      } finally {
        IOUtils.closeQuietly(out);
      }
    } catch(IOException e) {
      throw new RestClientException(e);
    }
  }

  private static class AddResult {
    public URL putUrl;
  }

  /**
   * Close a bundle that is open for uploads.
   */
  public void close() {
    // has to be a POST request, but any body will do
    client.postForUpdate(url + "/close", this,
            JsonNodeFactory.instance.objectNode());
  }

  /**
   * Delete this bundle, which also deletes any contained files that are
   * stored in the default AnnoMarket-managed location.
   */
  public void delete() {
    client.delete(url);
  }

}
