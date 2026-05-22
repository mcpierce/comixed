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

import { Component, inject, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { LoggerService } from '@angular-ru/cdk/logger';
import {
  selectComicFileGroups,
  selectComicFileListState
} from '@app/comic-files/selectors/comic-file-list.selectors';
import { ComicFileGroup } from '@app/comic-files/models/comic-file-group';
import { JsonPipe } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { MatFormField, MatInput, MatSuffix } from '@angular/material/input';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { selectUser } from '@app/user/selectors/user.selectors';
import { User } from '@app/user/models/user';
import { MatOption, MatSelect } from '@angular/material/select';
import { getUserPreference } from '@app/user';
import {
  IMPORT_MAXIMUM_RESULTS_DEFAULT,
  IMPORT_MAXIMUM_RESULTS_PREFERENCE,
  IMPORT_ROOT_DIRECTORY_PREFERENCE
} from '@app/library/library.constants';
import {
  loadComicFileLists,
  loadComicFilesFromSession
} from '@app/comic-files/actions/comic-file-list.actions';
import { setBusyState } from '@app/core/actions/busy.actions';

@Component({
  selector: 'cx-comic-import',
  imports: [
    JsonPipe,
    ReactiveFormsModule,
    MatFormField,
    MatIconButton,
    MatIcon,
    TranslatePipe,
    MatSuffix,
    MatInput,
    MatSelect,
    MatOption
  ],
  templateUrl: './comic-import.component.html',
  styleUrl: './comic-import.component.scss'
})
export class ComicImportComponent implements OnInit {
  readonly recordOptions = [
    { label: 'comic-files.label.maximum-all-files', value: 0 },
    { label: 'comic-files.label.maximum-10-files', value: 10 },
    { label: 'comic-files.label.maximum-50-files', value: 50 },
    { label: 'comic-files.label.maximum-100-files', value: 100 },
    { label: 'comic-files.label.maximum-1000-files', value: 1000 }
  ];

  logger = inject(LoggerService);
  store = inject(Store);
  formBuilder = inject(FormBuilder);
  importForm: FormGroup;

  constructor() {
    this.logger.debug('Building import form');
    this.importForm = this.formBuilder.group({
      directory: ['', Validators.required],
      maxRecords: ['', Validators.required]
    });
    this.logger.debug('Subscribing to user updates');
    this.store.select(selectUser).subscribe(user => (this.user = user));
    this.logger.debug('Subscribing to comic file list state updates');
    this.store
      .select(selectComicFileListState)
      .subscribe(state =>
        this.store.dispatch(setBusyState({ enabled: state.busy }))
      );
    this.logger.debug('Subscribing to comic file list updates');
    this.store
      .select(selectComicFileGroups)
      .subscribe(groups => (this.comicFileGroups = groups));
  }

  private _comicFileGroups: ComicFileGroup[] = [];

  get comicFileGroups(): ComicFileGroup[] {
    return this._comicFileGroups;
  }

  set comicFileGroups(groups: ComicFileGroup[]) {
    this._comicFileGroups = groups;
  }

  set user(user: User) {
    this.importForm.controls.directory.setValue(
      getUserPreference(
        user?.preferences,
        IMPORT_ROOT_DIRECTORY_PREFERENCE,
        this.importForm.controls.directory.value
      )
    );
    this.importForm.controls.maxRecords.setValue(
      parseInt(
        getUserPreference(
          user?.preferences,
          IMPORT_MAXIMUM_RESULTS_PREFERENCE,
          `${IMPORT_MAXIMUM_RESULTS_DEFAULT}`
        )
      ),
      10
    );
  }

  ngOnInit(): void {
    this.logger.debug('Loading comic files from session');
    this.store.dispatch(loadComicFilesFromSession());
  }

  onLoadComicFiles(): void {
    const directory = this.importForm.controls.directory.value;
    const maximum = this.importForm.controls.maxRecords.value;
    this.logger.debug('Loading comic files:', directory, maximum);
    this.store.dispatch(loadComicFileLists({ directory, maximum }));
  }
}
