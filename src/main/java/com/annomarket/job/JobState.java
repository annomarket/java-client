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
package com.annomarket.job;

/**
 * Enumeration of valid job states.
 * 
 * @author Ian Roberts
 */
public enum JobState {
  /**
   * Job has been reserved but is not yet fully configured (e.g. it has
   * no input or output specifications)
   */
  RESERVED,

  /**
   * Job is fully configured and ready to be started.
   */
  READY,

  /**
   * Job is running.
   */
  ACTIVE,

  /**
   * Job has completed running (successfully or unsuccessfully).
   */
  COMPLETED,

  /**
   * Job has been deleted and can no longer be run.
   */
  DELETED,

  /**
   * Job has been automatically suspended due to your account reaching
   * its credit limit. Once more credit is available the job may be
   * resumed.
   */
  SUSPENDED,
}
