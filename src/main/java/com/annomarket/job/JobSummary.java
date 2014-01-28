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
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Summary information about a single annotation job.
 * 
 * @author Ian Roberts
 */
public class JobSummary extends ApiObject {

  /**
   * The job's detail URL.
   */
  public String url;

  /**
   * Current state of the job.
   */
  public JobState state;

  /**
   * Fetch the full details of this job.
   * 
   * @return a {@link Job} object with the full job details.
   */
  public Job details() {
    Job details = client.get(url, new TypeReference<Job>() {
    });
    details.url = this.url;
    return details;
  }

}
