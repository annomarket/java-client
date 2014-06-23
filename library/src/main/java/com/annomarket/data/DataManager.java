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
import java.util.List;

import com.annomarket.client.RestClient;
import com.annomarket.client.RestClientException;
import com.annomarket.common.InputType;
import com.annomarket.job.JobSummary;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Main entry point for the data bundle API to create, manage, and
 * download data bundles that you own.
 * 
 * @author Ian Roberts
 */
public class DataManager {

  private RestClient client;

  /**
   * Construct a <code>DataManager</code> using the given
   * {@link RestClient} to communicate with the API.
   * 
   * @param client the client object used for communication
   */
  public DataManager(RestClient client) {
    this.client = client;
  }

  /**
   * Construct a <code>DataManager</code> accessing the AnnoMarket.com
   * public API with the given credentials.
   * 
   * @param apiKeyId API key ID for authentication
   * @param apiPassword corresponding password
   */
  public DataManager(String apiKeyId, String apiPassword) {
    this(new RestClient(apiKeyId, apiPassword));
  }

  /**
   * List all the data bundles that are owned by the authenticating user
   * 
   * @return list of {@link DataBundleSummary} objects representing the
   *         user's bundles - call the <code>details</code> method to
   *         get the full detail (which requires another API call).
   */
  public List<DataBundleSummary> listBundles() {
    return client.get("data/bundle",
            new TypeReference<List<DataBundleSummary>>() {
            });
  }

  /**
   * Get details of a particular bundle given its ID.
   * 
   * @param id the ID of the required bundle
   * @return the bundle details
   */
  public DataBundle getBundle(long id) {
    return client.get("data/bundle/" + id, new TypeReference<DataBundle>() {
    });
  }

  /**
   * Get details of a specific bundle given its detail URL (which will
   * have been received from an earlier API call).
   * 
   * @param url the detail URL for the required bundle
   * @return the bundle details
   */
  public DataBundle getBundle(String url) {
    return client.get(url, new TypeReference<DataBundle>() {
    });
  }

  /**
   * Create a new data bundle from a list of
   * <code>s3://bucket/key</code> URLs that point to ZIP or TAR archives
   * or Twitter JSON files that are already hosted on Amazon S3. Note
   * that all the files in a bundle must be the same type - one bundle
   * cannot contain a mixture of ZIP and TAR archives, for example. Job
   * parameters for the file encoding, MIME type, and the file
   * extensions used to filter the entries from the archives can also be
   * supplied, and will feed through to any annotation jobs that take
   * their input from the created bundle.
   * 
   * @param bundleName a name for the new bundle.
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
   * @param locations "URLs" of the form
   *          <code>s3://bucketname/key</code> denoting the target
   *          objects in Amazon S3
   * @return details of the newly created bundle.
   */
  public DataBundle createArchiveBundleFromS3(String bundleName,
          String accessKeyId, String secretKey, InputType inputType,
          String encoding, String mimeTypeOverride, String fileExtensions,
          String... locations) {
    if(inputType == InputType.ARC || inputType == InputType.WARC) {
      throw new RestClientException(
              "For ARC and WARC files use createARCBundleFromS3");
    }

    return createS3Bundle(bundleName, accessKeyId, secretKey, inputType,
            encoding, mimeTypeOverride, fileExtensions, null, locations);
  }

  /**
   * Create a new data bundle from a list of
   * <code>s3://bucket/key</code> URLs that point to ARC or WARC
   * archives that are already hosted on Amazon S3. Note that all the
   * files in a bundle must be the same type - one bundle cannot contain
   * a mixture of ARC and WARC archives, for example. Job parameters for
   * the file encoding, MIME type, and the MIME types used to filter the
   * entries from the archives can also be supplied, and will feed
   * through to any annotation jobs that take their input from the
   * created bundle.
   * 
   * @param bundleName a name for the new bundle.
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
   * @param mimeTypes space-separated list of MIME types used to filter
   *          the entries of interest from the ARC file. Entries whose
   *          MIME type does not match any of these will be ignored.
   * @param locations "URLs" of the form
   *          <code>s3://bucketname/key</code> denoting the target
   *          objects in Amazon S3
   * @return details of the newly created bundle.
   */
  public DataBundle createARCBundleFromS3(String bundleName,
          String accessKeyId, String secretKey, InputType inputType,
          String encoding, String mimeTypeOverride, String mimeTypes,
          String... locations) {
    if(inputType != InputType.ARC && inputType != InputType.WARC) {
      throw new RestClientException(
              "createARCBundleFromS3 only applicable to ARC and WARC types");
    }

    return createS3Bundle(bundleName, accessKeyId, secretKey, inputType,
            encoding, mimeTypeOverride, null, mimeTypes, locations);
  }

  /**
   * Common logic for creating bundles from S3.
   */
  protected DataBundle createS3Bundle(String bundleName, String accessKeyId,
          String secretKey, InputType inputType, String encoding,
          String mimeTypeOverride, String fileExtensions, String mimeTypes,
          String... locations) {
    ObjectNode request = JsonNodeFactory.instance.objectNode();

    request.put("name", bundleName);
    request.put("type", inputType.name());
    if(accessKeyId != null) {
      request.put("accessKey", accessKeyId);
      request.put("secretKey", secretKey);
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
    if(mimeTypes != null) {
      request.put("mimeTypeFilters", mimeTypes);
    }
    ArrayNode locationsArray = JsonNodeFactory.instance.arrayNode();
    for(String loc : locations) {
      locationsArray.add(loc);
    }
    request.put("locations", locationsArray);

    return client.post("data/bundle", new TypeReference<DataBundle>() {
    }, request);
  }

  /**
   * Create a new data bundle by uploading local ZIP or TAR archives or
   * Twitter JSON files to AnnoMarket managed storage. Note that all the
   * files in a bundle must be the same type - one bundle cannot contain
   * a mixture of ZIP and TAR archives, for example. Job parameters for
   * the file encoding, MIME type, and the file extensions used to
   * filter the entries from the archives can also be supplied, and will
   * feed through to any annotation jobs that take their input from the
   * created bundle.
   * 
   * @param bundleName a name for the new bundle.
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
   * @param localFiles the files to upload
   * @return details of the newly created bundle.
   */
  public DataBundle createArchiveBundleFromUploads(String bundleName,
          InputType inputType, String encoding, String mimeTypeOverride,
          String fileExtensions, File... localFiles) {
    if(inputType == InputType.ARC || inputType == InputType.WARC) {
      throw new RestClientException(
              "For ARC and WARC files use createARCBundleFromS3");
    }

    return uploadBundle(bundleName, inputType, encoding, mimeTypeOverride,
            fileExtensions, null, localFiles);
  }

  /**
   * Create a new data bundle by uploading local ARC or WARC archives to
   * AnnoMarket managed storage. Note that all the files in a bundle
   * must be the same type - one bundle cannot contain a mixture of ARC
   * and WARC archives, for example. Job parameters for the file
   * encoding, MIME type, and the MIME types used to filter the entries
   * from the archives can also be supplied, and will feed through to
   * any annotation jobs that take their input from the created bundle.
   * 
   * @param bundleName a name for the new bundle.
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
   * @param mimeTypes space-separated list of MIME types used to filter
   *          the entries of interest from the ARC file. Entries whose
   *          MIME type does not match any of these will be ignored.
   * @param localFiles the files to upload
   * @return details of the newly created bundle.
   */
  public DataBundle createARCBundleFromUploads(String bundleName,
          InputType inputType, String encoding, String mimeTypeOverride,
          String mimeTypes, File... localFiles) {
    if(inputType != InputType.ARC && inputType != InputType.WARC) {
      throw new RestClientException(
              "createARCBundleFromS3 only applicable to ARC and WARC types");
    }

    return uploadBundle(bundleName, inputType, encoding, mimeTypeOverride,
            null, mimeTypes, localFiles);
  }

  /**
   * Common logic for creating bundles from uploads.
   */
  protected DataBundle uploadBundle(String bundleName, InputType inputType,
          String encoding, String mimeTypeOverride, String fileExtensions,
          String mimeTypes, File... localFiles) {
    ObjectNode request = JsonNodeFactory.instance.objectNode();

    request.put("name", bundleName);
    request.put("type", inputType.name());
    if(encoding != null) {
      request.put("encoding", encoding);
    }
    if(mimeTypeOverride != null) {
      request.put("mimeTypeOverride", mimeTypeOverride);
    }
    if(fileExtensions != null) {
      request.put("fileExtensions", fileExtensions);
    }
    if(mimeTypes != null) {
      request.put("mimeTypeFilters", mimeTypes);
    }

    // create the initial bundle
    DataBundle bundle =
            client.post("data/bundle", new TypeReference<DataBundle>() {
            }, request);

    // if files provided, upload them and close the bundle
    if(localFiles != null && localFiles.length > 0) {
      // upload the files
      for(File file : localFiles) {
        bundle.addFile(file);
      }

      // close the bundle
      bundle.close();
    }

    return bundle;
  }

}
