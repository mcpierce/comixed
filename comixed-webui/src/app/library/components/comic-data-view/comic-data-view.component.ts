/*
 * ComiXed - A digital comic book library management application.
 * Copyright (C) 2026, The ComiXed Project
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

import { Component, Input } from '@angular/core';
import { DisplayableComic } from '@app/comic-books/models/displayable-comic';
import { JsonPipe } from '@angular/common';
import { MatTableDataSource } from '@angular/material/table';
import { SelectableListItem } from '@app/core/models/ui/selectable-list-item';
import { ComicGridViewComponent } from '@app/library/components/comic-grid-view/comic-grid-view.component';
import { ComicListViewComponent } from '@app/library/components/comic-list-view/comic-list-view.component';

@Component({
  selector: 'cx-comic-data-view',
  imports: [JsonPipe, ComicGridViewComponent, ComicListViewComponent],
  templateUrl: './comic-data-view.component.html',
  styleUrl: './comic-data-view.component.scss'
})
export class ComicDataViewComponent {
  dataSource = new MatTableDataSource<SelectableListItem<DisplayableComic>>([]);
  @Input() gridView = false;
  @Input() comics: DisplayableComic[];
}
