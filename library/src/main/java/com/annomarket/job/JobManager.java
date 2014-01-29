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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import com.annomarket.client.RestClient;
import com.annomarket.client.RestClientException;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Main entry point to the job management API to manage annotation jobs
 * you have reserved.
 * 
 * @author Ian Roberts
 */
public class JobManager {

  private RestClient client;

  /**
   * Construct a <code>JobManager</code> using the given
   * {@link RestClient} to communicate with the API.
   * 
   * @param client the client object used for communication
   */
  public JobManager(RestClient client) {
    this.client = client;
  }

  /**
   * Construct a <code>JobManager</code> accessing the AnnoMarket.com
   * public API with the given credentials.
   * 
   * @param apiKeyId API key ID for authentication
   * @param apiPassword corresponding password
   */
  public JobManager(String apiKeyId, String apiPassword) {
    this(new RestClient(apiKeyId, apiPassword));
  }

  /**
   * List all the jobs owned by the authenticated user that are in the
   * specified states.
   * 
   * @param states states of interest. If not specified, the default is
   *          to return RESERVED, READY, ACTIVE and COMPLETED jobs.
   * @return list of {@link JobSummary} objects representing the
   *         matching jobs - call the <code>details</code> method to get
   *         the full detail (which requires another API call).
   */
  public List<JobSummary> listJobs(JobState... states)
          throws RestClientException {
    StringBuilder urlBuilder = new StringBuilder("job");
    if(states != null && states.length > 0) {
      try {
        urlBuilder.append("?state=");
        urlBuilder.append(URLEncoder.encode(states[0].name(), "UTF-8"));
        for(int i = 1; i < states.length; i++) {
          urlBuilder.append("&state=");
          urlBuilder.append(URLEncoder.encode(states[i].name(), "UTF-8"));
        }
      } catch(UnsupportedEncodingException e) {
        // shouldn't happen
        throw new RuntimeException("JVM claims not to support UTF-8...", e);
      }
    }
    return client.get(urlBuilder.toString(),
            new TypeReference<List<JobSummary>>() {
            });
  }

  /**
   * Get an individual job by ID.
   * 
   * @param id the ID of the job
   * @return the details of the requested job.
   */
  public Job getJob(long id) throws RestClientException {
    Job j = client.get("job/" + id, new TypeReference<Job>() {
    });
    j.url = client.getBaseUrl().toString() + "job/" + id;
    return j;
  }

  /**
   * Fetch details of a specific job input specification given its
   * detail URL.
   * 
   * @param url the detail URL as returned by an earlier API call
   * @return the requested input specification.
   */
  public InputDetails getInputDetails(String url) throws RestClientException {
    return client.get(url, new TypeReference<InputDetails>() {
    });
  }

  /**
   * Fetch details of a specific job output specification given its
   * detail URL.
   * 
   * @param url the detail URL as returned by an earlier API call
   * @return the requested output specification.
   */
  public Output getOutputDetails(String url) throws RestClientException {
    return client.get(url, new TypeReference<Output>() {
    });
  }
}
