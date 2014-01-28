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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import com.annomarket.client.RestClient;
import com.annomarket.client.RestClientException;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Main entry point to the "shop" API to browse the available pipelines
 * or request a specific pipeline by ID.
 * 
 * @author Ian Roberts
 */
public class Shop {

  private RestClient client;

  /**
   * Construct a <code>Shop</code> using the given {@link RestClient} to
   * communicate with the API.
   * 
   * @param client the client object used for communication
   */
  public Shop(RestClient client) {
    this.client = client;
  }

  /**
   * Construct a <code>Shop</code> accessing the AnnoMarket.com public
   * API with the given credentials.
   * 
   * @param apiKeyId API key ID for authentication
   * @param apiPassword corresponding password
   */
  public Shop(String apiKeyId, String apiPassword) {
    this(new RestClient(apiKeyId, apiPassword));
  }

  /**
   * List the available "items", i.e. pipelines available for use as
   * annotation jobs.
   * 
   * @param tags tags to filter the list - only items with all the
   *          specified tags will be returned
   * @return a List of matching items
   */
  public List<Item> listItems(String... tags) throws RestClientException {
    StringBuilder urlBuilder = new StringBuilder("shop");
    if(tags != null && tags.length > 0) {
      try {
        urlBuilder.append("?tag=");
        urlBuilder.append(URLEncoder.encode(tags[0], "UTF-8"));
        for(int i = 1; i < tags.length; i++) {
          urlBuilder.append("&tag=");
          urlBuilder.append(URLEncoder.encode(tags[i], "UTF-8"));
        }
      } catch(UnsupportedEncodingException e) {
        // shouldn't happen
        throw new RuntimeException("JVM claims not to support UTF-8...", e);
      }
    }
    return client.get(urlBuilder.toString(), new TypeReference<List<Item>>() {
    });
  }

  /**
   * Fetch a specific item by ID.
   * 
   * @param id the ID of the item
   * @return the requested item
   */
  public Item getItem(long id) throws RestClientException {
    return client.get("shop/item/" + id, new TypeReference<Item>() {
    });
  }

  /**
   * Fetch a specific item by URL (e.g. copied from the shop web page).
   * 
   * @param url the URL of the item
   * @return the requested item
   */
  public Item getItem(String url) throws RestClientException {
    return client.get(url, new TypeReference<Item>() {
    });
  }

}
