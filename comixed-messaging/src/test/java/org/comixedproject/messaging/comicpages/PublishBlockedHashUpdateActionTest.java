/*
 * ComiXed - A digital comic book library management application.
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

package org.comixedproject.messaging.comicpages;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.comixedproject.messaging.PublishingException;
import org.comixedproject.model.comicpages.BlockedHash;
import org.comixedproject.views.View;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@ExtendWith(MockitoExtension.class)
class PublishBlockedHashUpdateActionTest {
  private static final String TEST_BLOCKED_PAGE_AS_JSON = "Object as JSON";

  @InjectMocks private PublishBlockedPageUpdateAction action;
  @Mock private SimpMessagingTemplate messagingTemplate;
  @Mock private ObjectMapper objectMapper;
  @Mock private ObjectWriter objectWriter;
  @Mock private BlockedHash blockedHash;

  @BeforeEach
  public void setUp() throws JsonProcessingException {
    Mockito.when(objectMapper.writerWithView(Mockito.any())).thenReturn(objectWriter);
    Mockito.when(objectWriter.writeValueAsString(Mockito.any()))
        .thenReturn(TEST_BLOCKED_PAGE_AS_JSON);
  }

  @Test
  void publish_jsonProcessingException() throws JsonProcessingException {
    Mockito.when(objectWriter.writeValueAsString(Mockito.any()))
        .thenThrow(JsonProcessingException.class);

    assertThrows(PublishingException.class, () -> action.publish(blockedHash));
  }

  @Test
  void publish() throws PublishingException, JsonProcessingException {
    Mockito.when(objectWriter.writeValueAsString(Mockito.any()))
        .thenReturn(TEST_BLOCKED_PAGE_AS_JSON);

    action.publish(blockedHash);

    Mockito.verify(objectMapper, Mockito.times(1)).writerWithView(View.BlockedHashList.class);
    Mockito.verify(objectWriter, Mockito.times(1)).writeValueAsString(blockedHash);
    Mockito.verify(messagingTemplate, Mockito.times(1))
        .convertAndSend(
            PublishBlockedPageUpdateAction.BLOCKED_HASH_LIST_UPDATE_TOPIC,
            TEST_BLOCKED_PAGE_AS_JSON);
  }
}
