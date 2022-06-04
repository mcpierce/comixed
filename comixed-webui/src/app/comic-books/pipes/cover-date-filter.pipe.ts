/*
 * ComiXed - A digital comic book library management application.
 * Copyright (C) 2022, The ComiXed Project
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

import { Pipe, PipeTransform } from '@angular/core';
import { ComicBook } from '@app/comic-books/models/comic-book';
import { CoverDateFilter } from '@app/comic-books/models/ui/cover-date-filter';

@Pipe({
  name: 'coverDateFilter'
})
export class CoverDateFilterPipe implements PipeTransform {
  transform(comicBooks: ComicBook[], filter: CoverDateFilter): ComicBook[] {
    return comicBooks
      .filter(
        comicBook =>
          !filter.year ||
          new Date(comicBook.coverDate).getFullYear() === filter.year
      )
      .filter(
        comicBook =>
          !filter.month ||
          new Date(comicBook.coverDate).getMonth() === filter.month
      );
  }
}