/*
 * ComiXed - A digital comicBook book library management application.
 * Copyright (C) 2024, The ComiXed Project
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

package org.comixedproject.adaptors.content;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.log4j.Log4j2;

/**
 * <code>ComicInfoXmlFileNameContentAdaptorProvider</code> provides a {link
 * FileNameContentAdaptorProvider} for {@link ComicInfoXmlFileNameContentAdaptor}.
 *
 * @author Darryl L. Pierce
 */
@Log4j2
public class ComicInfoXmlFileNameContentAdaptorProvider implements FileNameContentAdaptorProvider {
  private ComicInfoXmlFileNameContentAdaptor adaptor = new ComicInfoXmlFileNameContentAdaptor();

  @Override
  public FileNameContentAdaptor create() {
    return this.adaptor;
  }

  @Override
  public String getName() {
    return this.getClass().getSimpleName();
  }

  @Override
  public boolean supportedFilename(final String filename) {
    final Pattern pattern = Pattern.compile(".*ComicInfo.xml$", Pattern.CASE_INSENSITIVE);
    final Matcher matcher = pattern.matcher(filename);
    return matcher.matches();
  }
}
