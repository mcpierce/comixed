/*
 * ComiXed - A digital comic book library management application.
 * Copyright (C) 2023, The ComiXed Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses>
 */

package org.comixedproject.plugins;

import static junit.framework.TestCase.*;

import java.util.List;
import org.comixedproject.model.plugin.LibraryPlugin;
import org.comixedproject.model.plugin.LibraryPluginProperty;
import org.comixedproject.plugins.groovy.GroovyPluginRuntime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GroovyLibraryPluginRuntimeTest {
  private static final String TEST_GOOD_PLUGIN = "src/test/resources/good.cxplugin";
  private static final String TEST_BROKEN_PLUGIN = "src/test/resources/broken.cxplugin";
  private static final String TEST_PLUGIN_NAME = "Good Plugin";
  private static final String TEST_PLUGIN_VERSION = "1.2.3.4";
  private static final String TEST_PROPERTY_NAME_1 = "test_property_1";
  private static final Integer TEST_PROPERTY_1_LENGTH = 32;
  private static final String TEST_PROPERTY_NAME_2 = "test_property_2";
  private static final Integer TEST_PROPERTY_2_LENGTH = 64;

  @InjectMocks private GroovyPluginRuntime runner;
  @Mock private LibraryPlugin libraryPlugin;

  @Test
  void testExecuteHelloWorld() {
    Mockito.when(libraryPlugin.getFilename()).thenReturn(TEST_GOOD_PLUGIN);

    final Boolean result = runner.execute(libraryPlugin);

    assertTrue(result);
  }

  @Test
  void testExecuteBadScript() {
    Mockito.when(libraryPlugin.getFilename()).thenReturn(TEST_BROKEN_PLUGIN);

    final Boolean result = runner.execute(libraryPlugin);

    assertFalse(result);
  }

  @Test
  void testExecuteMissingScript() {
    Mockito.when(libraryPlugin.getFilename()).thenReturn(TEST_BROKEN_PLUGIN.substring(1));

    final Boolean result = runner.execute(libraryPlugin);

    assertFalse(result);
  }

  @Test
  void testGetName() {
    final String result = runner.getName(TEST_GOOD_PLUGIN);

    assertNotNull(result);
    assertEquals(TEST_PLUGIN_NAME, result);
  }

  @Test
  void testGetNameBadScript() {
    final String result = runner.getName(TEST_BROKEN_PLUGIN);

    assertNotNull(result);
    assertEquals("", result);
  }

  @Test
  void testGetNameMissingPlugin() {
    final String result = runner.getName(TEST_BROKEN_PLUGIN.substring(1));

    assertNotNull(result);
    assertEquals("", result);
  }

  @Test
  void testGetVersion() {
    final String result = runner.getVersion(TEST_GOOD_PLUGIN);

    assertNotNull(result);
    assertEquals(TEST_PLUGIN_VERSION, result);
  }

  @Test
  void testGetVersionBadScript() {
    final String result = runner.getVersion(TEST_BROKEN_PLUGIN);

    assertNotNull(result);
    assertEquals("", result);
  }

  @Test
  void testGetVersionMissingPlugin() {
    final String result = runner.getVersion(TEST_BROKEN_PLUGIN.substring(1));

    assertNotNull(result);
    assertEquals("", result);
  }

  @Test
  void testLoadProperties() {
    final List<LibraryPluginProperty> result = runner.getProperties(TEST_GOOD_PLUGIN);

    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertTrue(
        result.stream()
            .map(LibraryPluginProperty::getName)
            .toList()
            .contains(TEST_PROPERTY_NAME_1));
    assertTrue(
        result.stream()
            .map(LibraryPluginProperty::getLength)
            .toList()
            .contains(TEST_PROPERTY_1_LENGTH));
    assertTrue(
        result.stream()
            .map(LibraryPluginProperty::getName)
            .toList()
            .contains(TEST_PROPERTY_NAME_2));
    assertTrue(
        result.stream()
            .map(LibraryPluginProperty::getLength)
            .toList()
            .contains(TEST_PROPERTY_2_LENGTH));
  }

  @Test
  void testLoadPropertiesBadScript() {
    final List<LibraryPluginProperty> result = runner.getProperties(TEST_BROKEN_PLUGIN);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void testLoadPropertiesMissingPlugin() {
    final List<LibraryPluginProperty> result =
        runner.getProperties(TEST_BROKEN_PLUGIN.substring(1));

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
}
