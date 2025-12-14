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

import org.comixedproject.model.plugin.LibraryPlugin;

/**
 * <code>PluginLanguage</code> defines type for providing a runtime environment for plugins.
 *
 * @author Darryl L. Pierce
 */
public interface PluginLanguage {
  /**
   * Returns the plugin's name.
   *
   * @param filename the plugin filename
   * @return the name
   */
  String getName(String filename);

  /**
   * Returns the plugin's version.
   *
   * @param filename the plugin filename
   * @return the version
   */
  String getVersion(String filename);

  /**
   * Executes the given libraryPlugin.
   *
   * @param libraryPlugin the libraryPlugin
   * @return <code>true</code> if the libraryPlugin runs without error
   */
  Boolean execute(LibraryPlugin libraryPlugin);
}
