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
import com.annomarket.job.JobManager;
import com.annomarket.job.Output;

public class DeleteOutput extends AbstractCommand {

  public void run(RestClient client, String... args) throws Exception {
    if(args.length < 1) {
      System.err.println("Usage: delete-output <outputurl>");
      System.exit(1);
    }

    JobManager mgr = new JobManager(client);
    Output output = mgr.getOutputDetails(args[0]);
    output.delete();
    System.out.println("Output deleted successfully");
  }

}
