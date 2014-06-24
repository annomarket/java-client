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
package com.annomarket.cli.commands.job;

import com.annomarket.cli.commands.AbstractCommand;
import com.annomarket.client.RestClient;
import com.annomarket.job.InputDetails;
import com.annomarket.job.Job;
import com.annomarket.job.JobManager;

public class CommonCrawl extends AbstractCommand {

  public void run(RestClient client, String... args) throws Exception {
    if(args.length < 3) {
      usage();
    }
    long jobId = -1;
    try {
      jobId = Long.parseLong(args[0]);
    } catch(NumberFormatException e) {
      System.err.println("Job ID must be a valid number");
      System.exit(1);
    }

    JobManager mgr = new JobManager(client);
    Job job = mgr.getJob(jobId);

    boolean async = false;
    String protocol = null;
    String hostname = null;
    String pathPrefix = null;
    int i = 1;
    for(i = 1; i < args.length && args[i].startsWith("-"); i++) {
      if("-async".equals(args[i])) {
        async = true;
      } else if("-protocol".equals(args[i])) {
        protocol = args[++i];
      } else if("-hostname".equals(args[i])) {
        hostname = args[++i];
      } else if("-pathPrefix".equals(args[i])) {
        pathPrefix = args[++i];
      } else {
        usage();
      }
    }
    
    if(hostname == null) {
      System.err.println("Hostname is required");
      usage();
    }
    final InputDetails input = job.addCommonCrawlInput(protocol, hostname, pathPrefix);
    System.out.println("Created input " + input.url);
    if(!async) {
      System.out.println("Starting search...");
      if(input.commonCrawl != null && input.commonCrawl.complete) {
        System.out.println("Search complete: " + input.commonCrawl.items + " item(s) found.");
        System.exit(0);
      }
      // poll for updates
      Thread shutdownHook = new Thread() {
        public void run() {
          System.err.println("Search interrupted");
          input.delete();
        }
      };
      Runtime.getRuntime().addShutdownHook(shutdownHook);
      long itemsSoFar = 0;
      while(!input.commonCrawl.complete) {
        Thread.sleep(2000);
        input.refresh();
        System.out.print(".");
        if(input.commonCrawl.items / 1000 > itemsSoFar / 1000) {
          System.out.print(input.commonCrawl.items);
        }
        itemsSoFar = input.commonCrawl.items;
      }
      Runtime.getRuntime().removeShutdownHook(shutdownHook);
      System.out.println();
      System.out.println("Search complete: " + input.commonCrawl.items + " item(s) found.");
    }
  }
  
  private void usage() {
    System.err.println("Usage: common-crawl <jobid> [options]");
    System.err.println();
    System.err.println("Required switches:");
    System.err.println("  -hostname <pattern> : a pattern representing the host name(s) to");
    System.err.println("              be selected from the crawl. This is allowed to contain");
    System.err.println("              at most one \"*\" character, which, if present, must");
    System.err.println("              be placed exactly before the first \".\". The pattern");
    System.err.println("              must contain at least two specified inter-dot segments");
    System.err.println("              (e.g. \"*.com\" is not permitted as it would match too");
    System.err.println("              many entries).");
    System.err.println("Optional switches:");
    System.err.println("  -protocol <protocol>: either \"http\" or \"https\".  If omitted,");
    System.err.println("              both http and https pages will be returned.");
    System.err.println("  -pathPrefix <prefix>: a pattern which may contain \"*\" characters");
    System.err.println("              that is matched against the path part of the URL for");
    System.err.println("              the crawled items. Only crawled items for which the");
    System.err.println("              path starts as specified will be returned.  If omitted,");
    System.err.println("              all items that match the hostname will be returned.");
    System.err.println("  -async:     Return immediately the search starts.  By default this");
    System.err.println("              command blocks until the search completes, and you can");
    System.err.println("              interrupt the search by pressing CTRL-C.");

    System.exit(1);
  }

}
