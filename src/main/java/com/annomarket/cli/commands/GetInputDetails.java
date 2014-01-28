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
package com.annomarket.cli.commands;

import com.annomarket.client.RestClient;
import com.annomarket.job.InputDetails;
import com.annomarket.job.JobManager;

public class GetInputDetails extends AbstractCommand {

  public void run(RestClient client, String... args) throws Exception {
    if(args.length < 1) {
      System.err.println("Usage: input-details <inputurl>");
      System.exit(1);
    }

    JobManager mgr = new JobManager(client);
    InputDetails input = mgr.getInputDetails(args[0]);
    renderInput(input);
  }

  private void renderInput(InputDetails input) {
    System.out.println("               URL: " + input.url);
    if(input.commonCrawl != null && input.commonCrawl.hostname != null) {
      // this is a common crawl input
      System.out.println("Common Crawl search parameters:");
      System.out.println("     Protocol: "
              + (input.commonCrawl.protocol == null
                      ? "<any>"
                      : input.commonCrawl.protocol));
      System.out.println("     Hostname: " + input.commonCrawl.hostname);
      System.out.println("  Path prefix: "
              + (input.commonCrawl.pathPrefix == null
                      ? "<any>"
                      : input.commonCrawl.pathPrefix));
      System.out.println(input.commonCrawl.items
              + " found "
              + (input.commonCrawl.complete
                      ? "(search complete)"
                      : "so far (still searching)"));
    } else {
      // not common crawl
      System.out.println("              Type: " + input.type);
      System.out.println("          Location: " + input.location);
      if(input.encoding != null) {
        System.out.println("          Encoding: " + input.encoding);
      }
      if(input.mimeTypeOverride != null) {
        System.out.println("MIME type override: " + input.type);
      }
      if(input.fileExtensions != null) {
        System.out.println("   File extensions: " + input.fileExtensions);
      }
      if(input.mimeTypes != null) {
        System.out.println(" MIME type filters: " + input.mimeTypes);
      }
    }
  }

}
