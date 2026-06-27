package org.comixedproject.model.comicbooks;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.comixedproject.model.archives.ArchiveType;
import org.comixedproject.model.state.StatefulItem;
import org.comixedproject.views.View;

@Entity
@Table(name = "comic_books")
@NoArgsConstructor
public class ComicData implements StatefulItem<ComicState> {
  @Column(name = "comic_data_id")
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Getter
  private Long comicDataId;

  // comic details

  @Column(name = "filename", nullable = false, unique = true, length = 1024)
  @JsonProperty("filename")
  @JsonView({View.ComicListView.class, View.DeletedPageList.class})
  @Getter
  @Setter
  @NonNull
  private String filename;

  @Column(
      name = "archive_type",
      nullable = false,
      updatable = true,
      columnDefinition = "VARCHAR(4)")
  @Enumerated(EnumType.STRING)
  @JsonProperty("archiveType")
  @JsonView({
    View.ComicListView.class,
    View.DuplicatePageDetail.class,
    View.ReadingListDetail.class,
    View.DeletedPageList.class,
    View.DuplicatePageList.class
  })
  @Getter
  @Setter
  @NonNull
  private ArchiveType archiveType;

  @Column(
      name = "comic_state",
      nullable = false,
      updatable = true,
      columnDefinition = "VARCHAR(64)")
  @Enumerated(EnumType.STRING)
  @JsonProperty("comicState")
  @JsonView({
    View.ComicListView.class,
    View.ReadingListDetail.class,
    View.DeletedPageList.class,
    View.DuplicatePageList.class
  })
  private ComicState comicState = ComicState.CREATED;

  @Column(name = "last_modified_on", updatable = true, nullable = false)
  @JsonProperty("lastModifiedOn")
  @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
  @JsonView({View.ComicListView.class})
  @Temporal(TemporalType.TIMESTAMP)
  @Getter
  @Setter
  private Date lastModifiedOn = new Date();

  // batch processing fields
  @Column(name = "missing", nullable = false, updatable = true)
  @JsonProperty("missing")
  @JsonView({View.ComicDetailsView.class})
  @Getter
  @Setter
  private boolean missing = false;

  @Column(name = "file_contents_loaded", nullable = false, updatable = true)
  @JsonIgnore
  @Getter
  @Setter
  private boolean fileContentsLoaded = false;

  @Column(name = "update_metadata", nullable = false, updatable = true)
  @JsonIgnore
  @Getter
  @Setter
  private boolean updateMetadata = false;

  @Column(name = "batch_metadata_update", nullable = false, updatable = true)
  @JsonIgnore
  @Getter
  @Setter
  private boolean batchMetadataUpdate = false;

  @Column(name = "batch_scraping", nullable = false, updatable = true)
  @JsonIgnore
  @Getter
  @Setter
  private boolean batchScraping = false;

  @Column(name = "organizing", nullable = false, updatable = true)
  @JsonIgnore
  @Getter
  @Setter
  private boolean organizing = false;

  @Column(
      name = "target_archive_type",
      nullable = true,
      updatable = true,
      columnDefinition = "VARCHAR(4)")
  @Enumerated(EnumType.STRING)
  @JsonIgnore
  @Getter
  @Setter
  private ArchiveType targetArchiveType;

  @Column(name = "rename_pages", nullable = false, updatable = true)
  @JsonIgnore
  @Getter
  @Setter
  private boolean renamePages = false;

  @Column(name = "delete_pages", nullable = false, updatable = true)
  @JsonIgnore
  @Getter
  @Setter
  private boolean deletePages = false;

  @Column(name = "edit_details", nullable = false, updatable = true)
  @JsonIgnore
  @Getter
  @Setter
  private boolean editDetails = false;

  @Column(name = "purging", nullable = false, updatable = true)
  @JsonIgnore
  @Getter
  @Setter
  private boolean purging;

  @Override
  public ComicState getState() {
    return this.comicState;
  }

  @Override
  public void setState(@NonNull final ComicState state) {
    this.comicState = state;
  }
}
