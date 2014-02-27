package com.annomarket.online;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.annomarket.client.RestClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class OnlineApi {

  /**
   * The standard base URI for the AnnoMarket Online API.
   */
  public static final URL DEFAULT_BASE_URL;
  static {
    try {
      DEFAULT_BASE_URL =
              new URL("https://api.annomarket.com/online-processing/item/");
    } catch(MalformedURLException e) {
      // can't happen
      throw new ExceptionInInitializerError(e);
    }
  }

  private RestClient client;

  /**
   * Construct an <code>OnlineApi</code> using the given
   * {@link RestClient} to communicate with the API.
   * 
   * @param client the client object used for communication
   */
  public OnlineApi(RestClient client) {
    this.client = client;
  }

  /**
   * Construct an <code>OnlineApi</code> accessing the AnnoMarket.com
   * public API with the given credentials.
   * 
   * @param apiKeyId API key ID for authentication
   * @param apiPassword corresponding password
   */
  public OnlineApi(String apiKeyId, String apiPassword) {
    this(new RestClient(DEFAULT_BASE_URL, apiKeyId, apiPassword));
  }

  public InputStream annotateDocumentAsStream(String itemOrEndpoint,
          String documentText, String mimeType, String annotationSelectors,
          ResponseFormat format) {
    ObjectNode request = JsonNodeFactory.instance.objectNode();
    request.put("document", documentText);
    request.put("mimeType", mimeType);
    if(annotationSelectors != null) {
      request.put("annotationSelectors", annotationSelectors);
    }
    return client.requestForStream(itemOrEndpoint, "POST", request, "Accept",
            format.acceptHeader);
  }

  public AnnotatedDocument annotateDocument(String itemOrEndpoint,
          String documentText, String mimeType, String annotationSelectors) {
    ObjectNode request = JsonNodeFactory.instance.objectNode();
    request.put("document", documentText);
    request.put("mimeType", mimeType);
    if(annotationSelectors != null) {
      request.put("annotationSelectors", annotationSelectors);
    }
    return client.request(itemOrEndpoint, "POST",
            new TypeReference<AnnotatedDocument>() {
            }, request, "Accept", ResponseFormat.JSON.acceptHeader);
  }

}
