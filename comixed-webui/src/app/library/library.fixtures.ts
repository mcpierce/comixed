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

import { DuplicatePage } from './models/duplicate-page';
import {
  COMIC_1,
  COMIC_2,
  COMIC_3,
  PAGE_1,
  PAGE_2,
  PAGE_3
} from '../comic-book/comic-book.fixtures';

export const DUPLICATE_PAGE_1: DuplicatePage = {
  hash: PAGE_1.hash,
  comics: [COMIC_1]
};

export const DUPLICATE_PAGE_2: DuplicatePage = {
  hash: PAGE_2.hash,
  comics: [COMIC_2]
};

export const DUPLICATE_PAGE_3: DuplicatePage = {
  hash: PAGE_3.hash,
  comics: [COMIC_3]
};