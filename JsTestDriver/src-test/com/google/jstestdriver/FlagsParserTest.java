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

package com.google.jstestdriver;

import junit.framework.TestCase;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/**
 * @author corysmith
 *
 */
public class FlagsParserTest extends TestCase {
  public void testParseList() throws Exception {
    Flags flags = new FlagsParser().parseArgument(new String[]{"--tests", "foo,bar,baz"});
    assertEquals(Arrays.asList("foo", "bar", "baz"), flags.getTests());
  }
  public void testParseListTrailingWhiteSpace() throws Exception {
    Flags flags = new FlagsParser().parseArgument(new String[]{"--tests", "foo, bar,\nbaz"});
    assertEquals(Arrays.asList("foo", "bar", "baz"), flags.getTests());
  }
  public void testParseListWithSlash() throws Exception {
    Flags flags = new FlagsParser().parseArgument(new String[]{"--browser", "/path/browser,/beep"});
    assertEquals(Arrays.asList("/path/browser", "/beep"), flags.getBrowser());
  }
  public void testParseListWithSlashAndComma() throws Exception {
    Flags flags = new FlagsParser().parseArgument(new String[]{"--browser",
        "open,/Applications/Google\\ Chrome.app/Contents/MacOS/Google\\ Chrome"});
    assertEquals(Arrays.asList("open", "/Applications/Google\\ Chrome.app/" +
        "Contents/MacOS/Google\\ Chrome"), flags.getBrowser());
  }

  public void testParseListWindowsOpts() throws Exception {
    Flags flags = new FlagsParser().parseArgument(new String[]{"--browser",
      "C:\\Program Files\\Mozilla Firefox\\firefox.exe," +
      "C:\\Program Files\\Safari\\Safari.exe," +
      "C:\\Program Files\\Internet Explorer\\iexplore.exe," +
      "C:\\Documents and Settings\\Misko\\Local Settings" +
      "\\Application Data\\Google\\Chrome\\Application\\chrome.exe"});
    assertEquals(Arrays.asList("C:\\Program Files\\Mozilla Firefox\\firefox.exe",
      "C:\\Program Files\\Safari\\Safari.exe",
      "C:\\Program Files\\Internet Explorer\\iexplore.exe",
      "C:\\Documents and Settings\\Misko\\Local Settings" +
      "\\Application Data\\Google\\Chrome\\Application\\chrome.exe"), flags.getBrowser());
  }
  public void testParseInteger() throws Exception {
    Flags flags = new FlagsParser().parseArgument(new String[]{"--port", "4504"});
    assertEquals(new Integer(4504), flags.getPort());
  }
  public void testNoArgs() throws Exception {
    try{
      new FlagsParser().parseArgument(new String[]{});
      fail("expected instructions");
    } catch (CmdLineException e) {
      ByteArrayOutputStream message = new ByteArrayOutputStream();
      new CmdLineParser(new FlagsImpl()).printUsage(message);
      assertEquals(message.toString(), e.getMessage());
    }
  }
  public void testBadArgs() throws Exception {
    String[] args = new String[]{"--port"};
    try{
      new FlagsParser().parseArgument(args);
      fail("expected instructions");
    } catch (CmdLineException e) {
      ByteArrayOutputStream message = new ByteArrayOutputStream();
      new CmdLineParser(new FlagsImpl()).printUsage(message);
      assertEquals("Option \"--port\" takes an operand\n" + message.toString(), e.getMessage());
    }
  }
}
