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

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * <code>AbstractPluginRuntime</code> provides a foundation for creating new instances of {@link
 * PluginRuntime}.
 *
 * @author Darryl L. Pierce
 */
@Log4j2
public abstract class AbstractPluginRuntime implements PluginRuntime {
  @Getter private Map<String, Object> properties = new HashMap<>();

  @Override
  public void addProperty(final String propertyName, final Object propertyValue) {
    log.trace("Adding property: {}={}", propertyName, propertyValue);
    this.properties.put(propertyName, propertyValue);
  }
}
