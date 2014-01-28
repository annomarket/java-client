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
import com.annomarket.job.OutputType;

public class AddFileOutput extends AbstractCommand {

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
    
    OutputType outputType = null;
    String fileExtension = null;
    String annotationSelectors = null;
    
    int i;
    for(i = 1; i+1 < args.length && args[i].startsWith("-"); i++) {
      if("-type".equals(args[i])) {
        try {
          outputType = OutputType.valueOf(args[++i]);
        } catch(IllegalArgumentException e) {
          System.err.println("Unrecognised output type.");
          usage();
        }
        if(outputType == OutputType.MIMIR) {
          System.err.println("Use add-mimir-output for Mimir outputs");
          usage();
        }
      } else if("-extension".equals(args[i])) {
        fileExtension = args[++i];
      } else if("-annotationSelectors".equals(args[i])) {
        annotationSelectors = args[++i];
      } else {
        usage();
      }
    }
    
    if(outputType == null) {
      System.err.println("Output type must be specified");
      usage();
    }
    if(fileExtension == null) {
      System.err.println("File extension must be specified");
      usage();
    }

    Output output = job.addFileOutput(outputType, fileExtension, annotationSelectors);
    System.out.println("Created output " + output.url);
  }

  private void usage() {
    System.err.println("Usage: add-file-output <jobid> -type <type> [options]");
    System.err.println();
    System.err.println("Required switches:");
    System.err.println("  -type <type> : the type of the output definition, must be one of");
    System.err.println("                 GATE_XML, INLINE_XML, XCES or JSON");
    System.err.println("  -extension <.ext> : the file extension to use for each generated");
    System.err.println("                 document file, e.g. \".GATE.xml\" or \".Person.xml\"");
    System.err.println("Optional switches:");
    System.err.println("  -annotationSelectors : Comma-separated annotation selector");
    System.err.println("              expressions to select which annotations to include.");
    System.err.println("              Each expression takes the form setName:type, where");
    System.err.println("              an empty set name means the default set and an empty");
    System.err.println("              type means all annotations from that set (so \":\" on its");
    System.err.println("              own means all annotation from the default set).");
    System.err.println("              If omitted the pipeline's default selectors will be used.");
    
    System.exit(1);
  }

}
