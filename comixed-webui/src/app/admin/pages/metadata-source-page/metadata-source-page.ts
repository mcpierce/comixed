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

import { Component, inject, OnInit, signal } from '@angular/core';
import { MenuItem } from '@openng/optimus-ui/api';
import { LoggerService } from '@angular-ru/cdk/logger';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { TitleService } from '@app/layout/services/title-service';
import { ActivatedRoute } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Breadcrumb } from '@openng/optimus-ui/breadcrumb';
import { Card } from '@openng/optimus-ui/card';
import { TableModule } from '@openng/optimus-ui/table';
import {
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { InputText } from '@openng/optimus-ui/inputtext';
import { Checkbox } from '@openng/optimus-ui/checkbox';
import { Button } from '@openng/optimus-ui/button';
import { MetadataSourceProperty } from '@app/settings/models/metadata-source-property';
import { Store } from '@ngrx/store';
import { selectMetadataSourceListEntries } from '@app/settings/selectors/metadata-source-list.selectors';
import { MetadataSource } from '@app/settings/models/metadata-source';
import { MetadataSourceListActions } from '@app/settings/actions/metadata-source-list.actions';

@Component({
  imports: [
    Breadcrumb,
    Card,
    TableModule,
    ReactiveFormsModule,
    InputText,
    TranslatePipe,
    Checkbox,
    Button
  ],
  selector: 'app-metadata-source-page',
  styleUrl: './metadata-source-page.scss',
  templateUrl: './metadata-source-page.html'
})
export class MetadataSourcePage implements OnInit {
  readonly logger = inject(LoggerService);
  readonly activatedRoute = inject(ActivatedRoute);
  readonly translateService = inject(TranslateService);
  readonly titleService = inject(TitleService);
  readonly sourceName = signal('');
  readonly sourceEntries = signal<MetadataSource[]>([]);
  readonly sourceProperties = signal<MetadataSourceProperty[]>([]);
  readonly store = inject(Store);
  readonly formBuilder = inject(FormBuilder);

  readonly home: MenuItem = { icon: 'pi pi-home', routerLink: ['/'] };
  readonly trail = signal<MenuItem[]>([]);

  sourceForm: FormGroup;

  constructor() {
    this.sourceForm = this.formBuilder.group({
      sourceName: new FormControl(''),
      preferred: new FormControl(false),
      properties: this.formBuilder.array([])
    });

    this.activatedRoute.params.pipe(takeUntilDestroyed()).subscribe({
      next: params => {
        this.sourceName.set(params['sourceName']);
        this.doLoadSourceForm();
        this.doLoadTranslations();
      }
    });
    this.store
      .select(selectMetadataSourceListEntries)
      .pipe(takeUntilDestroyed())
      .subscribe({
        next: entries => {
          this.sourceEntries.set(entries);
          this.doLoadSourceForm();
        }
      });
    this.translateService.onLangChange.subscribe({
      next: () => this.doLoadTranslations()
    });
  }

  get properties(): FormArray {
    return this.sourceForm.controls['properties'] as FormArray;
  }

  ngOnInit(): void {
    this.logger.trace('Loading metadata sources');
    this.store.dispatch(MetadataSourceListActions.load());
  }

  onSubmitForm(): void {}

  private doAddProperty(name: string, value: string): FormGroup {
    return this.formBuilder.group({
      propertyName: [name, Validators.required],
      propertyValue: [value, Validators.required]
    });
  }

  private doLoadTranslations() {
    this.titleService.setTitle(
      this.translateService.instant('metadata-source-list-page.page-title')
    );
    this.trail.set([
      {
        label: this.translateService.instant('settings.trail-title')
      },
      {
        label: this.translateService.instant(
          'metadata-source-list-page.trail-title'
        ),
        routerLink: ['/admin/settings/metadata']
      },
      {
        label: this.sourceName()
      }
    ]);
  }

  private doLoadSourceForm() {
    if (
      this.sourceEntries()
        .map(entry => entry.name)
        .includes(this.sourceName())
    ) {
      const source = this.sourceEntries().find(
        entry => entry.name === this.sourceName()
      )!;
      this.logger.debug('Found source:', source);
      this.sourceForm.controls['sourceName'].setValue(source.name);
      this.sourceForm.controls['preferred'].setValue(source.preferred);
      while (this.properties.length > 0) {
        this.properties.removeAt(0);
      }
      this.properties.reset([]);
      if (source.properties.length > 0) {
        source.properties.forEach(property =>
          this.properties.push(
            this.doAddProperty(property.name, property.value)
          )
        );
      }
      this.sourceProperties.set(source.properties);
      this.sourceForm.markAsPristine();
      this.sourceForm.markAsUntouched();
    } else {
      this.logger.debug('Source not found!');
    }
  }
}
