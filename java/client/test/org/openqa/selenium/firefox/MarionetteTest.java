// Licensed to the Software Freedom Conservancy (SFC) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The SFC licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.openqa.selenium.firefox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.openqa.selenium.testing.Driver.FIREFOX;

import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.testing.Ignore;
import org.openqa.selenium.testing.JUnit4TestBase;

import java.io.IOException;
import java.util.Optional;

@Ignore(FIREFOX)
public class MarionetteTest extends JUnit4TestBase {

  private FirefoxDriver localDriver;

  @After
  public void quitDriver() {
    if (localDriver != null) {
      localDriver.quit();
    }
  }

  @Test
  public void canStartDriverWithEmptyOptions() {
    localDriver = new FirefoxDriver(new FirefoxOptions());
    verifyItIsMarionette(localDriver);
  }

  @Test
  public void canStartDriverWithNoParameters() {
    localDriver = new FirefoxDriver();
    verifyItIsMarionette(localDriver);
  }

  @Test
  public void canStartDriverWithSpecifiedBinary() throws IOException {
    FirefoxBinary binary = spy(new FirefoxBinary());

    localDriver = new FirefoxDriver(new FirefoxOptions().setBinary(binary));

    verifyItIsMarionette(localDriver);
    verify(binary, atLeastOnce()).getPath();
    verify(binary, never()).startFirefoxProcess(any());
  }

  @Test
  public void canStartDriverWithSpecifiedProfile() {
    FirefoxProfile profile = new FirefoxProfile();
    profile.setPreference("browser.startup.page", 1);
    profile.setPreference("browser.startup.homepage", pages.xhtmlTestPage);

    localDriver = new FirefoxDriver(new FirefoxOptions().setProfile(profile));
    wait.until($ -> "XHTML Test Page".equals(localDriver.getTitle()));

    verifyItIsMarionette(localDriver);
  }

  @Test
  public void canStartDriverWithSpecifiedBinaryAndProfile() throws IOException {
    FirefoxBinary binary = spy(new FirefoxBinary());

    FirefoxProfile profile = new FirefoxProfile();
    profile.setPreference("browser.startup.page", 1);
    profile.setPreference("browser.startup.homepage", pages.xhtmlTestPage);

    localDriver = new FirefoxDriver(new FirefoxOptions().setBinary(binary).setProfile(profile));
    wait.until($ -> "XHTML Test Page".equals(localDriver.getTitle()));

    verifyItIsMarionette(localDriver);
    verify(binary, atLeastOnce()).getPath();
    verify(binary, never()).startFirefoxProcess(any());
  }

  @Test
  public void canPassCapabilities() {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setCapability(CapabilityType.PAGE_LOAD_STRATEGY, "none");

    localDriver = new FirefoxDriver(capabilities);

    verifyItIsMarionette(localDriver);
    assertEquals(
        localDriver.getCapabilities().getCapability(CapabilityType.PAGE_LOAD_STRATEGY), "none");
  }

  @Test
  public void canSetPreferencesInFirefoxOptions() {
    FirefoxOptions options = new FirefoxOptions()
      .addPreference("browser.startup.page", 1)
      .addPreference("browser.startup.homepage", pages.xhtmlTestPage);

    localDriver = new FirefoxDriver(options);
    wait.until($ -> "XHTML Test Page".equals(localDriver.getTitle()));

    verifyItIsMarionette(localDriver);
  }

  @Test
  public void canSetProfileInFirefoxOptions() {
    FirefoxProfile profile = new FirefoxProfile();
    profile.setPreference("browser.startup.page", 1);
    profile.setPreference("browser.startup.homepage", pages.xhtmlTestPage);

    FirefoxOptions options = new FirefoxOptions().setProfile(profile);

    localDriver = new FirefoxDriver(options);
    wait.until($ -> "XHTML Test Page".equals(localDriver.getTitle()));

    verifyItIsMarionette(localDriver);
  }

  @Test
  public void canSetProfileInCapabilities() {
    FirefoxProfile profile = new FirefoxProfile();
    profile.setPreference("browser.startup.page", 1);
    profile.setPreference("browser.startup.homepage", pages.xhtmlTestPage);

    DesiredCapabilities caps = new DesiredCapabilities();
    caps.setCapability(FirefoxDriver.PROFILE, profile);

    localDriver = new FirefoxDriver(caps);
    wait.until($ -> "XHTML Test Page".equals(localDriver.getTitle()));

    verifyItIsMarionette(localDriver);
  }

  @Test
  public void canUseSameProfileInCapabilitiesAndDirectly() {
    FirefoxProfile profile = new FirefoxProfile();
    profile.setPreference("browser.startup.page", 1);
    profile.setPreference("browser.startup.homepage", pages.xhtmlTestPage);

    DesiredCapabilities caps = new DesiredCapabilities();
    caps.setCapability(FirefoxDriver.PROFILE, profile);

    localDriver = new FirefoxDriver(
        new FirefoxOptions()
            .setProfile(profile)
            .addCapabilities(caps));
    wait.until($ -> "XHTML Test Page".equals(localDriver.getTitle()));

    verifyItIsMarionette(localDriver);
  }

  @Test
  public void canPassCapabilitiesBinaryAndProfileSeparately() throws IOException {
    FirefoxBinary binary = spy(new FirefoxBinary());

    FirefoxProfile profile = new FirefoxProfile();
    profile.setPreference("browser.startup.page", 1);
    profile.setPreference("browser.startup.homepage", pages.xhtmlTestPage);

    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setCapability(CapabilityType.PAGE_LOAD_STRATEGY, "none");

    localDriver = new FirefoxDriver(
        new FirefoxOptions()
          .setBinary(binary)
          .setProfile(profile)
          .addCapabilities(capabilities));
    wait.until($ -> "XHTML Test Page".equals(localDriver.getTitle()));

    verifyItIsMarionette(localDriver);
    verify(binary, atLeastOnce()).getPath();
    verify(binary, never()).startFirefoxProcess(any());
    assertEquals(
        localDriver.getCapabilities().getCapability(CapabilityType.PAGE_LOAD_STRATEGY), "none");
  }

  @Test
  public void canSetPreferencesAndProfileInFirefoxOptions() {
    FirefoxProfile profile = new FirefoxProfile();
    profile.setPreference("browser.startup.page", 1);
    profile.setPreference("browser.startup.homepage", pages.xhtmlTestPage);

    FirefoxOptions options = new FirefoxOptions()
        .setProfile(profile)
        .addPreference("browser.startup.homepage", pages.javascriptPage);

    localDriver = new FirefoxDriver(options);
    wait.until($ -> "Testing Javascript".equals(localDriver.getTitle()));

    verifyItIsMarionette(localDriver);
  }

  @Test
  public void canSetPageLoadStrategyViaOptions() {
    localDriver = new FirefoxDriver(
        new FirefoxOptions().setPageLoadStrategy(PageLoadStrategy.NONE));

    verifyItIsMarionette(localDriver);
    assertEquals(localDriver.getCapabilities()
                     .getCapability(CapabilityType.PAGE_LOAD_STRATEGY), "none");
  }

  @Test
  public void canSetInsecureCertSupportViaOptions() {
    localDriver = new FirefoxDriver(new FirefoxOptions().setAcceptInsecureCerts(true));

    verifyItIsMarionette(localDriver);
    assertEquals(localDriver.getCapabilities()
                     .getCapability(CapabilityType.ACCEPT_INSECURE_CERTS), true);
  }

  @Test
  public void canStartHeadless() {
    localDriver = new FirefoxDriver(new FirefoxOptions().setHeadless(true));

    verifyItIsMarionette(localDriver);
    assertEquals(localDriver.getCapabilities().getCapability("moz:headless"), true);
  }

  private void verifyItIsMarionette(FirefoxDriver driver) {
    assertNotNull(
        Optional.ofNullable(driver.getCapabilities().getCapability("moz:processID"))
            .orElse(driver.getCapabilities().getCapability("processId")));
  }
}
