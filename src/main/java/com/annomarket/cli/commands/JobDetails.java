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
import com.annomarket.job.JobState;

public class JobDetails extends AbstractCommand {

  public void run(RestClient client, String... args) throws Exception {
    if(args.length < 1) {
      System.err.println("Usage: job-details <jobid>");
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
    renderJob(j);
  }

  private void renderJob(Job j) {
    System.out.println("             ID: " + j.id);
    System.out.println("           Name: " + j.name);
    System.out.println("          State: " + j.state);
    System.out.println("          Price: " + formatPrices(j.price));
    System.out.println("   Date created: " + j.dateCreated);
    if(j.state == JobState.COMPLETED) {
      System.out.println(" Date completed: " + j.dateCompleted);
      System.out.println("    Exipry date: " + j.resultsAvailableUntil);
    }
    if(j.timeUsed > 0) {
      System.out.println("Processing time: " + formatMs(j.timeUsed)
              + " (charged " + formatMs(j.timeCharged) + " so far)");
    }
    if(j.bytesUsed > 0) {
      System.out.println(" Data processed: " + formatBytes(j.bytesUsed)
              + " (charged " + formatBytes(j.bytesCharged) + " so far)");
    }
    if(j.progress > 0) {
      System.out.println("   Job progress: " + formatPercent(j.progress));
    }

  }

}
