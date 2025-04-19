/*
 * ComiXed - A digital comic book library management application.
 * Copyright (C) 2025, The ComiXed Project
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

import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { MetadataSource } from '@app/comic-metadata/models/metadata-source';
import { LoggerService } from '@angular-ru/cdk/logger';
import { Store } from '@ngrx/store';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { loadMetadataSources } from '@app/comic-metadata/actions/metadata-source-list.actions';
import { selectMetadataSourceList } from '@app/comic-metadata/selectors/metadata-source-list.selectors';

@Component({
  selector: 'cx-story-scraping',
  templateUrl: './story-scraping.component.html',
  styleUrl: './story-scraping.component.scss'
})
export class StoryScrapingComponent implements OnInit, OnDestroy {
  skipCache = false;
  metadataSource: MetadataSource | null = null;
  storyScrapingForm: FormGroup;
  metadataSourcesSubscription: Subscription;
  metadataSources: MetadataSource[] = [];

  constructor(
    private logger: LoggerService,
    private store: Store,
    private formBuilder: FormBuilder
  ) {
    this.logger.trace('Creating story scraping form');
    this.storyScrapingForm = this.formBuilder.group({
      metadataSource: [null, [Validators.required]],
      referenceId: [''],
      storyName: ['', [Validators.required]]
    });
    this.logger.trace('Subscribing to metadata source list updates');
    this.metadataSourcesSubscription = this.store
      .select(selectMetadataSourceList)
      .subscribe(list => (this.metadataSources = list));
  }

  @Input() set storyName(storyName: string) {
    this.storyScrapingForm.controls.storyName.setValue(storyName);
  }

  get readyToScrape(): boolean {
    return false;
  }

  get readyToScrapeByReference(): boolean {
    return (
      this.storyScrapingForm.controls.referenceId.value.length > 0 &&
      this.storyScrapingForm.controls.metadataSource.value !== null
    );
  }

  ngOnInit(): void {
    this.logger.trace('Loading metadata sources');
    this.store.dispatch(loadMetadataSources());
  }

  ngOnDestroy(): void {
    this.logger.trace('Unsubscribing from metadata source list updates');
    this.metadataSourcesSubscription.unsubscribe();
  }

  onLoadStoryCandidates(): void {}

  onFetchCandidates(): void {}

  onScrapeByReferenceId(): void {}
}
