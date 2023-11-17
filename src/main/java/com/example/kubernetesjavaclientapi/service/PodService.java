package com.example.kubernetesjavaclientapi.service;

import com.example.kubernetesjavaclientapi.dto.pod.PodDto;
import com.example.kubernetesjavaclientapi.dto.pod.V1ManagedFieldsEntryDto;
import com.example.kubernetesjavaclientapi.dto.pod.V1OwnerReferenceDto;
import com.example.kubernetesjavaclientapi.mapper.V1ManagedFieldsEntryToV1ManagedFieldsEntryDtoMapper;
import com.example.kubernetesjavaclientapi.mapper.V1OwnerReferenceToV1OwnerReferenceDtoMapper;
import com.example.kubernetesjavaclientapi.payload.request.CreatePodRequest;
import com.example.kubernetesjavaclientapi.payload.request.DeletePodRequest;
import com.example.kubernetesjavaclientapi.payload.request.EditPodRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PodService {

    private final CoreV1Api coreV1Api;

    private final V1ManagedFieldsEntryToV1ManagedFieldsEntryDtoMapper v1ManagedFieldsEntryToV1ManagedFieldsEntryDtoMapper = V1ManagedFieldsEntryToV1ManagedFieldsEntryDtoMapper.initialize();
    private final V1OwnerReferenceToV1OwnerReferenceDtoMapper v1OwnerReferenceToV1OwnerReferenceDtoMapper = V1OwnerReferenceToV1OwnerReferenceDtoMapper.initialize();

    public List<PodDto> listPods() throws Exception {

        V1PodList list = coreV1Api.listPodForAllNamespaces(null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);


        return list.getItems().stream()
                .map(item -> {
                    V1ObjectMeta data = item.getMetadata();
                    log.info("listPods | data | name :" + data.getName());

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
    }

    public V1Pod createPod(CreatePodRequest request) throws ApiException {
        V1Pod pod = new V1Pod();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(request.getPodName());
        pod.setMetadata(metadata);

        V1PodSpec podSpec = new V1PodSpec();

        // Define container
        V1Container container = new V1Container();
        container.setName("example-container");
        container.setImage("nginx:latest");

        podSpec.setContainers(Collections.singletonList(container));
        podSpec.setRestartPolicy("Always");

        pod.setSpec(podSpec);

        return coreV1Api.createNamespacedPod(
                request.getNamespace(),
                pod,
                null,
                null,
                null,
                null);
    }

    public V1Pod editPod(EditPodRequest request) throws ApiException {

            // Retrieve the existing pod
            V1Pod existingPod = coreV1Api.readNamespacedPod(request.getPodName(), request.getNamespace(),null);

            // Update pod metadata (labels in this example)
            V1ObjectMeta metadata = existingPod.getMetadata();
            if (metadata.getLabels() == null) {
                metadata.setLabels(new HashMap<>());
            }
            metadata.getLabels().putAll(request.getUpdatedLabels());

            // Update the pod
            return coreV1Api.replaceNamespacedPod(
                    request.getPodName(),
                    request.getNamespace(),
                    existingPod,
                    null,
                    null,
                    null,
                null);
    }

    public V1Pod deletePod(DeletePodRequest request) throws ApiException {
        return coreV1Api.deleteNamespacedPod(request.getPodName(), request.getNamespace(), null, null, null, null, null, null);
    }
}
