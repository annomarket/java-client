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
package com.annomarket.client;

import com.fasterxml.jackson.databind.JsonNode;


/**
 * Exception representing any problem communicating with the AnnoMarket
 * REST API. In cases where the server could successfully be contacted
 * but it returned an error response (4xx or 5xx) with a JSON response
 * body, that response will be available as a {@link JsonNode} from the
 * {@link #getResponse()} method.
 * 
 * @author Ian Roberts
 */
public class RestClientException extends RuntimeException {

  private static final long serialVersionUID = -7227598956545774156L;

  /**
   * The JSON error response message, if applicable.
   */
  private JsonNode response;

  public RestClientException() {
  }

  public RestClientException(String message) {
    super(message);
  }

  public RestClientException(Throwable cause) {
    super(cause);
  }

  public RestClientException(String message, Throwable cause) {
    super(message, cause);
  }

  public RestClientException(String message, JsonNode response) {
    super(message);
    this.response = response;
  }

  /**
   * If this exception resulted from a 4xx or 5xx error response from
   * the server, this method provides access to the response body.
   * 
   * @return the error response body as a Jackson {@link JsonNode}, or
   *         <code>null</code> if no response is available (e.g. if this
   *         exception wraps an IOException rather than representing an
   *         error response from the server).
   */
  public JsonNode getResponse() {
    return response;
  }

}
