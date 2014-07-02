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

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import com.annomarket.cli.commands.AbstractCommand;
import com.annomarket.client.RestClient;
import com.annomarket.client.RestClientException;
import com.annomarket.job.JobResult;

public class Download extends AbstractCommand {

  public void run(RestClient client, String... args) throws Exception {
    if(args.length < 1) {
      System.err.println("Usage: download <URL>");
      System.exit(1);
    }
    JobResult res = new JobResult(new URL(args[0]));
    res.setClient(client);
    String path = res.url.getPath();
    String filename = path.substring(path.lastIndexOf("/") + 1);
    System.out.println(filename);
    File f = new File(filename);
    try {
      FileUtils.copyURLToFile(res.urlToDownload(), f);
    } catch(IOException e) {
      throw new RestClientException("Error downloading " + res.url);
    }
  }

}
