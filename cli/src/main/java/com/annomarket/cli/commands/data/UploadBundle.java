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
package com.annomarket.cli.commands.data;

import java.io.File;

import com.annomarket.cli.commands.AbstractCommand;
import com.annomarket.client.RestClient;
import com.annomarket.data.DataBundle;
import com.annomarket.data.DataManager;
import com.annomarket.common.InputType;

public class UploadBundle extends AbstractCommand {

  public void run(RestClient client, String... args) throws Exception {
    if(args.length < 3) {
      usage();
    }

    String bundleName = args[0];

    DataManager mgr = new DataManager(client);

    InputType inputType = null;
    String encoding = null;
    String mimeTypeOverride = null;
    String fileExtensions = null;
    String mimeTypes = null;

    int i;
    for(i = 1; i + 1 < args.length && args[i].startsWith("-"); i++) {
      if("-type".equals(args[i])) {
        try {
          inputType = InputType.valueOf(args[++i]);
        } catch(IllegalArgumentException e) {
          System.err.println("Unrecognised input type.");
          usage();
        }
      } else if("-encoding".equals(args[i])) {
        encoding = args[++i];
      } else if("-mimeTypeOverride".equals(args[i])) {
        mimeTypeOverride = args[++i];
      } else if("-fileExtensions".equals(args[i])) {
        fileExtensions = args[++i];
      } else if("-mimeTypes".equals(args[i])) {
        mimeTypes = args[++i];
      } else {
        usage();
      }
    }

    if(inputType == null) {
      System.err.println("Input type must be specified");
      usage();
    }

    DataBundle newBundle = null;
    if(inputType == InputType.ARC || inputType == InputType.WARC) {
      newBundle =
              mgr.createARCBundleFromUploads(bundleName, inputType, encoding,
                      mimeTypeOverride, mimeTypes);
    } else {
      newBundle =
              mgr.createArchiveBundleFromUploads(bundleName, inputType,
                      encoding, mimeTypeOverride, fileExtensions);
    }
    System.out.println("Created bundle " + newBundle.id);

    for(; i < args.length; i++) {
      System.out.println("Uploading " + args[i]);
      newBundle.addFile(new File(args[i]));
    }
    System.out.println("Closing bundle...");
    newBundle.close();
    System.out.println("Done");
  }

  private void usage() {
    System.err
            .println("Usage: upload-bundle \"bundle name\" -type <type> [options] file1 file2 ...");
    System.err.println();
    System.err.println("Required switches:");
    System.err
            .println("  -type <type> : the type of the files to upload, must be one of");
    System.err
            .println("              ZIP, TAR, ARC, WARC, TWITTER_SEARCH, TWITTER_STREAM");
    System.err.println("Optional switches:");
    System.err
            .println("  -encoding : Character encoding to use when loading documents");
    System.err
            .println("              from the archive (if omitted, ARC files will use the");
    System.err
            .println("              encoding specified by the ARC entry, other files");
    System.err.println("              will use UTF-8)");
    System.err
            .println("  -mimeTypeOverride: The MIME type of the entries in the archive");
    System.err
            .println("              (if omitted, the type will be guessed from the file");
    System.err
            .println("              name extension and/or the ARC record mime type)");
    System.err
            .println("  -mimeTypeFilters: (ARC and WARC inputs only) space-separated");
    System.err
            .println("              list of MIME types - entries that are not of one of");
    System.err.println("              these types will be ignored");
    System.err
            .println("  -fileExtensions: (ZIP and TAR inputs only) comma-separated list");
    System.err
            .println("              of file extensions - ZIP/TAR entries that do not have");
    System.err
            .println("              one of these extensions will be ignored.");

    System.exit(1);
  }

}
