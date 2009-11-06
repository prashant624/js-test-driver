/*
 * Copyright 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.jstestdriver.coverage;

import com.google.inject.Inject;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.hooks.FileLoadPostProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Instruments the javascript code found in the FileInfo.
 * 
 * @author corysmith@google.com (Cory Smith)
 * 
 */
public class CoverageInstrumentingProcessor implements FileLoadPostProcessor {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(CoverageInstrumentingProcessor.class);
  private final CodeCoverageDecorator decorator;
  private final Set<String> excludes;

  @Inject
  public CoverageInstrumentingProcessor(CodeCoverageDecorator decorator,
                                        @Coverage("coverageExcludes") Set<String> excludes) {
    this.decorator = decorator;
    this.excludes = excludes;
  }

  public FileInfo process(FileInfo file) {
    if (file.getFileName().contains("LCOV.js") || !file.canLoad()
      || file.isServeOnly() || excludes.contains(file.getFileName())) {
      return file;
    }
    LOGGER.info("Generating coverage for " + file.getFileName());
    String instrumented = decorator.decorate(new Code(file.getFileName(),
                                                      file.getData()));
    return new FileInfo(file.getFileName(),
                        file.getTimestamp(),
                        file.isPatch(),
                        file.isServeOnly(),
                        instrumented);
  }
}
