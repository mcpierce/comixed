/*
 * ComiXed - A digital comic book library management application.
 * Copyright (C) 2022, The ComiXed Project
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

package org.comixedproject.service.metadata;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.comixedproject.metadata.MetadataAdaptorProvider;
import org.comixedproject.metadata.MetadataAdaptorRegistry;
import org.comixedproject.model.metadata.MetadataSource;
import org.comixedproject.model.metadata.MetadataSourceProperty;
import org.comixedproject.repositories.metadata.MetadataSourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MetadataSourceServiceTest {
  private static final long TEST_SOURCE_ID = 723L;
  private static final String TEST_ADAPTOR_NAME = "Metadata Adaptor Name";
  private static final String TEST_EXISTING_PROPERTY_NAME = "property-one";
  private static final String TEST_EXISTING_PROPERTY_VALUE = "Property value 1";
  private static final String TEST_CREATED_PROPERTY_NAME = "property-two";
  private static final String TEST_CREATED_PROPERTY_VALUE = "Property value 2";
  private static final String TEST_REMOVED_PROPERTY_NAME = "property-three";
  private static final String TEST_REMOVED_PROPERTY_VALUE = "Property value 3";
  private static final String TEST_METADATA_ADAPTOR_NAME = "The adaptor's name";
  private static final String TEST_METADATA_ADAPTOR_PROVIDER_PROPERTY =
      "The provider property name";

  @Captor ArgumentCaptor<MetadataSource> metadataSourceArgumentCaptor;
  @InjectMocks private MetadataSourceService service;
  @Mock private MetadataSourceRepository metadataSourceRepository;
  @Mock private MetadataAdaptorRegistry metadataAdaptorRegistry;
  @Mock private MetadataSource existingSource;
  @Mock private MetadataSource incomingSource;
  @Mock private MetadataSource savedSource;
  @Mock private MetadataAdaptorProvider metadataAdaptorProvider;
  @Mock private MetadataSource existingMetadataSource;
  @Mock private MetadataSource savedMetadataSource;
  @Mock private MetadataSourceProperty metadataSourceProperty;

  private Set<MetadataSourceProperty> sourceProperties = new HashSet<>();
  private List<MetadataAdaptorProvider> metadatAdaptorList = new ArrayList<>();
  private List<MetadataSource> metadataSourceList = new ArrayList<>();
  private Set<MetadataSourceProperty> metadataSourcePropertyList = new HashSet<>();
  private Set<String> metadataAdaptorProviderPropertyList = new HashSet<>();

  @BeforeEach
  public void setUp() {
    metadataAdaptorProviderPropertyList.add(TEST_METADATA_ADAPTOR_PROVIDER_PROPERTY);
    Mockito.when(metadataAdaptorProvider.getProperties())
        .thenReturn(metadataAdaptorProviderPropertyList);
    Mockito.when(metadataAdaptorProvider.getName()).thenReturn(TEST_METADATA_ADAPTOR_NAME);
    metadatAdaptorList.add(metadataAdaptorProvider);
    Mockito.when(metadataAdaptorRegistry.getAdaptors()).thenReturn(metadatAdaptorList);

    metadataSourcePropertyList.add(metadataSourceProperty);
    Mockito.when(existingMetadataSource.getAdaptorName()).thenReturn(TEST_METADATA_ADAPTOR_NAME);
    metadataSourceList.add(existingMetadataSource);

    Mockito.when(metadataSourceRepository.save(metadataSourceArgumentCaptor.capture()))
        .thenReturn(savedMetadataSource);

    Mockito.when(incomingSource.getAdaptorName()).thenReturn(TEST_ADAPTOR_NAME);
    Mockito.when(incomingSource.getPreferred()).thenReturn(false);
    sourceProperties.add(
        new MetadataSourceProperty(
            incomingSource, TEST_EXISTING_PROPERTY_NAME, TEST_EXISTING_PROPERTY_VALUE));
    sourceProperties.add(
        new MetadataSourceProperty(
            incomingSource, TEST_CREATED_PROPERTY_NAME, "  " + TEST_CREATED_PROPERTY_VALUE + "  "));
    Mockito.when(incomingSource.getProperties()).thenReturn(sourceProperties);
  }

  @Test
  void loadMetadataSourcesNoneAvailable() {
    Mockito.when(metadataAdaptorProvider.getName())
        .thenReturn(TEST_METADATA_ADAPTOR_NAME.substring(1));
    Mockito.when(metadataSourceRepository.loadMetadataSources()).thenReturn(metadataSourceList);

    final List<MetadataSource> result = service.loadMetadataSources();

    assertNotNull(result);
    assertFalse(result.isEmpty());

    Mockito.verify(metadataSourceRepository, Mockito.times(2)).loadMetadataSources();
  }

  @Test
  void loadMetadataSourcesNewSourceFound() {
    final List<MetadataSource> emptyMetadataSourceList = new ArrayList<>();
    Mockito.when(metadataSourceRepository.loadMetadataSources())
        .thenReturn(emptyMetadataSourceList, metadataSourceList);

    final List<MetadataSource> result = service.loadMetadataSources();

    final MetadataSource metadataSource = metadataSourceArgumentCaptor.getValue();
    assertNotNull(metadataSource);
    assertFalse(metadataSource.getProperties().isEmpty());
    assertTrue(
        metadataSource
            .getProperties()
            .contains(
                new MetadataSourceProperty(
                    metadataSource, TEST_METADATA_ADAPTOR_PROVIDER_PROPERTY, "")));

    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertSame(existingMetadataSource, result.get(0));

    Mockito.verify(metadataSourceRepository, Mockito.times(2)).loadMetadataSources();
    Mockito.verify(metadataSourceRepository, Mockito.times(1)).save(metadataSource);
  }

  @Test
  void loadMetadataSources() {
    Mockito.when(metadataSourceRepository.loadMetadataSources()).thenReturn(metadataSourceList);

    final List<MetadataSource> result = service.loadMetadataSources();

    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertSame(existingMetadataSource, result.get(0));

    Mockito.verify(metadataSourceRepository, Mockito.times(2)).loadMetadataSources();
  }

  @Test
  void getByIdInvalidId() {
    Mockito.when(metadataSourceRepository.getById(Mockito.anyLong())).thenReturn(null);

    assertThrows(MetadataSourceException.class, () -> service.getById(TEST_SOURCE_ID));
  }

  @Test
  void getById() throws MetadataSourceException {
    Mockito.when(metadataSourceRepository.getById(Mockito.anyLong())).thenReturn(existingSource);

    final MetadataSource result = service.getById(TEST_SOURCE_ID);

    assertNotNull(result);
    assertSame(existingSource, result);

    Mockito.verify(metadataSourceRepository, Mockito.times(1)).getById(TEST_SOURCE_ID);
  }

  @Test
  void getByBeanNameNotFound() {
    Mockito.when(metadataSourceRepository.getByAdaptorName(Mockito.anyString())).thenReturn(null);

    final MetadataSource result = service.getByAdaptorName(TEST_ADAPTOR_NAME);

    assertNull(result);

    Mockito.verify(metadataSourceRepository, Mockito.times(1)).getByAdaptorName(TEST_ADAPTOR_NAME);
  }

  @Test
  void getByBeanName() {
    Mockito.when(metadataSourceRepository.getByAdaptorName(Mockito.anyString()))
        .thenReturn(existingSource);

    final MetadataSource result = service.getByAdaptorName(TEST_ADAPTOR_NAME);

    assertNotNull(result);
    assertSame(existingSource, result);

    Mockito.verify(metadataSourceRepository, Mockito.times(1)).getByAdaptorName(TEST_ADAPTOR_NAME);
  }

  @Test
  void getByNameNotFound() {
    Mockito.when(metadataSourceRepository.getByName(Mockito.anyString())).thenReturn(null);

    final MetadataSource result = service.getByName(TEST_ADAPTOR_NAME);

    assertNull(result);

    Mockito.verify(metadataSourceRepository, Mockito.times(1)).getByName(TEST_ADAPTOR_NAME);
  }

  @Test
  void getByName() {
    Mockito.when(metadataSourceRepository.getByName(Mockito.anyString()))
        .thenReturn(existingSource);

    final MetadataSource result = service.getByName(TEST_ADAPTOR_NAME);

    assertNotNull(result);
    assertSame(existingSource, result);

    Mockito.verify(metadataSourceRepository, Mockito.times(1)).getByName(TEST_ADAPTOR_NAME);
  }

  @Test
  void createExceptionOnSave() {
    Mockito.when(metadataSourceRepository.save(metadataSourceArgumentCaptor.capture()))
        .thenThrow(RuntimeException.class);

    assertThrows(MetadataSourceException.class, () -> service.create(incomingSource));
  }

  @Test
  void create() throws MetadataSourceException {
    Mockito.when(metadataSourceRepository.save(metadataSourceArgumentCaptor.capture()))
        .thenReturn(savedSource);

    final MetadataSource result = service.create(incomingSource);

    assertNotNull(result);
    assertSame(savedSource, result);

    final MetadataSource foundSource = metadataSourceArgumentCaptor.getValue();
    assertNotNull(foundSource);
    assertEquals(TEST_ADAPTOR_NAME, foundSource.getAdaptorName());
    assertFalse(foundSource.getProperties().isEmpty());
    assertTrue(
        foundSource
            .getProperties()
            .contains(
                new MetadataSourceProperty(
                    foundSource, TEST_EXISTING_PROPERTY_NAME, TEST_EXISTING_PROPERTY_VALUE)));
    assertTrue(
        foundSource
            .getProperties()
            .contains(
                new MetadataSourceProperty(
                    foundSource, TEST_CREATED_PROPERTY_NAME, TEST_CREATED_PROPERTY_VALUE)));
    assertFalse(
        foundSource
            .getProperties()
            .contains(
                new MetadataSourceProperty(
                    foundSource, TEST_REMOVED_PROPERTY_NAME, TEST_REMOVED_PROPERTY_VALUE)));

    Mockito.verify(metadataSourceRepository, Mockito.times(1))
        .save(metadataSourceArgumentCaptor.getValue());
  }

  @Test
  void createNewPreferredSource() throws MetadataSourceException {
    Mockito.when(incomingSource.getPreferred()).thenReturn(true);
    Mockito.when(metadataSourceRepository.save(metadataSourceArgumentCaptor.capture()))
        .thenReturn(savedSource);

    final MetadataSource result = service.create(incomingSource);

    assertNotNull(result);
    assertSame(savedSource, result);

    Mockito.verify(metadataSourceRepository, Mockito.times(1)).clearPreferredSource();
  }

  @Test
  void updateInvalidId() {
    Mockito.when(metadataSourceRepository.getById(Mockito.anyLong())).thenReturn(null);

    assertThrows(
        MetadataSourceException.class, () -> service.update(TEST_SOURCE_ID, incomingSource));
  }

  @Test
  void updateExceptionOnSave() {
    Mockito.when(metadataSourceRepository.getById(Mockito.anyLong())).thenReturn(savedSource);
    Mockito.when(metadataSourceRepository.save(metadataSourceArgumentCaptor.capture()))
        .thenThrow(RuntimeException.class);

    assertThrows(
        MetadataSourceException.class, () -> service.update(TEST_SOURCE_ID, incomingSource));
  }

  @Test
  void update() throws MetadataSourceException {
    final Set<MetadataSourceProperty> properties = new HashSet<>();

    properties.add(
        new MetadataSourceProperty(
            incomingSource, TEST_EXISTING_PROPERTY_NAME, TEST_EXISTING_PROPERTY_VALUE));
    properties.add(
        new MetadataSourceProperty(
            incomingSource, TEST_REMOVED_PROPERTY_NAME, TEST_REMOVED_PROPERTY_VALUE));

    Mockito.when(metadataSourceRepository.getById(Mockito.anyLong())).thenReturn(savedSource);
    Mockito.when(savedSource.getProperties()).thenReturn(properties);
    Mockito.when(metadataSourceRepository.save(metadataSourceArgumentCaptor.capture()))
        .thenReturn(savedSource);

    final MetadataSource result = service.update(TEST_SOURCE_ID, incomingSource);

    assertNotNull(result);
    assertSame(savedSource, result);

    final MetadataSource foundSource = metadataSourceArgumentCaptor.getValue();
    assertNotNull(foundSource);
    assertSame(savedSource, foundSource);

    Mockito.verify(savedSource, Mockito.times(1)).setAdaptorName(TEST_ADAPTOR_NAME);

    assertFalse(properties.isEmpty());

    assertTrue(
        properties.contains(
            new MetadataSourceProperty(
                foundSource, TEST_EXISTING_PROPERTY_NAME, TEST_EXISTING_PROPERTY_VALUE)));
    assertTrue(
        properties.contains(
            new MetadataSourceProperty(
                foundSource, TEST_CREATED_PROPERTY_NAME, TEST_CREATED_PROPERTY_VALUE)));
    assertFalse(
        properties.contains(
            new MetadataSourceProperty(
                foundSource, TEST_REMOVED_PROPERTY_NAME, TEST_REMOVED_PROPERTY_VALUE)));

    Mockito.verify(metadataSourceRepository, Mockito.times(1)).getById(TEST_SOURCE_ID);
    Mockito.verify(metadataSourceRepository, Mockito.times(1))
        .save(metadataSourceArgumentCaptor.getValue());
  }

  @Test
  void updatePreferredSource() throws MetadataSourceException {
    Mockito.when(incomingSource.getPreferred()).thenReturn(true);
    Mockito.when(metadataSourceRepository.getById(Mockito.anyLong())).thenReturn(savedSource);
    Mockito.when(metadataSourceRepository.save(metadataSourceArgumentCaptor.capture()))
        .thenReturn(savedSource);

    final MetadataSource result = service.update(TEST_SOURCE_ID, incomingSource);

    assertNotNull(result);
    assertSame(savedSource, result);

    Mockito.verify(metadataSourceRepository, Mockito.times(1)).getById(TEST_SOURCE_ID);
    Mockito.verify(metadataSourceRepository, Mockito.times(1)).clearPreferredSource();
  }

  @Test
  void deleteInvalidId() {
    Mockito.when(metadataSourceRepository.getById(Mockito.anyLong())).thenReturn(null);

    assertThrows(MetadataSourceException.class, () -> service.delete(TEST_SOURCE_ID));
  }

  @Test
  void deleteRepositoryException() {
    Mockito.when(metadataSourceRepository.getById(Mockito.anyLong())).thenReturn(savedSource);
    Mockito.doThrow(RuntimeException.class)
        .when(metadataSourceRepository)
        .delete(Mockito.any(MetadataSource.class));

    assertThrows(MetadataSourceException.class, () -> service.delete(TEST_SOURCE_ID));
  }

  @Test
  void delete() throws MetadataSourceException {
    Mockito.when(metadataSourceRepository.getById(Mockito.anyLong())).thenReturn(savedSource);
    Mockito.when(metadataSourceRepository.loadMetadataSources()).thenReturn(metadataSourceList);

    final List<MetadataSource> result = service.delete(TEST_SOURCE_ID);

    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertTrue(result.contains(existingMetadataSource));

    Mockito.verify(metadataSourceRepository, Mockito.times(1)).getById(TEST_SOURCE_ID);
    Mockito.verify(metadataSourceRepository, Mockito.times(1)).delete(savedSource);
  }
}
