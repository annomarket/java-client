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

import java.util.Formatter;
import java.util.List;

import com.annomarket.cli.commands.AbstractCommand;
import com.annomarket.client.RestClient;
import com.annomarket.job.Job;
import com.annomarket.job.JobManager;
import com.annomarket.job.JobState;
import com.annomarket.job.JobSummary;

public class ListJobs extends AbstractCommand {

  public void run(RestClient client, String... args) throws Exception {
    JobManager mgr = new JobManager(client);
    JobState[] states = new JobState[args.length];
    for(int i = 0; i < args.length; i++) {
      try {
        states[i] = JobState.valueOf(args[i]);
      } catch(IllegalArgumentException e) {
        System.err.println("Invalid job state " + args[i]);
        System.exit(1);
      }
    }
    List<JobSummary> jobs = mgr.listJobs(states);
    if(jobs == null || jobs.isEmpty()) {
      System.out.println("No jobs found");
    } else {
      // ID (6 cols), Name (32 cols), price (rest)
      System.out.println("    ID  Name                                      State");
      System.out.println("----------------------------------------------------------");
      Formatter f = new Formatter(System.out);
      for(JobSummary summ : jobs) {
        Job j = summ.details();
        String name = j.name;
        if(name.length() > 40) {
          name = name.substring(0,37) + "...";
        }
        f.format("%6d  %-40s  %s%n", j.id, name, j.state);
      }
      f.close();
    }
  }

}
