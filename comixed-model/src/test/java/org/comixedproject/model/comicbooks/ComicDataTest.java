package org.comixedproject.model.comicbooks;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ComicDataTest {
  @InjectMocks private ComicData comicData;

  @Test
  void setState_nullState() {
    assertThrows(NullPointerException.class, () -> comicData.setState(null));
  }

  @Test
  void setState() {
    for (int index = 0; index < ComicState.values().length; index++) {
      final ComicState state = ComicState.values()[index];
      comicData.setState(state);
      assertSame(state, comicData.getState());
    }
  }
}
