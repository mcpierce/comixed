/*
 * ComiXed - A digital comic book library management application.
 * Copyright (C) 2020, The ComiXed Project
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

package org.comixedproject.service.library;

/**
 * <code>LibraryException</code> is thrown when an error occurs during a library operation.
 *
 * @author Darryl L. Pierce
 */
public class LibraryException extends Exception {
  /**
   * Creates an instance with a message.
   *
   * @param message the message
   */
  public LibraryException(final String message) {
    super(message);
  }

  /**
   * Creates an instance with a message and cause.
   *
   * @param message the message
   * @param cause the cause
   */
  public LibraryException(String message, Exception cause) {
    super(message, cause);
  }
}
