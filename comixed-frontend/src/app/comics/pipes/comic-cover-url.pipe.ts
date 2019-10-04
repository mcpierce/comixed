/*
 * ComiXed - A digital comic book library management application.
 * Copyright (C) 2018, The ComiXed Project
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.package
 * org.comixed;
 */

import { Pipe, PipeTransform } from '@angular/core';
import { COMIC_SERVICE_API_URL } from 'app/services/comic.service';
import { Comic } from 'app/library';

export const MISSING_COMIC_IMAGE_URL = '/assets/img/missing-comic-file.png';

@Pipe({
  name: 'comicCoverUrl'
})
export class ComicCoverUrlPipe implements PipeTransform {
  transform(comic: Comic): string {
    if (comic.missing) {
      return MISSING_COMIC_IMAGE_URL;
    } else {
      return `${COMIC_SERVICE_API_URL}/comics/${comic.id}/pages/0/content`;
    }
  }
}