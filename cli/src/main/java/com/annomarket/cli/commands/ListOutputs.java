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

import java.util.List;

import com.annomarket.client.RestClient;
import com.annomarket.job.Job;
import com.annomarket.job.JobManager;
import com.annomarket.job.Output;
import com.annomarket.job.OutputType;

public class ListOutputs extends AbstractCommand {

  public void run(RestClient client, String... args) throws Exception {
    if(args.length < 1) {
      System.err.println("Usage: list-inputs <jobid>");
      System.exit(1);
    }
    long jobId = -1;
    try {
      jobId = Long.parseLong(args[0]);
    } catch(NumberFormatException e) {
      System.err.println("Job ID must be a valid number");
      System.exit(1);
    }

    JobManager mgr = new JobManager(client);
    Job j = mgr.getJob(jobId);
    List<Output> outputs = j.listOutputs();
    if(outputs == null || outputs.isEmpty()) {
      System.out.println("No outputs found");
    } else {
      System.out.println(outputs.size() + " output(s) found");
      for(Output o : outputs) {
        System.out.println();
        System.out.println("          Detail URL: " + o.url);
        System.out.println("                Type: " + o.type);
        if(o.type == OutputType.MIMIR) {
          System.out.println("           Index URL: " + o.indexUrl);
          if(o.username != null) {
            System.out.println("            Username: " + o.username);
          }
        } else {
          System.out.println("      File extension: " + o.fileExtension);
          System.out.println("Annotation selectors: " + o.annotationSelectors);
        }
      }
    }
  }

}
