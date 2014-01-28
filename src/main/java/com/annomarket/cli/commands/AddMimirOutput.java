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
import com.annomarket.job.Job;
import com.annomarket.job.JobManager;
import com.annomarket.job.Output;

public class AddMimirOutput extends AbstractCommand {

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
    
    String indexUrl = null;
    String username = null;
    String password = null;
    
    int i;
    for(i = 1; i+1 < args.length && args[i].startsWith("-"); i++) {
      if("-indexUrl".equals(args[i])) {
        indexUrl = args[++i];
      } else if("-username".equals(args[i])) {
        username = args[++i];
      } else if("-password".equals(args[i])) {
        password = args[++i];
      } else {
        usage();
      }
    }
    
    if(indexUrl == null) {
      System.err.println("Index URL must be specified");
      usage();
    }

    Output output = job.addMimirOutput(indexUrl, username, password);
    System.out.println("Created output " + output.url);
  }

  private void usage() {
    System.err.println("Usage: add-mimir-output <jobid> -indexUrl <url> [options]");
    System.err.println();
    System.err.println("Required switches:");
    System.err.println("  -indexUrl <url> : the URL of the target Mimir index.");
    System.err.println("Optional switches:");
    System.err.println("  -username : Username to use when communicating with the Mimir");
    System.err.println("              server.  If omitted, requests are not authenticated.");
    System.err.println("  -password : Password to use when communicating with the Mimir");
    System.err.println("              server.  If omitted, requests are not authenticated.");
    
    System.exit(1);
  }

}
