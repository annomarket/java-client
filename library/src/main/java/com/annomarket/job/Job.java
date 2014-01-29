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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.IOUtils;

import com.annomarket.client.RestClientException;
import com.annomarket.common.Prices;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Full details of an annotation job, and methods to configure and
 * control the job. Note that while many of the fields on this object
 * are public, direct modifications to the field values will not be
 * reflected in the server-side state - use the manipulation methods to
 * modify the job on the server.
 * 
 * @author Ian Roberts
 */
public class Job extends JobSummary {

  /**
   * The job's numeric identifier.
   */
  public long id;

  /**
   * The name of the job.
   */
  public String name;

  /**
   * Internal unique identifier for the job, used for example in
   * temporary file names.
   */
  public String uuid;

  /**
   * The prices that will be charged when running this job.
   */
  public Prices price;

  /**
   * Date the job was created, in the form of {@link Date#toString()}.
   */
  public String dateCreated;

  /**
   * Date the job completed execution, in the form of
   * {@link Date#toString()}, or <code>null</code> if the job has not
   * yet been run.
   */
  public String dateCompleted;

  /**
   * Date when the results of the completed job will expire and be
   * deleted from temporary storage. If your job has not been configured
   * to store its results directly in your own S3 bucket then you must
   * download them before this time or they will be lost. This field
   * will be <code>null</code> if the job has not yet been run.
   */
  public String resultsAvailableUntil;

  /**
   * Processing time consumed so far by this job, measured in
   * milliseconds.
   */
  public long timeUsed;

  /**
   * Processing time that has been charged so far for this job. Measured
   * in milliseconds but the value will always be a multiple of one hour
   * (3600000 ms) and may be more than {@link #timeUsed} as charges are
   * rounded up to whole hours.
   */
  public long timeCharged;

  /**
   * Number of bytes of data processed so far by this job.
   */
  public long bytesUsed;

  /**
   * Number of bytes of data processing that has so far been charged to
   * your account for this job.
   */
  public long bytesCharged;

  /**
   * Number between 0 and 1 giving the proportion of the job's tasks
   * that have so far been completed, or less than 0 if the number of
   * sub-tasks has not yet been determined.
   */
  public double progress;

  /**
   * Change the name of this job.
   * 
   * @param newName the new name
   */
  public void rename(String newName) {
    ObjectNode request = JsonNodeFactory.instance.objectNode();
    request.put("name", newName);
    client.postForUpdate(url, this, request);
  }

  /**
   * Configure this job to send its output files directly to an S3
   * bucket that is not owned by AnnoMarket.
   * 
   * @param location a "URL" of the form
   *          <code>s3://bucketname/keyprefix/</code> denoting the
   *          target bucket and prefix to prepend to key values when
   *          storing objects in the bucket
   * @param accessKeyId an AWS access key ID (typically a limited IAM
   *          user) with permission to put objects in the specified
   *          bucket with keys starting with the specified prefix
   * @param secretKey the corresponding AWS secret key.
   */
  public void outputToS3(String location, String accessKeyId, String secretKey) {
    ObjectNode request = JsonNodeFactory.instance.objectNode();
    request.put("s3Location", location);
    request.put("accessKey", accessKeyId);
    request.put("secretKey", secretKey);
    client.post(url + "/outputDirectory", new TypeReference<JsonNode>() {
    }, request);
  }

  /**
   * Configure this job to use the default behaviour of saving its
   * output in an AnnoMarket-owned location accessible via
   * {@link #results()}. This call can be used to countermand an earlier
   * {@link #outputToS3}.
   */
  public void outputToDefault() {
    ObjectNode request = JsonNodeFactory.instance.objectNode();
    request.put("defaultLocation", true);
    client.post(url + "/outputDirectory", new TypeReference<JsonNode>() {
    }, request);
  }

  /**
   * List all the input specifications configured for this job.
   * 
   * @return one {@link InputSummary} object per input specification
   */
  public List<InputSummary> listInputs() {
    return client.get(url + "/input", new TypeReference<List<InputSummary>>() {
    });
  }

  /**
   * Upload a local "archive" file (zip, tar{.gz|.bz2}, or a Twitter
   * search result or stream of Tweets) as input to this job.
   * 
   * @param inputFile the local file to upload
   * @param inputType the type of the input
   * @param encoding character encoding to use when reading entries from
   *          the archive. If <code>null</code>, UTF-8 will be used.
   *          Should be left as <code>null</code> for Twitter input
   *          types.
   * @param mimeTypeOverride the MIME type to use when parsing entries
   *          from the archive. If <code>null</code> the appropriate
   *          type will be guessed based on the file name extension.
   *          Should be left as <code>null</code> for Twitter input
   *          types.
   * @param fileExtensions comma-separated list of file extensions that
   *          will be processed. Entries that do not match any of these
   *          extensions will be ignored. If <code>null</code> all
   *          entries that represent files (as opposed to directories)
   *          will be processed. Should be left as <code>null</code> for
   *          Twitter input types.
   * @return details of the newly-created input specification.
   */
  public InputDetails addArchiveInput(File inputFile, InputType inputType,
          String encoding, String mimeTypeOverride, String fileExtensions) {
    if(inputType == InputType.ARC) {
      throw new RestClientException("For ARC files use addARCInput");
    }
    return addUploadInput(inputFile, inputType, encoding, mimeTypeOverride,
            fileExtensions, null);
  }

  /**
   * Point to an "archive" file (zip, tar{.gz|.bz2}, or a Twitter search
   * result or stream of Tweets) on Amazon S3 as input to this job.
   * 
   * @param s3Location a "URL" of the form
   *          <code>s3://bucketname/key</code> denoting the target
   *          object in Amazon S3
   * @param accessKeyId an AWS access key ID (typically a limited IAM
   *          user) with permission to get the specified object
   * @param secretKey the corresponding AWS secret key.
   * @param inputType the type of the input
   * @param encoding character encoding to use when reading entries from
   *          the archive. If <code>null</code>, UTF-8 will be used.
   *          Should be left as <code>null</code> for Twitter input
   *          types.
   * @param mimeTypeOverride the MIME type to use when parsing entries
   *          from the archive. If <code>null</code> the appropriate
   *          type will be guessed based on the file name extension.
   *          Should be left as <code>null</code> for Twitter input
   *          types.
   * @param fileExtensions comma-separated list of file extensions that
   *          will be processed. Entries that do not match any of these
   *          extensions will be ignored. If <code>null</code> all
   *          entries that represent files (as opposed to directories)
   *          will be processed. Should be left as <code>null</code> for
   *          Twitter input types.
   * @return details of the newly-created input specification.
   */
  public InputDetails addArchiveInput(String s3Location, String accessKeyId,
          String secretKey, InputType inputType, String encoding,
          String mimeTypeOverride, String fileExtensions) {
    if(inputType == InputType.ARC) {
      throw new RestClientException("For ARC files use addARCInput");
    }
    return addS3Input(s3Location, accessKeyId, secretKey, inputType, encoding,
            mimeTypeOverride, fileExtensions, null);
  }

  /**
   * Upload a local Internet Archive ARC file as input to this job.
   * 
   * @param inputFile the local file to upload
   * @param inputType the type of the input
   * @param encoding character encoding to use when reading entries from
   *          the archive. If <code>null</code>, the ARC entry headers
   *          will be used to guess an appropriate encoding for each
   *          entry.
   * @param mimeTypeOverride the MIME type to use when parsing entries
   *          from the archive. If <code>null</code> the appropriate
   *          type will be guessed based on the file name extension and
   *          HTTP headers from the ARC entry.
   * @param mimeTypes space-separated list of MIME types used to filter
   *          the entries of interest from the ARC file. Entries whose
   *          MIME type does not match any of these will be ignored.
   * @return details of the newly-created input specification.
   */
  public InputDetails addARCInput(File inputFile, String encoding,
          String mimeTypeOverride, String mimeTypes) {
    return addUploadInput(inputFile, InputType.ARC, encoding, mimeTypeOverride,
            null, mimeTypes);
  }

  /**
   * Point to an Internet Archive ARC file on Amazon S3 as input to this
   * job.
   * 
   * @param s3Location a "URL" of the form
   *          <code>s3://bucketname/key</code> denoting the target
   *          object in Amazon S3
   * @param accessKeyId an AWS access key ID (typically a limited IAM
   *          user) with permission to get the specified object
   * @param secretKey the corresponding AWS secret key.
   * @param inputType the type of the input
   * @param encoding character encoding to use when reading entries from
   *          the archive. If <code>null</code>, the ARC entry headers
   *          will be used to guess an appropriate encoding for each
   *          entry.
   * @param mimeTypeOverride the MIME type to use when parsing entries
   *          from the archive. If <code>null</code> the appropriate
   *          type will be guessed based on the file name extension and
   *          HTTP headers from the ARC entry.
   * @param mimeTypes space-separated list of MIME types used to filter
   *          the entries of interest from the ARC file. Entries whose
   *          MIME type does not match any of these will be ignored.
   * @return details of the newly-created input specification.
   */
  public InputDetails addARCInput(String s3Location, String accessKeyId,
          String secretKey, String encoding, String mimeTypeOverride,
          String mimeTypes) {
    return addS3Input(s3Location, accessKeyId, secretKey, InputType.ARC,
            encoding, mimeTypeOverride, null, mimeTypes);
  }

  /**
   * Common implementation for all upload-based inputs.
   */
  protected InputDetails addUploadInput(File inputFile, InputType inputType,
          String encoding, String mimeTypeOverride, String fileExtensions,
          String mimeTypes) {
    ObjectNode request = JsonNodeFactory.instance.objectNode();
    request.put("fileName", inputFile.getName());
    // create the input
    InputDetails input =
            client.post(url + "/input", new TypeReference<InputDetails>() {
            }, request);
    try {
      // upload the file
      HttpURLConnection putConnection =
              (HttpURLConnection)input.putUrl.openConnection();
      putConnection.setDoOutput(true);
      putConnection.setRequestMethod("PUT");
      putConnection.setRequestProperty("Content-Type",
              "application/octet-stream");
      putConnection.setFixedLengthStreamingMode((int)inputFile.length());
      FileInputStream in = new FileInputStream(inputFile);
      OutputStream out = putConnection.getOutputStream();
      try {
        IOUtils.copy(in, out);
      } finally {
        IOUtils.closeQuietly(in);
        IOUtils.closeQuietly(out);
      }
    } catch(IOException e) {
      throw new RestClientException(e);
    }
    // configure the input
    return configureInput(input.url, inputType, encoding, mimeTypeOverride,
            fileExtensions, mimeTypes);
  }

  /**
   * Common implementation for all S3-based inputs.
   */
  protected InputDetails addS3Input(String s3Location, String accessKeyId,
          String secretKey, InputType inputType, String encoding,
          String mimeTypeOverride, String fileExtensions, String mimeTypes) {
    ObjectNode request = JsonNodeFactory.instance.objectNode();
    request.put("s3Location", s3Location);
    if(accessKeyId != null) {
      request.put("accessKey", accessKeyId);
      request.put("secretKey", secretKey);
    }
    // create the input
    InputDetails input =
            client.post(url + "/input", new TypeReference<InputDetails>() {
            }, request);
    return configureInput(input.url, inputType, encoding, mimeTypeOverride,
            fileExtensions, mimeTypes);
  }

  /**
   * Common implementation for post-creation configuration of inputs.
   */
  private InputDetails configureInput(String inputUrl, InputType inputType,
          String encoding, String mimeTypeOverride, String fileExtensions,
          String mimeTypeFilters) {
    ObjectNode request = JsonNodeFactory.instance.objectNode();
    if(inputType != null) {
      request.put("type", inputType.name());
    }
    if(encoding != null) {
      request.put("encoding", encoding);
    }
    if(mimeTypeOverride != null) {
      request.put("mimeTypeOverride", mimeTypeOverride);
    }
    if(fileExtensions != null) {
      request.put("fileExtensions", fileExtensions);
    }
    if(mimeTypeFilters != null) {
      request.put("mimeTypeFilters", mimeTypeFilters);
    }
    return client.post(inputUrl, new TypeReference<InputDetails>() {
    }, request);
  }

  /**
   * Define an input specification for this job based on a search of the
   * Common Crawl corpus.
   * 
   * @param protocol either "http" or "https". If <code>null</code>,
   *          pages for both protocols will be included.
   * @param hostName a pattern representing the host name(s) to be
   *          selected from the crawl. This is allowed to contain at
   *          most one "*" character, which, if present, must be placed
   *          exactly before the first ".". The pattern must contain at
   *          least two specified inter-dot segments (e.g. "*.com" is
   *          not permitted as it would match too many entries).
   * @param pathPrefix a pattern which may contain "*" characters that
   *          is matched against the path part of the URL for the
   *          crawled items. Only crawled items for which the path
   *          starts as specified will be returned. If <code>null</code>
   *          , all items that match the hostname will be returned.
   * @return details of the newly-created input specification.
   */
  public InputDetails addCommonCrawlInput(String protocol, String hostName,
          String pathPrefix) {
    if(hostName == null) {
      throw new RestClientException(
              "For common crawl inputs, hostname is required");
    }
    ObjectNode request = JsonNodeFactory.instance.objectNode();
    ObjectNode cc = JsonNodeFactory.instance.objectNode();
    request.put("commonCrawl", cc);
    if(protocol != null) {
      cc.put("protocol", protocol);
    }
    cc.put("hostname", hostName);
    if(pathPrefix != null) {
      cc.put("pathPrefix", pathPrefix);
    }
    // create the input
    return client.post(url + "/input", new TypeReference<InputDetails>() {
    }, request);
  }

  /**
   * List all the output specifications for this job.
   * 
   * @return one {@link Output} object per output specification
   */
  public List<Output> listOutputs() {
    return client.get(url + "/output", new TypeReference<List<Output>>() {
    });
  }

  /**
   * Add an output specification for this job that pushes results into a
   * M&iacute;mir index.
   * 
   * @param indexUrl URL of the target index
   * @param username username used to authenticate to the index. If
   *          <code>null</code> authentication is not used.
   * @param password the corresponding password (should be
   *          <code>null</code> if and only if username is
   *          <code>null</code>)
   * @return details of the newly-created output specification.
   */
  public Output addMimirOutput(String indexUrl, String username, String password) {
    ObjectNode request = JsonNodeFactory.instance.objectNode();
    request.put("type", OutputType.MIMIR.name());
    request.put("indexUrl", indexUrl);
    if(username != null) {
      request.put("username", username);
      request.put("password", password);
    }
    return client.post(url + "/output", new TypeReference<Output>() {
    }, request);
  }

  /**
   * Add an output specification for this job that saves results in
   * files (which will be packaged up and delivered as ZIP archives) in
   * one of a number of formats.
   * 
   * @param type output type
   * @param fileExtension the extension to append to the generated file
   *          names (e.g. ".GATE.xml"). This should be different for
   *          each output specification.
   * @param annotationSelectors comma-separated list of
   *          {@link Output#annotationSelectors annotation selector
   *          expressions}.
   * @return details of the newly-created output specification.
   */
  public Output addFileOutput(OutputType type, String fileExtension,
          String annotationSelectors) {
    ObjectNode request = JsonNodeFactory.instance.objectNode();
    request.put("type", type.name());
    request.put("fileExtension", fileExtension);
    if(annotationSelectors != null) {
      request.put("annotationSelectors", annotationSelectors);
    }
    return client.post(url + "/output", new TypeReference<Output>() {
    }, request);
  }

  /**
   * Common implementation for job control actions.
   */
  protected void control(String action) {
    ObjectNode request = JsonNodeFactory.instance.objectNode();
    request.put("action", action);
    client.post(url + "/control", new TypeReference<JsonNode>() {
    }, request);
    refresh();
  }

  /**
   * Start the execution of this job. The job must be in the
   * {@link JobState#READY READY} state or this operation will fail.
   */
  public void start() {
    control("start");
  }

  /**
   * Stop the execution of this job. The job must be in the
   * {@link JobState#RUNNING RUNNING} state or this operation will fail.
   */
  public void stop() {
    control("stop");
  }

  /**
   * Resume execution of this job after it was suspended due to lack of
   * funds. The job must be in the {@link JobState#SUSPENDED SUSPENDED}
   * state or this operation will fail.
   */
  public void resume() {
    control("resume");
  }

  /**
   * Reset this job so it can be re-run. The job must be in the
   * {@link JobState#COMPLETED COMPLETED} state or this operation will
   * fail.
   */
  public void reset() {
    control("reset");
  }

  /**
   * Fetch execution log messages for this job. The <code>from</code>
   * and <code>to</code> parameters allow you to restrict to log entries
   * within a specific window of time - either or both of these
   * parameters may be <code>null</code>.
   * 
   * @param from earliest time stamp for log events to retrieve.
   * @param to latest time stamp for log events to retrieve.
   * @return log messages within the specified range
   */
  public List<LogMessage> executionLog(Calendar from, Calendar to) {
    StringBuilder urlBuilder = new StringBuilder(url);
    urlBuilder.append("/log");
    try {
      if(from != null) {
        urlBuilder.append("?from=");
        urlBuilder.append(URLEncoder.encode(
                DatatypeConverter.printDateTime(from), "UTF-8"));
      }
      if(to != null) {
        if(from == null) {
          urlBuilder.append("?to=");
        } else {
          urlBuilder.append("&to=");
        }
        urlBuilder.append(URLEncoder.encode(
                DatatypeConverter.printDateTime(to), "UTF-8"));
      }
    } catch(UnsupportedEncodingException e) {
      // shouldn't happen
      throw new RuntimeException("JVM claims not to support UTF-8...", e);
    }

    return client.get(urlBuilder.toString(),
            new TypeReference<List<LogMessage>>() {
            });
  }

  /**
   * Retrieve all the result files produced by this job.
   * 
   * @return a list of results, call <code>urlToDownload</code> on each
   *         result to get a (time-limited) URL from which the file can
   *         be downloaded.
   */
  public List<JobResult> results() {
    return client.get(url + "/results", new TypeReference<List<JobResult>>() {
    });
  }

  /**
   * Refresh this job's data from the server to update things like the
   * {@link #progress} counter.
   */
  public void refresh() {
    client.getForUpdate(url, this);
  }

  /**
   * Delete this job, which also deletes any output files that are
   * stored in the default AnnoMarket-managed location.
   */
  public void delete() {
    client.delete(url);
  }

}
