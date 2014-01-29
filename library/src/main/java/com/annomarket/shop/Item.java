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
package com.annomarket.shop;

import java.util.ArrayList;
import java.util.List;

import com.annomarket.common.ApiObject;
import com.annomarket.common.Prices;
import com.annomarket.job.Job;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * "Struct" class to represent a single shop item.
 */
public class Item extends ApiObject {

  /**
   * The item ID.
   */
  public long id;

  /**
   * The item name.
   */
  public String name;

  /**
   * A short fragment of HTML describing the item.
   */
  public String shortDescription;

  /**
   * The URL to query for further details, or to POST to reserve a job.
   */
  public String detailUrl;

  /**
   * The pricing structure for this item.
   */
  public Prices price;

  /**
   * URL to process documents using the online API.
   */
  public String onlineUrl;

  /**
   * Reserve a number of annotation jobs based on this pipeline.
   * 
   * @param quantity the number of jobs to reserve
   * @param allowPayment permit payment if the item has an upfront cost.
   * @return one {@link Job} object per reserved job.
   */
  public List<Job> reserve(int quantity, boolean allowPayment) {
    String[] extraHeaders =
            (allowPayment ? new String[] {"X-AnnoMarket-Payment", "OK"} : null);
    ObjectNode request = JsonNodeFactory.instance.objectNode();
    request.put("quantity", quantity);
    ReserveResponse response =
            client.request(detailUrl, "POST",
                    new TypeReference<ReserveResponse>() {
                    }, request, extraHeaders);
    List<Job> jobs = new ArrayList<Job>();
    for(String jobUrl : response.jobs) {
      Job job = client.get(jobUrl, new TypeReference<Job>() {
      });
      job.url = jobUrl;
      jobs.add(job);
    }
    return jobs;
  }

  /**
   * Reserve a number of annotation jobs based on this pipeline. If the
   * item has an upfront cost this call will fail.
   * 
   * @param quantity the number of jobs to reserve
   * @return one {@link Job} object per reserved job.
   */
  public List<Job> reserve(int quantity) {
    return reserve(quantity, false);
  }

  /**
   * Reserve one annotation job based on this pipeline.
   * 
   * @param allowPayment permit payment if the item has an upfront cost.
   * @return The reserved {@link Job}.
   */
  public Job reserve(boolean allowPayment) {
    List<Job> jobs = reserve(1, allowPayment);
    if(jobs == null || jobs.isEmpty()) {
      return null;
    } else {
      return jobs.get(0);
    }
  }

  /**
   * Reserve one job based on this pipeline. If the item has an upfront
   * cost this call will fail.
   * 
   * @return the reserved {@link Job}
   */
  public Job reserve() {
    return reserve(false);
  }

  /**
   * DTO representing the response of the "reserve" call.
   */
  private static class ReserveResponse {
    @SuppressWarnings("unused")
    public String status;

    public List<String> jobs;
  }
}
