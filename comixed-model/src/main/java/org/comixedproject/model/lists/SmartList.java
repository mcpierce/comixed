package org.comixedproject.model.lists;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import org.comixedproject.model.library.DisplayableComic;

public class SmartList {
  @Getter private Set<DisplayableComic> entries = new HashSet<>();
}
