/*
 * ComiXed - A digital comic book library management application.
 * Copyright (C) 2021, The ComiXed Project
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

import { createAction, props } from '@ngrx/store';
import { FilenameScrapingRule } from '@app/admin/models/filename-scraping-rule';
import { DownloadDocument } from '@app/core/models/download-document';

export const loadFilenameScrapingRules = createAction(
  '[Filename Scraping Rules] Load filename scraping rules'
);

export const loadFilenameScrapingRulesSuccess = createAction(
  '[Filename Scraping Rules] Filename scraping rules were loaded',
  props<{ rules: FilenameScrapingRule[] }>()
);

export const loadFilenameScrapingRulesFailure = createAction(
  '[Filename Scraping Rules] Failed to load the filename scraping rues'
);

export const saveFilenameScrapingRules = createAction(
  '[Filename Scraping Rules] Save filename scraping rules',
  props<{ rules: FilenameScrapingRule[] }>()
);

export const saveFilenameScrapingRulesSuccess = createAction(
  '[Filename Scraping Rules] Filename scraping rules saved',
  props<{ rules: FilenameScrapingRule[] }>()
);

export const saveFilenameScrapingRulesFailure = createAction(
  '[Filename Scraping Rules] Failed to save filename scraping rules'
);

export const downloadFilenameScrapingRules = createAction(
  '[Filename Scraping Rules] Download the current list of rules'
);

export const downloadFilenameScrapingRulesSuccess = createAction(
  '[Filename Scraping Rules] Successfully downloaded the current list of rules',
  props<{ document: DownloadDocument }>()
);

export const downloadFilenameScrapingRulesFailure = createAction(
  '[Filename Scraping Rules] Failed to download the current list of rules'
);

export const uploadFilenameScrapingRules = createAction(
  '[Filename Scraping Rules] Upload filename scraping rules file',
  props<{
    file: File;
  }>()
);

export const uploadFilenameScrapingRulesSuccess = createAction(
  '[Filename Scraping Rules] Successfully uploaded filename scraping rules file',
  props<{
    rules: FilenameScrapingRule[];
  }>()
);

export const uploadFilenameScrapingRulesFailure = createAction(
  '[Filename Scraping Rules] Failed to uploading filename scraping rules file'
);
