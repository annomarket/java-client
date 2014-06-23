package com.annomarket.data;

import com.annomarket.common.ApiObject;
import com.fasterxml.jackson.core.type.TypeReference;

public class DataBundleSummary extends ApiObject {

  /**
   * The job's numeric identifier.
   */
  public long id;

  /**
   * The name of the job.
   */
  public String name;

  /**
   * The bundle's detail URL.
   */
  public String url;

  /**
   * Can the contents of this bundle be directly downloaded?
   */
  public boolean downloadable;

  /**
   * Has this bundle been closed? Only non-closed bundles can accept
   * further file uploads.
   */
  public boolean closed;

  /**
   * Fetch the full details of this bundle.
   * 
   * @return a {@link DataBundle} object with the full bundle details.
   */
  public DataBundle details() {
    DataBundle details = client.get(url, new TypeReference<DataBundle>() {
    });
    details.url = this.url;
    return details;
  }

}
