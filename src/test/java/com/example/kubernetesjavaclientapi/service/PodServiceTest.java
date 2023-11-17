package com.example.kubernetesjavaclientapi.service;

import com.example.kubernetesjavaclientapi.base.BaseServiceTest;
import com.example.kubernetesjavaclientapi.dto.pod.PodDto;
import com.example.kubernetesjavaclientapi.dto.pod.V1ManagedFieldsEntryDto;
import com.example.kubernetesjavaclientapi.dto.pod.V1OwnerReferenceDto;
import com.example.kubernetesjavaclientapi.mapper.V1ManagedFieldsEntryToV1ManagedFieldsEntryDtoMapper;
import com.example.kubernetesjavaclientapi.mapper.V1OwnerReferenceToV1OwnerReferenceDtoMapper;
import com.example.kubernetesjavaclientapi.payload.request.pods.CreatePodRequest;
import com.example.kubernetesjavaclientapi.payload.request.pods.DeletePodRequest;
import com.example.kubernetesjavaclientapi.payload.request.pods.EditPodRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PodServiceTest extends BaseServiceTest {

    @InjectMocks
    private PodService podService;

    @Mock
    private CoreV1Api coreV1Api;

    private final V1ManagedFieldsEntryToV1ManagedFieldsEntryDtoMapper v1ManagedFieldsEntryToV1ManagedFieldsEntryDtoMapper = V1ManagedFieldsEntryToV1ManagedFieldsEntryDtoMapper.initialize();

    private final V1OwnerReferenceToV1OwnerReferenceDtoMapper v1OwnerReferenceToV1OwnerReferenceDtoMapper = V1OwnerReferenceToV1OwnerReferenceDtoMapper.initialize();

    @Test
    public void testListPods() throws Exception {

        // Given
        V1PodList podList = new V1PodList();
        V1Pod pod = new V1Pod();


        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName("TestPod");
        pod.setMetadata(metadata);

        // Mock the mapping methods
        V1ManagedFieldsEntry v1ManagedFieldsEntry = new V1ManagedFieldsEntry()
                .apiVersion("v1")
                .fieldsType("FieldsV1")
                .fieldsV1("example")
                .manager("manager")
                .operation("operation")
                .subresource("subresource")
                .time(OffsetDateTime.now());

        metadata.setManagedFields(Collections.singletonList(v1ManagedFieldsEntry));

        V1OwnerReference v1OwnerReference = new V1OwnerReference()
                .apiVersion("v1")
                .blockOwnerDeletion(true)
                .controller(false)
                .kind("Kind")
                .name("OwnerName")
                .uid("OwnerUid");

        metadata.setOwnerReferences(Collections.singletonList(v1OwnerReference));

        podList.setItems(Collections.singletonList(pod));

        List<PodDto> expected = podList.getItems().stream()
                .map(item -> {
                    V1ObjectMeta data = item.getMetadata();

                    List<V1ManagedFieldsEntryDto> v1ManagedFieldsEntryDtos = v1ManagedFieldsEntryToV1ManagedFieldsEntryDtoMapper.map(data.getManagedFields());
                    List<V1OwnerReferenceDto> v1OwnerReferenceDtos = v1OwnerReferenceToV1OwnerReferenceDtoMapper.map(data.getOwnerReferences());

                    return PodDto.builder()
                            .uid(data.getUid())
                            .name(data.getName())
                            .annotations(data.getAnnotations())
                            .clusterName(data.getClusterName())
                            .creationTimestamp(data.getCreationTimestamp())
                            .deletionGracePeriodSeconds(data.getDeletionGracePeriodSeconds())
                            .deletionTimestamp(data.getDeletionTimestamp())
                            .finalizers(data.getFinalizers())
                            .generateName(data.getGenerateName())
                            .generation(data.getGeneration())
                            .labels(data.getLabels())
                            .managedFields(v1ManagedFieldsEntryDtos)
                            .namespace(data.getNamespace())
                            .ownerReferences(v1OwnerReferenceDtos)
                            .selfLink(data.getSelfLink())
                            .build();
                })
                .collect(Collectors.toList());

        // When
        when(coreV1Api.listPodForAllNamespaces(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()))
                .thenReturn(podList);


        // Then
        List<PodDto> actual = podService.listPods();

        assertEquals(1, actual.size());
        assertEquals(expected.get(0).getName(), actual.get(0).getName());
        assertEquals(expected.get(0).getUid(), actual.get(0).getUid());
        assertEquals(expected.get(0).getManagedFields().size(), actual.get(0).getManagedFields().size());
        assertEquals(expected.get(0).getManagedFields().get(0).getApiVersion(), actual.get(0).getManagedFields().get(0).getApiVersion());

        // Verify
        verify(coreV1Api, times(1)).listPodForAllNamespaces(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any());


    }

    @Test
    public void testCreatePod() throws ApiException {

        // Given
        CreatePodRequest createPodRequest = CreatePodRequest.builder()
                .namespace("default")
                .podName("test-pod")
                .build();

        // Mock the Kubernetes API response
        V1Pod expected = new V1Pod();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(createPodRequest.getPodName());
        expected.setMetadata(metadata);

        // When
        when(coreV1Api.createNamespacedPod(
                any(),
                any(),
                any(),
                any(),
                any(),
                any()))
                .thenReturn(expected);

        // Then
        V1Pod actual = podService.createPod(createPodRequest);

        assertNotNull(actual);
        assertNotNull(actual.getMetadata());
        assertEquals(createPodRequest.getPodName(), actual.getMetadata().getName());

        // Verify
        verify(coreV1Api, times(1)).createNamespacedPod(
                any(),
                any(),
                any(),
                any(),
                any(),
                any());
    }

    @Test
    public void testEditPod() throws ApiException {

        // Given
        EditPodRequest editPodRequest = EditPodRequest.builder()
                .namespace("default")
                .podName("test-pod")
                .updatedLabels(Map.of("key1", "value1", "key2", "value2"))
                .build();

        V1Pod existingPod = new V1Pod();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(editPodRequest.getPodName());
        existingPod.setMetadata(metadata);


        // Initialize labels if null
        if (metadata.getLabels() == null) {
            metadata.setLabels(new HashMap<>());
        }

        metadata.getLabels().putAll(editPodRequest.getUpdatedLabels());

        // When
        when(coreV1Api.readNamespacedPod(
                any(),
                any(),
                any()))
                .thenReturn(existingPod);

        when(coreV1Api.replaceNamespacedPod(
                any(),
                any(),
                eq(existingPod),
                isNull(),
                isNull(),
                isNull(),
                isNull()))
                .thenReturn(existingPod);


        // Then
        V1Pod editedPod = podService.editPod(editPodRequest);

        assertNotNull(editedPod);
        assertEquals(editPodRequest.getPodName(), editedPod.getMetadata().getName());
        assertEquals(editPodRequest.getUpdatedLabels(), editedPod.getMetadata().getLabels());

        // Verify
        verify(coreV1Api, times(1)).readNamespacedPod(
                any(),
                any(),
                any());

        verify(coreV1Api, times(1)).replaceNamespacedPod(
                any(),
                any(),
                eq(existingPod),
                isNull(),
                isNull(),
                isNull(),
                isNull());
    }

    @Test
    public void testDeletePod() throws ApiException {

        // Given
        DeletePodRequest deletePodRequest = DeletePodRequest.builder()
                .namespace("default")
                .podName("test-pod")
                .build();

        V1Pod deletedPod = new V1Pod();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(deletePodRequest.getPodName());
        deletedPod.setMetadata(metadata);

        // When
        when(coreV1Api.deleteNamespacedPod(
                eq(deletePodRequest.getPodName()),
                eq(deletePodRequest.getNamespace()),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull()))
                .thenReturn(deletedPod);

        // Then
        V1Pod v1Pod = podService.deletePod(deletePodRequest);

        // Verify
        assertEquals(null, v1Pod.getKind());
        assertNotNull(deletedPod);
        assertEquals(deletePodRequest.getPodName(), deletedPod.getMetadata().getName());

        // Verify
        verify(coreV1Api, times(1)).deleteNamespacedPod(
                eq(deletePodRequest.getPodName()),
                eq(deletePodRequest.getNamespace()),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull());
    }

}