/*
 * ComiXed - A digital comicBook book library management application.
 * Copyright (C) 2021, The ComiXed Project.
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

package org.comixedproject.adaptors.comicbooks;

import static org.comixedproject.adaptors.comicbooks.ComicFileAdaptor.NO_COVER_DATE;
import static org.comixedproject.adaptors.comicbooks.ComicFileAdaptor.UNKNOWN_VALUE;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;
import org.comixedproject.model.comicbooks.ComicDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class ComicFileAdaptorTest {
  private static final String TEST_COMIC_FILENAME = "Super Awesome Issue #3";
  private static final String TEST_ROOT_DIRECTORY = "/Users/comixedreader/Documents/comics/library";
  private static final String TEST_FULL_COMIC_FILENAME =
      TEST_ROOT_DIRECTORY + "/" + TEST_COMIC_FILENAME;
  private static final String TEST_RELATIVE_NAME_WITHOUT_RULE = "/" + TEST_COMIC_FILENAME;
  private static final String TEST_UNIX_RENAMING_RULE =
      "$PUBLISHER/$SERIES/$VOLUME/$SERIES v$VOLUME #$ISSUE $TITLE $PUBMONTH $PUBYEAR $COVERDATE $IMPRINT";
  private static final String TEST_WINDOWS_RENAMING_RULE =
      "$PUBLISHER\\$SERIES\\$VOLUME\\$SERIES v$VOLUME #$ISSUE $TITLE $PUBMONTH $PUBYEAR $COVERDATE $IMPRINT";
  private static final String TEST_RENAMING_RULE_PADDED_ISSUE =
      "$PUBLISHER/$SERIES/$VOLUME/$SERIES v$VOLUME #$ISSUE(8) $TITLE $PUBMONTH $PUBYEAR $COVERDATE $IMPRINT";
  private static final String TEST_PUBLISHER = "The Publisher";
  private static final String TEST_IMPRINT = "The Imprint";
  private static final String TEST_SERIES = "The Series";
  private static final String TEST_VOLUME = "2020";
  private static final String TEST_ISSUE = "717";
  private static final String TEST_TITLE = "The Comic Title";
  private static final String TEST_PADDED_ISSUE = "00000717";
  private static final Date TEST_COVER_DATE = new Date(120, 6, 1);
  private static final LocalDate TEST_COVER_DATE_LOCALDATE =
      TEST_COVER_DATE.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
  private static final String TEST_FORMATTED_COVER_DATE =
      TEST_COVER_DATE_LOCALDATE.getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault())
          + " "
          + TEST_COVER_DATE_LOCALDATE.getYear();
  private static final Date TEST_STORE_DATE = new Date(120, 5, 1);
  private static final String TEST_PUBLISHED_MONTH = "5";
  private static final String TEST_PUBLISHED_YEAR = "2020";
  private static final String TEST_PUBLISHER_WITH_UNSUPPORTED_CHARACTERS = "\"?/:|'{}?%Publisher*'";
  private static final String TEST_PUBLISHER_WITH_UNSUPPORTED_CHARACTERS_SCRUBBED =
      "________Publisher_";
  private static final String TEST_SERIES_WITH_UNSUPPORTED_CHARACTERS = "<|{:}Series?>";
  private static final String TEST_SERIES_WITH_UNSUPPORTED_CHARACTERS_SCRUBBED = "_____Series__";
  private static final String TEST_ISSUE_WITH_UNSUPPORTED_CHARACTERS = "\\/717:";
  private static final String TEST_ISSUE_WITH_UNSUPPORTED_CHARACTERS_SCRUBBED = "_717_";
  private static final String TEST_RENAMING_RULE_WITH_UNSUPPORTED_CHARACTERS =
      "?*$PUBLISHER/<|?>$SERIES/:$VOLUME/$SERIES v$VOLUME #$ISSUE ($COVERDATE) $IMPRINT";
  private static final String TEST_ORIGINAL_FILENAME = "src/test/resources/available.cbz";
  private static final String TEST_EXISTING_FILENAME = "src/test/resources/example.cbz";
  private static final String TEST_TARGET_DIRECTORY =
      String.join(File.separator, new String[] {"target", "test-classes", "library"});

  @InjectMocks private ComicFileAdaptor adaptor;
  @Mock private ComicDetail comicDetail;

  @BeforeEach
  void setUp() {
    Mockito.when(comicDetail.getPublisher()).thenReturn(TEST_PUBLISHER);
    Mockito.when(comicDetail.getImprint()).thenReturn(TEST_IMPRINT);
    Mockito.when(comicDetail.getSeries()).thenReturn(TEST_SERIES);
    Mockito.when(comicDetail.getVolume()).thenReturn(TEST_VOLUME);
    Mockito.when(comicDetail.getIssueNumber()).thenReturn(TEST_ISSUE);
    Mockito.when(comicDetail.getCoverDate()).thenReturn(TEST_COVER_DATE);
    Mockito.when(comicDetail.getStoreDate()).thenReturn(TEST_STORE_DATE);
    Mockito.when(comicDetail.getTitle()).thenReturn(TEST_TITLE);
  }

  @Test
  void createFilenameFromRule_windows() {
    final String result =
        adaptor.createFilenameFromRule(
            comicDetail,
            TEST_FULL_COMIC_FILENAME,
            TEST_WINDOWS_RENAMING_RULE,
            TEST_TARGET_DIRECTORY);

    assertEquals(
        formattedName(
            TEST_TARGET_DIRECTORY,
            TEST_PUBLISHER,
            TEST_IMPRINT,
            TEST_SERIES,
            TEST_VOLUME,
            TEST_ISSUE,
            TEST_TITLE,
            TEST_FORMATTED_COVER_DATE,
            TEST_PUBLISHED_MONTH,
            TEST_PUBLISHED_YEAR),
        result);
  }

  @Test
  void createFilenameFromRule_EmptyRule() {
    final String result =
        adaptor.createFilenameFromRule(comicDetail, TEST_FULL_COMIC_FILENAME, "", "");

    assertEquals(TEST_RELATIVE_NAME_WITHOUT_RULE, result);
  }

  @Test
  void createFileFromRule() {
    final String result =
        adaptor.createFilenameFromRule(
            comicDetail, TEST_FULL_COMIC_FILENAME, TEST_UNIX_RENAMING_RULE, TEST_TARGET_DIRECTORY);

    assertEquals(
        formattedName(
            TEST_TARGET_DIRECTORY,
            TEST_PUBLISHER,
            TEST_IMPRINT,
            TEST_SERIES,
            TEST_VOLUME,
            TEST_ISSUE,
            TEST_TITLE,
            TEST_FORMATTED_COVER_DATE,
            TEST_PUBLISHED_MONTH,
            TEST_PUBLISHED_YEAR),
        result);
  }

  @Test
  void createFilenameFromRule_paddedIssue() {
    final String result =
        adaptor.createFilenameFromRule(
            comicDetail,
            TEST_FULL_COMIC_FILENAME,
            TEST_RENAMING_RULE_PADDED_ISSUE,
            TEST_TARGET_DIRECTORY);

    assertEquals(
        formattedName(
            TEST_TARGET_DIRECTORY,
            TEST_PUBLISHER,
            TEST_IMPRINT,
            TEST_SERIES,
            TEST_VOLUME,
            TEST_PADDED_ISSUE,
            TEST_TITLE,
            TEST_FORMATTED_COVER_DATE,
            TEST_PUBLISHED_MONTH,
            TEST_PUBLISHED_YEAR),
        result);
  }

  @Test
  void createFilenameFromRule_renamingRuleHasUnsupportedCharacters() {
    final String result =
        adaptor.createFilenameFromRule(
            comicDetail,
            TEST_FULL_COMIC_FILENAME,
            TEST_RENAMING_RULE_WITH_UNSUPPORTED_CHARACTERS,
            TEST_TARGET_DIRECTORY);

    assertEquals(
        String.format(
            String.join(
                File.separator, new String[] {"%s", "__%s", "____%s", "_%s", "%s v%s #%s (%s) %s"}),
            TEST_TARGET_DIRECTORY,
            TEST_PUBLISHER,
            TEST_SERIES,
            TEST_VOLUME,
            TEST_SERIES,
            TEST_VOLUME,
            TEST_ISSUE,
            TEST_FORMATTED_COVER_DATE,
            TEST_IMPRINT),
        result);
  }

  @Test
  void createFilenameFromRule_hasUnsupportedCharacters() {
    Mockito.when(comicDetail.getPublisher()).thenReturn(TEST_PUBLISHER_WITH_UNSUPPORTED_CHARACTERS);
    Mockito.when(comicDetail.getSeries()).thenReturn(TEST_SERIES_WITH_UNSUPPORTED_CHARACTERS);
    Mockito.when(comicDetail.getIssueNumber()).thenReturn(TEST_ISSUE_WITH_UNSUPPORTED_CHARACTERS);

    final String result =
        adaptor.createFilenameFromRule(
            comicDetail, TEST_FULL_COMIC_FILENAME, TEST_UNIX_RENAMING_RULE, TEST_TARGET_DIRECTORY);

    assertEquals(
        formattedName(
            TEST_TARGET_DIRECTORY,
            TEST_PUBLISHER_WITH_UNSUPPORTED_CHARACTERS_SCRUBBED,
            TEST_IMPRINT,
            TEST_SERIES_WITH_UNSUPPORTED_CHARACTERS_SCRUBBED,
            TEST_VOLUME,
            TEST_ISSUE_WITH_UNSUPPORTED_CHARACTERS_SCRUBBED,
            TEST_TITLE,
            TEST_FORMATTED_COVER_DATE,
            TEST_PUBLISHED_MONTH,
            TEST_PUBLISHED_YEAR),
        result);
  }

  @Test
  void createFilenameFromRule_noPublisher() {
    Mockito.when(comicDetail.getPublisher()).thenReturn(null);

    final String result =
        adaptor.createFilenameFromRule(
            comicDetail, TEST_FULL_COMIC_FILENAME, TEST_UNIX_RENAMING_RULE, TEST_TARGET_DIRECTORY);

    assertEquals(
        formattedName(
            TEST_TARGET_DIRECTORY,
            UNKNOWN_VALUE,
            TEST_IMPRINT,
            TEST_SERIES,
            TEST_VOLUME,
            TEST_ISSUE,
            TEST_TITLE,
            TEST_FORMATTED_COVER_DATE,
            TEST_PUBLISHED_MONTH,
            TEST_PUBLISHED_YEAR),
        result);
  }

  @Test
  void createFilenameFromRule_noSeries() {
    Mockito.when(comicDetail.getSeries()).thenReturn(null);

    final String result =
        adaptor.createFilenameFromRule(
            comicDetail, TEST_FULL_COMIC_FILENAME, TEST_UNIX_RENAMING_RULE, TEST_TARGET_DIRECTORY);

    assertEquals(
        formattedName(
            TEST_TARGET_DIRECTORY,
            TEST_PUBLISHER,
            TEST_IMPRINT,
            UNKNOWN_VALUE,
            TEST_VOLUME,
            TEST_ISSUE,
            TEST_TITLE,
            TEST_FORMATTED_COVER_DATE,
            TEST_PUBLISHED_MONTH,
            TEST_PUBLISHED_YEAR),
        result);
  }

  @Test
  void createFilenameFromRule_noImprint() {
    Mockito.when(comicDetail.getImprint()).thenReturn(null);

    final String result =
        adaptor.createFilenameFromRule(
            comicDetail, TEST_FULL_COMIC_FILENAME, TEST_UNIX_RENAMING_RULE, TEST_TARGET_DIRECTORY);

    assertEquals(
        formattedName(
            TEST_TARGET_DIRECTORY,
            TEST_PUBLISHER,
            TEST_PUBLISHER,
            TEST_SERIES,
            TEST_VOLUME,
            TEST_ISSUE,
            TEST_TITLE,
            TEST_FORMATTED_COVER_DATE,
            TEST_PUBLISHED_MONTH,
            TEST_PUBLISHED_YEAR),
        result);
  }

  @Test
  void createFilenameFromRule_noImprintOrPublisher() {
    Mockito.when(comicDetail.getPublisher()).thenReturn(null);
    Mockito.when(comicDetail.getImprint()).thenReturn(null);

    final String result =
        adaptor.createFilenameFromRule(
            comicDetail, TEST_FULL_COMIC_FILENAME, TEST_UNIX_RENAMING_RULE, TEST_TARGET_DIRECTORY);

    assertEquals(
        formattedName(
            TEST_TARGET_DIRECTORY,
            UNKNOWN_VALUE,
            UNKNOWN_VALUE,
            TEST_SERIES,
            TEST_VOLUME,
            TEST_ISSUE,
            TEST_TITLE,
            TEST_FORMATTED_COVER_DATE,
            TEST_PUBLISHED_MONTH,
            TEST_PUBLISHED_YEAR),
        result);
  }

  @Test
  void createFilenameFromRule_noVolume() {
    Mockito.when(comicDetail.getVolume()).thenReturn(null);

    final String result =
        adaptor.createFilenameFromRule(
            comicDetail, TEST_FULL_COMIC_FILENAME, TEST_UNIX_RENAMING_RULE, TEST_TARGET_DIRECTORY);

    assertEquals(
        formattedName(
            TEST_TARGET_DIRECTORY,
            TEST_PUBLISHER,
            TEST_IMPRINT,
            TEST_SERIES,
            UNKNOWN_VALUE,
            TEST_ISSUE,
            TEST_TITLE,
            TEST_FORMATTED_COVER_DATE,
            TEST_PUBLISHED_MONTH,
            TEST_PUBLISHED_YEAR),
        result);
  }

  @Test
  void createFilenameFromRule_noTitle() {
    Mockito.when(comicDetail.getTitle()).thenReturn(null);

    final String result =
        adaptor.createFilenameFromRule(
            comicDetail, TEST_FULL_COMIC_FILENAME, TEST_UNIX_RENAMING_RULE, TEST_TARGET_DIRECTORY);

    assertEquals(
        formattedName(
            TEST_TARGET_DIRECTORY,
            TEST_PUBLISHER,
            TEST_IMPRINT,
            TEST_SERIES,
            TEST_VOLUME,
            TEST_ISSUE,
            UNKNOWN_VALUE,
            TEST_FORMATTED_COVER_DATE,
            TEST_PUBLISHED_MONTH,
            TEST_PUBLISHED_YEAR),
        result);
  }

  private String formattedName(
      final String targetDirectory,
      final String publisher,
      final String imprint,
      final String series,
      final String volume,
      final String issueNumber,
      final String title,
      final String coverDate,
      final String publishedMonth,
      final String publishedYear) {
    return String.format(
        String.join(
            File.separator, new String[] {"%s", "%s", "%s", "%s", "%s v%s #%s %s %s %s %s %s"}),
        targetDirectory,
        publisher,
        series,
        volume,
        series,
        volume,
        issueNumber,
        title,
        publishedMonth,
        publishedYear,
        coverDate,
        imprint);
  }

  @Test
  void createFilenameFromRule_noIssueNumber() {
    Mockito.when(comicDetail.getIssueNumber()).thenReturn(null);

    final String result =
        adaptor.createFilenameFromRule(
            comicDetail, TEST_FULL_COMIC_FILENAME, TEST_UNIX_RENAMING_RULE, TEST_TARGET_DIRECTORY);

    assertEquals(
        formattedName(
            TEST_TARGET_DIRECTORY,
            TEST_PUBLISHER,
            TEST_IMPRINT,
            TEST_SERIES,
            TEST_VOLUME,
            UNKNOWN_VALUE,
            TEST_TITLE,
            TEST_FORMATTED_COVER_DATE,
            TEST_PUBLISHED_MONTH,
            TEST_PUBLISHED_YEAR),
        result);
  }

  @Test
  void createFilenameFromRule_noCoverDate() {
    Mockito.when(comicDetail.getCoverDate()).thenReturn(null);

    final String result =
        adaptor.createFilenameFromRule(
            comicDetail, TEST_FULL_COMIC_FILENAME, TEST_UNIX_RENAMING_RULE, TEST_TARGET_DIRECTORY);

    assertEquals(
        formattedName(
            TEST_TARGET_DIRECTORY,
            TEST_PUBLISHER,
            TEST_IMPRINT,
            TEST_SERIES,
            TEST_VOLUME,
            TEST_ISSUE,
            TEST_TITLE,
            NO_COVER_DATE,
            TEST_PUBLISHED_MONTH,
            TEST_PUBLISHED_YEAR),
        result);
  }

  @Test
  void createFileFromRule_publisherEndsWithPeriod() {
    Mockito.when(comicDetail.getPublisher()).thenReturn(TEST_PUBLISHER + ".");

    final String result =
        adaptor.createFilenameFromRule(
            comicDetail, TEST_FULL_COMIC_FILENAME, TEST_UNIX_RENAMING_RULE, TEST_TARGET_DIRECTORY);

    assertEquals(
        formattedName(
            TEST_TARGET_DIRECTORY,
            TEST_PUBLISHER + "_",
            TEST_IMPRINT,
            TEST_SERIES,
            TEST_VOLUME,
            TEST_ISSUE,
            TEST_TITLE,
            TEST_FORMATTED_COVER_DATE,
            TEST_PUBLISHED_MONTH,
            TEST_PUBLISHED_YEAR),
        result);
  }

  @Test
  void createFileFromRule_imprintEndsWithPeriod() {
    Mockito.when(comicDetail.getImprint()).thenReturn(TEST_IMPRINT + ".");

    final String result =
        adaptor.createFilenameFromRule(
            comicDetail, TEST_FULL_COMIC_FILENAME, TEST_UNIX_RENAMING_RULE, TEST_TARGET_DIRECTORY);

    assertEquals(
        formattedName(
            TEST_TARGET_DIRECTORY,
            TEST_PUBLISHER,
            TEST_IMPRINT + "_",
            TEST_SERIES,
            TEST_VOLUME,
            TEST_ISSUE,
            TEST_TITLE,
            TEST_FORMATTED_COVER_DATE,
            TEST_PUBLISHED_MONTH,
            TEST_PUBLISHED_YEAR),
        result);
  }

  @Test
  void createFileFromRule_seriesEndsWithPeriod() {
    Mockito.when(comicDetail.getSeries()).thenReturn(TEST_SERIES + ".");

    final String result =
        adaptor.createFilenameFromRule(
            comicDetail, TEST_FULL_COMIC_FILENAME, TEST_UNIX_RENAMING_RULE, TEST_TARGET_DIRECTORY);

    assertEquals(
        formattedName(
            TEST_TARGET_DIRECTORY,
            TEST_PUBLISHER,
            TEST_IMPRINT,
            TEST_SERIES + "_",
            TEST_VOLUME,
            TEST_ISSUE,
            TEST_TITLE,
            TEST_FORMATTED_COVER_DATE,
            TEST_PUBLISHED_MONTH,
            TEST_PUBLISHED_YEAR),
        result);
  }

  @Test
  void createFileFromRule_volumeEndsWithPeriod() {
    Mockito.when(comicDetail.getVolume()).thenReturn(TEST_VOLUME + ".");

    final String result =
        adaptor.createFilenameFromRule(
            comicDetail, TEST_FULL_COMIC_FILENAME, TEST_UNIX_RENAMING_RULE, TEST_TARGET_DIRECTORY);

    assertEquals(
        formattedName(
            TEST_TARGET_DIRECTORY,
            TEST_PUBLISHER,
            TEST_IMPRINT,
            TEST_SERIES,
            TEST_VOLUME + "_",
            TEST_ISSUE,
            TEST_TITLE,
            TEST_FORMATTED_COVER_DATE,
            TEST_PUBLISHED_MONTH,
            TEST_PUBLISHED_YEAR),
        result);
  }

  @Test
  void createFileFromRule_issueNumberEndsWithPeriod() {
    Mockito.when(comicDetail.getIssueNumber()).thenReturn(TEST_ISSUE + ".");

    final String result =
        adaptor.createFilenameFromRule(
            comicDetail, TEST_FULL_COMIC_FILENAME, TEST_UNIX_RENAMING_RULE, TEST_TARGET_DIRECTORY);

    assertEquals(
        formattedName(
            TEST_TARGET_DIRECTORY,
            TEST_PUBLISHER,
            TEST_IMPRINT,
            TEST_SERIES,
            TEST_VOLUME,
            TEST_ISSUE + "_",
            TEST_TITLE,
            TEST_FORMATTED_COVER_DATE,
            TEST_PUBLISHED_MONTH,
            TEST_PUBLISHED_YEAR),
        result);
  }

  @Test
  void isComicFile_cbz() {
    assertTrue(adaptor.isComicFile(new File("filename.cbz")));
  }

  @Test
  void isComicFile_cbr() {
    assertTrue(adaptor.isComicFile(new File("filename.cbr")));
  }

  @Test
  void isComicFile_cb7() {
    assertTrue(adaptor.isComicFile(new File("filename.cb7")));
  }

  @Test
  void findAvailableFilename() {
    final String result =
        adaptor.findAvailableFilename(
            TEST_ORIGINAL_FILENAME, "src/test/resources/notfound", 0, "cbz");

    assertEquals("src/test/resources/notfound.cbz", result);
  }

  @Test
  void findAvailableFilename_usedFilename() {
    // called with attempt 1 to simulate a second call when the original filename fails
    final String result =
        adaptor.findAvailableFilename(
            TEST_EXISTING_FILENAME, "src/test/resources/example", 1, "cbz");

    assertEquals("src/test/resources/example-2.cbz", result);
  }

  @Test
  void findAvailableFilename_alreadyUsed() {
    final String result =
        adaptor.findAvailableFilename(
            TEST_ORIGINAL_FILENAME, "src/test/resources/example", 0, "cbz");

    assertEquals("src/test/resources/example-2.cbz", result);
  }

  @Test
  void findAvailableFilename_matchesExistingName() {
    final String result =
        adaptor.findAvailableFilename(
            TEST_EXISTING_FILENAME, "src/test/resources/example", 0, "cbz");

    assertEquals("src/test/resources/example.cbz", result);
  }

  @Test
  void standardizeFilename_notWindows() {
    String result = adaptor.standardizeFilename(TEST_FULL_COMIC_FILENAME);

    assertNotNull(result);
    assertEquals(TEST_FULL_COMIC_FILENAME, result);
  }

  @Test
  void standardizeFilename_windows() {
    String result = adaptor.standardizeFilename(TEST_FULL_COMIC_FILENAME.replace("/", "\\"));

    assertNotNull(result);
    assertEquals(TEST_FULL_COMIC_FILENAME, result);
  }
}
