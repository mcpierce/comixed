/*
 * ComiXed - A digital comic book library management application.
 * Copyright (C) 2023, The ComiXed Project
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

import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { Observable, of, throwError } from 'rxjs';
import { ComicBookSelectionEffects } from './comic-book-selection.effects';
import { ArchiveType } from '@app/comic-books/models/archive-type.enum';
import { ComicType } from '@app/comic-books/models/comic-type';
import { ComicState } from '@app/comic-books/models/comic-state';
import { ComicBookSelectionService } from '@app/comic-books/services/comic-book-selection.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import {
  addSingleComicBookSelection,
  clearComicBookSelectionState,
  clearComicBookSelectionStateFailed,
  comicBookSelectionsLoaded,
  comicBookSelectionStateCleared,
  loadComicBookSelections,
  loadComicBookSelectionsFailed,
  removeSingleComicBookSelection,
  setComicBookSelectionByUnreadState,
  setDuplicateComicBooksSelectionState,
  setMultipleComicBookByFilterSelectionState,
  setMultipleComicBookByIdSelectionState,
  setMultipleComicBookByPublisherSelectionState,
  setMultipleComicBookByPublisherSeriesAndVolumeSelectionState,
  setMultipleComicBooksByTagTypeAndValueSelectionState,
  setMultipleComicBookSelectionStateFailure,
  setMultipleComicBookSelectionStateSuccess,
  singleComicBookSelectionFailed,
  singleComicBookSelectionUpdated
} from '@app/comic-books/actions/comic-book-selection.actions';
import { hot } from 'jasmine-marbles';
import { LoggerModule } from '@angular-ru/cdk/logger';
import { TranslateModule } from '@ngx-translate/core';
import { AlertService } from '@app/core/services/alert.service';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { PUBLISHER_1, SERIES_1 } from '@app/collections/collections.fixtures';
import { ComicTagType } from '@app/comic-books/models/comic-tag-type';

describe('ComicBookSelectionEffects', () => {
  const COVER_YEAR = Math.random() * 100 + 1900;
  const COVER_MONTH = Math.random() * 12;
  const ARCHIVE_TYPE = ArchiveType.CB7;
  const COMIC_TYPE = ComicType.ISSUE;
  const COMIC_STATE = ComicState.UNPROCESSED;
  const UNSCRAPED_STATE = Math.random() > 0.5;
  const SEARCH_TEXT = 'This is some text';
  const COMIC_BOOK_ID = 65;
  const PUBLISHER = PUBLISHER_1.name;
  const SERIES = SERIES_1.name;
  const VOLUME = '2024';
  const SELECTED = Math.random() > 0.5;
  const UNREAD_ONLY = Math.random() > 0.5;
  const TAG_TYPE = ComicTagType.TEAM;
  const TAG_VALUE = 'Some team';
  const COMIC_BOOK_IDS = [7, 17, 65, 1, 29, 91];

  let actions$: Observable<any>;
  let effects: ComicBookSelectionEffects;
  let comicBookSelectionService: jasmine.SpyObj<ComicBookSelectionService>;
  let alertService: AlertService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ComicBookSelectionEffects,
        provideMockActions(() => actions$),
        {
          provide: ComicBookSelectionService,
          useValue: {
            loadSelections: jasmine.createSpy(
              'ComicBookSelectionService.loadSelections()'
            ),
            addSingleSelection: jasmine.createSpy(
              'ComicBookSelectionService.addSingleSelection()'
            ),
            removeSingleSelection: jasmine.createSpy(
              'ComicBookSelectionService.removeSingleSelection()'
            ),
            setSelectedByFilter: jasmine.createSpy(
              'ComicBookSelectionService.setSelectedByFilter()'
            ),
            setSelectedByTagTypeAndValue: jasmine.createSpy(
              'ComicBookSelectionService.setSelectedByTagTypeAndValue()'
            ),
            setSelectedById: jasmine.createSpy(
              'ComicBookSelectionService.setSelectedById()'
            ),
            setSelectedByPublisher: jasmine.createSpy(
              'ComicBookSelectionService.setSelectedByPublisher()'
            ),
            setSelectedByPublisherSeriesAndVolume: jasmine.createSpy(
              'ComicBookSelectionService.setSelectedByPublisherSeriesAndVolume()'
            ),
            setDuplicateComicBooksSelectionState: jasmine.createSpy(
              'ComicBookSelectionService.setDuplicateComicBooksSelectionState()'
            ),
            setUnreadComicBooksSelectionState: jasmine.createSpy(
              'ComicBookSelectionService.setUnreadComicBooksSelectionState()'
            ),
            clearSelections: jasmine.createSpy(
              'ComicBookSelectionService.clearSelections()'
            )
          }
        }
      ],
      imports: [
        LoggerModule.forRoot(),
        TranslateModule.forRoot(),
        MatSnackBarModule
      ]
    });

    effects = TestBed.inject(ComicBookSelectionEffects);
    comicBookSelectionService = TestBed.inject(
      ComicBookSelectionService
    ) as jasmine.SpyObj<ComicBookSelectionService>;
    alertService = TestBed.inject(AlertService);
    spyOn(alertService, 'error');
  });

  it('should be created', () => {
    expect(effects).toBeTruthy();
  });

  describe('loading the current comic book selections', () => {
    it('fires an action on success', () => {
      const serviceResponse = [COMIC_BOOK_ID];
      const action = loadComicBookSelections();
      const outcome = comicBookSelectionsLoaded({ ids: [COMIC_BOOK_ID] });

      actions$ = hot('-a', {
        a: action
      });
      comicBookSelectionService.loadSelections.and.returnValue(
        of(serviceResponse)
      );

      const expected = hot('-b', { b: outcome });
      expect(effects.loadSelections$).toBeObservable(expected);
    });

    it('fires an action on service failure', () => {
      const serviceResponse = new HttpErrorResponse({});
      const action = loadComicBookSelections();
      const outcome = loadComicBookSelectionsFailed();

      actions$ = hot('-a', {
        a: action
      });
      comicBookSelectionService.loadSelections.and.returnValue(
        throwError(serviceResponse)
      );

      const expected = hot('-b', { b: outcome });
      expect(effects.loadSelections$).toBeObservable(expected);
      expect(alertService.error).toHaveBeenCalledWith(jasmine.any(String));
    });

    it('fires an action on general failure', () => {
      const action = loadComicBookSelections();
      const outcome = loadComicBookSelectionsFailed();

      actions$ = hot('-a', {
        a: action
      });
      comicBookSelectionService.loadSelections.and.throwError('expected');

      const expected = hot('-(b|)', { b: outcome });
      expect(effects.loadSelections$).toBeObservable(expected);
      expect(alertService.error).toHaveBeenCalledWith(jasmine.any(String));
    });
  });

  describe('adding a single comic book selection', () => {
    it('fires an action on success', () => {
      const serviceResponse = new HttpResponse({});
      const action = addSingleComicBookSelection({
        comicBookId: COMIC_BOOK_ID
      });
      const outcome = singleComicBookSelectionUpdated();

      actions$ = hot('-a', { a: action });
      comicBookSelectionService.addSingleSelection
        .withArgs({
          comicBookId: COMIC_BOOK_ID
        })
        .and.returnValue(of(serviceResponse));

      const expected = hot('-b', { b: outcome });
      expect(effects.addSingleSelection$).toBeObservable(expected);
    });

    it('fires an action on service failure', () => {
      const serviceResponse = new HttpErrorResponse({});
      const action = addSingleComicBookSelection({
        comicBookId: COMIC_BOOK_ID
      });
      const outcome = singleComicBookSelectionFailed();

      actions$ = hot('-a', { a: action });
      comicBookSelectionService.addSingleSelection
        .withArgs({
          comicBookId: COMIC_BOOK_ID
        })
        .and.returnValue(throwError(serviceResponse));

      const expected = hot('-b', { b: outcome });
      expect(effects.addSingleSelection$).toBeObservable(expected);
      expect(alertService.error).toHaveBeenCalledWith(jasmine.any(String));
    });

    it('fires an action on general failure', () => {
      const action = addSingleComicBookSelection({
        comicBookId: COMIC_BOOK_ID
      });
      const outcome = singleComicBookSelectionFailed();

      actions$ = hot('-a', { a: action });
      comicBookSelectionService.addSingleSelection
        .withArgs({
          comicBookId: COMIC_BOOK_ID
        })
        .and.throwError('expected');

      const expected = hot('-(b|)', { b: outcome });
      expect(effects.addSingleSelection$).toBeObservable(expected);
      expect(alertService.error).toHaveBeenCalledWith(jasmine.any(String));
    });
  });

  describe('removing a single comic book selection', () => {
    it('fires an action on success', () => {
      const serviceResponse = new HttpResponse({});
      const action = removeSingleComicBookSelection({
        comicBookId: COMIC_BOOK_ID
      });
      const outcome = singleComicBookSelectionUpdated();

      actions$ = hot('-a', { a: action });
      comicBookSelectionService.removeSingleSelection
        .withArgs({
          comicBookId: COMIC_BOOK_ID
        })
        .and.returnValue(of(serviceResponse));

      const expected = hot('-b', { b: outcome });
      expect(effects.removeSingleSelection$).toBeObservable(expected);
    });

    it('fires an action on service failure', () => {
      const serviceResponse = new HttpErrorResponse({});
      const action = removeSingleComicBookSelection({
        comicBookId: COMIC_BOOK_ID
      });
      const outcome = singleComicBookSelectionFailed();

      actions$ = hot('-a', { a: action });
      comicBookSelectionService.removeSingleSelection
        .withArgs({
          comicBookId: COMIC_BOOK_ID
        })
        .and.returnValue(throwError(serviceResponse));

      const expected = hot('-b', { b: outcome });
      expect(effects.removeSingleSelection$).toBeObservable(expected);
      expect(alertService.error).toHaveBeenCalledWith(jasmine.any(String));
    });

    it('fires an action on general failure', () => {
      const action = removeSingleComicBookSelection({
        comicBookId: COMIC_BOOK_ID
      });
      const outcome = singleComicBookSelectionFailed();

      actions$ = hot('-a', { a: action });
      comicBookSelectionService.removeSingleSelection
        .withArgs({
          comicBookId: COMIC_BOOK_ID
        })
        .and.throwError('expected');

      const expected = hot('-(b|)', { b: outcome });
      expect(effects.removeSingleSelection$).toBeObservable(expected);
      expect(alertService.error).toHaveBeenCalledWith(jasmine.any(String));
    });
  });

  describe('selecting comic books by filter', () => {
    it('fires an action on success', () => {
      const serviceResponse = new HttpResponse({});
      const action = setMultipleComicBookByFilterSelectionState({
        coverYear: COVER_YEAR,
        coverMonth: COVER_MONTH,
        archiveType: ARCHIVE_TYPE,
        comicType: COMIC_TYPE,
        comicState: COMIC_STATE,
        unscrapedState: UNSCRAPED_STATE,
        searchText: SEARCH_TEXT,
        selected: SELECTED
      });
      const outcome = setMultipleComicBookSelectionStateSuccess();

      actions$ = hot('-a', { a: action });
      comicBookSelectionService.setSelectedByFilter
        .withArgs({
          coverYear: COVER_YEAR,
          coverMonth: COVER_MONTH,
          archiveType: ARCHIVE_TYPE,
          comicType: COMIC_TYPE,
          comicState: COMIC_STATE,
          unscrapedState: UNSCRAPED_STATE,
          searchText: SEARCH_TEXT,
          selected: SELECTED
        })
        .and.returnValue(of(serviceResponse));

      const expected = hot('-b', { b: outcome });
      expect(effects.setSelectedByFilter$).toBeObservable(expected);
    });

    it('fires an action on service failure', () => {
      const serviceResponse = new HttpErrorResponse({});
      const action = setMultipleComicBookByFilterSelectionState({
        coverYear: COVER_YEAR,
        coverMonth: COVER_MONTH,
        archiveType: ARCHIVE_TYPE,
        comicType: COMIC_TYPE,
        comicState: COMIC_STATE,
        unscrapedState: UNSCRAPED_STATE,
        searchText: SEARCH_TEXT,
        selected: SELECTED
      });
      const outcome = setMultipleComicBookSelectionStateFailure();

      actions$ = hot('-a', { a: action });
      comicBookSelectionService.setSelectedByFilter
        .withArgs({
          coverYear: COVER_YEAR,
          coverMonth: COVER_MONTH,
          archiveType: ARCHIVE_TYPE,
          comicType: COMIC_TYPE,
          comicState: COMIC_STATE,
          unscrapedState: UNSCRAPED_STATE,
          searchText: SEARCH_TEXT,
          selected: SELECTED
        })
        .and.returnValue(throwError(serviceResponse));

      const expected = hot('-b', { b: outcome });
      expect(effects.setSelectedByFilter$).toBeObservable(expected);
      expect(alertService.error).toHaveBeenCalledWith(jasmine.any(String));
    });

    it('fires an action on general failure', () => {
      const action = setMultipleComicBookByFilterSelectionState({
        coverYear: COVER_YEAR,
        coverMonth: COVER_MONTH,
        archiveType: ARCHIVE_TYPE,
        comicType: COMIC_TYPE,
        comicState: COMIC_STATE,
        unscrapedState: UNSCRAPED_STATE,
        searchText: SEARCH_TEXT,
        selected: SELECTED
      });
      const outcome = setMultipleComicBookSelectionStateFailure();

      actions$ = hot('-a', { a: action });
      comicBookSelectionService.setSelectedByFilter
        .withArgs({
          coverYear: COVER_YEAR,
          coverMonth: COVER_MONTH,
          archiveType: ARCHIVE_TYPE,
          comicType: COMIC_TYPE,
          comicState: COMIC_STATE,
          unscrapedState: UNSCRAPED_STATE,
          searchText: SEARCH_TEXT,
          selected: SELECTED
        })
        .and.throwError('expected');

      const expected = hot('-(b|)', { b: outcome });
      expect(effects.setSelectedByFilter$).toBeObservable(expected);
      expect(alertService.error).toHaveBeenCalledWith(jasmine.any(String));
    });
  });

  describe('selecting comic books by tag type and value', () => {
    it('fires an action on success', () => {
      const serviceResponse = new HttpResponse({});
      const action = setMultipleComicBooksByTagTypeAndValueSelectionState({
        tagType: TAG_TYPE,
        tagValue: TAG_VALUE,
        selected: SELECTED
      });
      const outcome = setMultipleComicBookSelectionStateSuccess();

      actions$ = hot('-a', { a: action });
      comicBookSelectionService.setSelectedByTagTypeAndValue
        .withArgs({
          tagType: TAG_TYPE,
          tagValue: TAG_VALUE,
          selected: SELECTED
        })
        .and.returnValue(of(serviceResponse));

      const expected = hot('-b', { b: outcome });
      expect(effects.setSelectedByTagTypeAndValue$).toBeObservable(expected);
    });

    it('fires an action on service failure', () => {
      const serviceResponse = new HttpErrorResponse({});
      const action = setMultipleComicBooksByTagTypeAndValueSelectionState({
        tagType: TAG_TYPE,
        tagValue: TAG_VALUE,
        selected: SELECTED
      });
      const outcome = setMultipleComicBookSelectionStateFailure();

      actions$ = hot('-a', { a: action });
      comicBookSelectionService.setSelectedByTagTypeAndValue
        .withArgs({
          tagType: TAG_TYPE,
          tagValue: TAG_VALUE,
          selected: SELECTED
        })
        .and.returnValue(throwError(serviceResponse));

      const expected = hot('-b', { b: outcome });
      expect(effects.setSelectedByTagTypeAndValue$).toBeObservable(expected);
      expect(alertService.error).toHaveBeenCalledWith(jasmine.any(String));
    });

    it('fires an action on general failure', () => {
      const action = setMultipleComicBooksByTagTypeAndValueSelectionState({
        tagType: TAG_TYPE,
        tagValue: TAG_VALUE,
        selected: SELECTED
      });
      const outcome = setMultipleComicBookSelectionStateFailure();

      actions$ = hot('-a', { a: action });
      comicBookSelectionService.setSelectedByTagTypeAndValue
        .withArgs({
          tagType: TAG_TYPE,
          tagValue: TAG_VALUE,
          selected: SELECTED
        })
        .and.throwError('expected');

      const expected = hot('-(b|)', { b: outcome });
      expect(effects.setSelectedByTagTypeAndValue$).toBeObservable(expected);
      expect(alertService.error).toHaveBeenCalledWith(jasmine.any(String));
    });
  });

  describe('selecting comic books by id', () => {
    it('fires an action on success', () => {
      const serviceResponse = new HttpResponse({});
      const action = setMultipleComicBookByIdSelectionState({
        comicBookIds: COMIC_BOOK_IDS,
        selected: SELECTED
      });
      const outcome = setMultipleComicBookSelectionStateSuccess();

      actions$ = hot('-a', { a: action });
      comicBookSelectionService.setSelectedById
        .withArgs({ comicBookIds: COMIC_BOOK_IDS, selected: SELECTED })
        .and.returnValue(of(serviceResponse));

      const expected = hot('-b', { b: outcome });
      expect(effects.setSelectedById$).toBeObservable(expected);
    });

    it('fires an action on service failure', () => {
      const serviceResponse = new HttpErrorResponse({});
      const action = setMultipleComicBookByIdSelectionState({
        comicBookIds: COMIC_BOOK_IDS,
        selected: SELECTED
      });
      const outcome = setMultipleComicBookSelectionStateFailure();

      actions$ = hot('-a', { a: action });
      comicBookSelectionService.setSelectedById
        .withArgs({ comicBookIds: COMIC_BOOK_IDS, selected: SELECTED })
        .and.returnValue(throwError(serviceResponse));

      const expected = hot('-b', { b: outcome });
      expect(effects.setSelectedById$).toBeObservable(expected);
      expect(alertService.error).toHaveBeenCalledWith(jasmine.any(String));
    });

    it('fires an action on general failure', () => {
      const action = setMultipleComicBookByIdSelectionState({
        comicBookIds: COMIC_BOOK_IDS,
        selected: SELECTED
      });
      const outcome = setMultipleComicBookSelectionStateFailure();

      actions$ = hot('-a', { a: action });
      comicBookSelectionService.setSelectedById
        .withArgs({ comicBookIds: COMIC_BOOK_IDS, selected: SELECTED })
        .and.throwError('expected');

      const expected = hot('-(b|)', { b: outcome });
      expect(effects.setSelectedById$).toBeObservable(expected);
      expect(alertService.error).toHaveBeenCalledWith(jasmine.any(String));
    });
  });

  describe('selecting comic books by publisher name', () => {
    it('fires an action on success', () => {
      const serviceResponse = new HttpResponse({});
      const action = setMultipleComicBookByPublisherSelectionState({
        publisher: PUBLISHER,
        selected: SELECTED
      });
      const outcome = setMultipleComicBookSelectionStateSuccess();

      actions$ = hot('-a', { a: action });
      comicBookSelectionService.setSelectedByPublisher
        .withArgs({ publisher: PUBLISHER, selected: SELECTED })
        .and.returnValue(of(serviceResponse));

      const expected = hot('-b', { b: outcome });
      expect(effects.setSelectedByPublisher$).toBeObservable(expected);
    });

    it('fires an action on service failure', () => {
      const serviceResponse = new HttpErrorResponse({});
      const action = setMultipleComicBookByPublisherSelectionState({
        publisher: PUBLISHER,
        selected: SELECTED
      });
      const outcome = setMultipleComicBookSelectionStateFailure();

      actions$ = hot('-a', { a: action });
      comicBookSelectionService.setSelectedByPublisher
        .withArgs({ publisher: PUBLISHER, selected: SELECTED })
        .and.returnValue(throwError(serviceResponse));

      const expected = hot('-b', { b: outcome });
      expect(effects.setSelectedByPublisher$).toBeObservable(expected);
      expect(alertService.error).toHaveBeenCalledWith(jasmine.any(String));
    });

    it('fires an action on general failure', () => {
      const action = setMultipleComicBookByPublisherSelectionState({
        publisher: PUBLISHER,
        selected: SELECTED
      });
      const outcome = setMultipleComicBookSelectionStateFailure();

      actions$ = hot('-a', { a: action });
      comicBookSelectionService.setSelectedByPublisher
        .withArgs({ publisher: PUBLISHER, selected: SELECTED })
        .and.throwError('expected');

      const expected = hot('-(b|)', { b: outcome });
      expect(effects.setSelectedByPublisher$).toBeObservable(expected);
      expect(alertService.error).toHaveBeenCalledWith(jasmine.any(String));
    });
  });

  describe('selecting comic books by publisher, series, and volume', () => {
    it('fires an action on success', () => {
      const serviceResponse = new HttpResponse({});
      const action =
        setMultipleComicBookByPublisherSeriesAndVolumeSelectionState({
          publisher: PUBLISHER,
          series: SERIES,
          volume: VOLUME,
          selected: SELECTED
        });
      const outcome = setMultipleComicBookSelectionStateSuccess();

      actions$ = hot('-a', { a: action });
      comicBookSelectionService.setSelectedByPublisherSeriesAndVolume
        .withArgs({
          publisher: PUBLISHER,
          series: SERIES,
          volume: VOLUME,
          selected: SELECTED
        })
        .and.returnValue(of(serviceResponse));

      const expected = hot('-b', { b: outcome });
      expect(effects.setSelectedByPublisherSeriesAndVolume$).toBeObservable(
        expected
      );
    });

    it('fires an action on service failure', () => {
      const serviceResponse = new HttpErrorResponse({});
      const action =
        setMultipleComicBookByPublisherSeriesAndVolumeSelectionState({
          publisher: PUBLISHER,
          series: SERIES,
          volume: VOLUME,
          selected: SELECTED
        });
      const outcome = setMultipleComicBookSelectionStateFailure();

      actions$ = hot('-a', { a: action });
      comicBookSelectionService.setSelectedByPublisherSeriesAndVolume
        .withArgs({
          publisher: PUBLISHER,
          series: SERIES,
          volume: VOLUME,
          selected: SELECTED
        })
        .and.returnValue(throwError(serviceResponse));

      const expected = hot('-b', { b: outcome });
      expect(effects.setSelectedByPublisherSeriesAndVolume$).toBeObservable(
        expected
      );
      expect(alertService.error).toHaveBeenCalledWith(jasmine.any(String));
    });

    it('fires an action on general failure', () => {
      const action =
        setMultipleComicBookByPublisherSeriesAndVolumeSelectionState({
          publisher: PUBLISHER,
          series: SERIES,
          volume: VOLUME,
          selected: SELECTED
        });
      const outcome = setMultipleComicBookSelectionStateFailure();

      actions$ = hot('-a', { a: action });
      comicBookSelectionService.setSelectedByPublisherSeriesAndVolume
        .withArgs({
          publisher: PUBLISHER,
          series: SERIES,
          volume: VOLUME,
          selected: SELECTED
        })
        .and.throwError('expected');

      const expected = hot('-(b|)', { b: outcome });
      expect(effects.setSelectedByPublisherSeriesAndVolume$).toBeObservable(
        expected
      );
      expect(alertService.error).toHaveBeenCalledWith(jasmine.any(String));
    });
  });

  describe('selecting all duplicate comic books', () => {
    it('fires an action on success', () => {
      const serviceResponse = new HttpResponse({});
      const action = setDuplicateComicBooksSelectionState({
        selected: SELECTED
      });
      const outcome = setMultipleComicBookSelectionStateSuccess();

      actions$ = hot('-a', { a: action });
      comicBookSelectionService.setDuplicateComicBooksSelectionState
        .withArgs({ selected: SELECTED })
        .and.returnValue(of(serviceResponse));

      const expected = hot('-b', { b: outcome });
      expect(effects.setDuplicateComicBooksSelectionState$).toBeObservable(
        expected
      );
    });

    it('fires an action on service failure', () => {
      const serviceResponse = new HttpErrorResponse({});
      const action = setDuplicateComicBooksSelectionState({
        selected: SELECTED
      });
      const outcome = setMultipleComicBookSelectionStateFailure();

      actions$ = hot('-a', { a: action });
      comicBookSelectionService.setDuplicateComicBooksSelectionState
        .withArgs({ selected: SELECTED })
        .and.returnValue(throwError(serviceResponse));

      const expected = hot('-b', { b: outcome });
      expect(effects.setDuplicateComicBooksSelectionState$).toBeObservable(
        expected
      );
      expect(alertService.error).toHaveBeenCalledWith(jasmine.any(String));
    });

    it('fires an action on general failure', () => {
      const action = setDuplicateComicBooksSelectionState({
        selected: SELECTED
      });
      const outcome = setMultipleComicBookSelectionStateFailure();

      actions$ = hot('-a', { a: action });
      comicBookSelectionService.setDuplicateComicBooksSelectionState
        .withArgs({ selected: SELECTED })
        .and.throwError('expected');

      const expected = hot('-(b|)', { b: outcome });
      expect(effects.setDuplicateComicBooksSelectionState$).toBeObservable(
        expected
      );
      expect(alertService.error).toHaveBeenCalledWith(jasmine.any(String));
    });
  });

  describe('selecting all comic books by read state', () => {
    it('fires an action on success', () => {
      const serviceResponse = new HttpResponse({});
      const action = setComicBookSelectionByUnreadState({
        selected: SELECTED,
        unreadOnly: UNREAD_ONLY
      });
      const outcome = setMultipleComicBookSelectionStateSuccess();

      actions$ = hot('-a', { a: action });
      comicBookSelectionService.setUnreadComicBooksSelectionState
        .withArgs({ selected: SELECTED, unreadOnly: UNREAD_ONLY })
        .and.returnValue(of(serviceResponse));

      const expected = hot('-b', { b: outcome });
      expect(effects.setUnreadComicBooksSelectionState$).toBeObservable(
        expected
      );
    });

    it('fires an action on service failure', () => {
      const serviceResponse = new HttpErrorResponse({});
      const action = setComicBookSelectionByUnreadState({
        selected: SELECTED,
        unreadOnly: UNREAD_ONLY
      });
      const outcome = setMultipleComicBookSelectionStateFailure();

      actions$ = hot('-a', { a: action });
      comicBookSelectionService.setUnreadComicBooksSelectionState
        .withArgs({ selected: SELECTED, unreadOnly: UNREAD_ONLY })
        .and.returnValue(throwError(serviceResponse));

      const expected = hot('-b', { b: outcome });
      expect(effects.setUnreadComicBooksSelectionState$).toBeObservable(
        expected
      );
      expect(alertService.error).toHaveBeenCalledWith(jasmine.any(String));
    });

    it('fires an action on general failure', () => {
      const action = setComicBookSelectionByUnreadState({
        selected: SELECTED,
        unreadOnly: UNREAD_ONLY
      });
      const outcome = setMultipleComicBookSelectionStateFailure();

      actions$ = hot('-a', { a: action });
      comicBookSelectionService.setUnreadComicBooksSelectionState
        .withArgs({ selected: SELECTED, unreadOnly: UNREAD_ONLY })
        .and.throwError('expected');

      const expected = hot('-(b|)', { b: outcome });
      expect(effects.setUnreadComicBooksSelectionState$).toBeObservable(
        expected
      );
      expect(alertService.error).toHaveBeenCalledWith(jasmine.any(String));
    });
  });

  describe('clear the comic book selection state', () => {
    it('fires an action on success', () => {
      const serviceResponse = new HttpResponse({});
      const action = clearComicBookSelectionState();
      const outcome = comicBookSelectionStateCleared();

      actions$ = hot('-a', { a: action });
      comicBookSelectionService.clearSelections.and.returnValue(
        of(serviceResponse)
      );

      const expected = hot('-b', { b: outcome });
      expect(effects.clearSelections$).toBeObservable(expected);
    });

    it('fires an action on service failure', () => {
      const serviceResponse = new HttpErrorResponse({});
      const action = clearComicBookSelectionState();
      const outcome = clearComicBookSelectionStateFailed();

      actions$ = hot('-a', { a: action });
      comicBookSelectionService.clearSelections.and.returnValue(
        throwError(serviceResponse)
      );

      const expected = hot('-b', { b: outcome });
      expect(effects.clearSelections$).toBeObservable(expected);
      expect(alertService.error).toHaveBeenCalledWith(jasmine.any(String));
    });

    it('fires an action on general failure', () => {
      const action = clearComicBookSelectionState();
      const outcome = clearComicBookSelectionStateFailed();

      actions$ = hot('-a', { a: action });
      comicBookSelectionService.clearSelections.and.throwError('expected');

      const expected = hot('-(b|)', { b: outcome });
      expect(effects.clearSelections$).toBeObservable(expected);
      expect(alertService.error).toHaveBeenCalledWith(jasmine.any(String));
    });
  });
});
