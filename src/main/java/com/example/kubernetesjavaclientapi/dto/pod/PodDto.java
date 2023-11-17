package com.example.kubernetesjavaclientapi.dto.pod;

import io.kubernetes.client.openapi.models.V1ManagedFieldsEntry;
import io.kubernetes.client.openapi.models.V1OwnerReference;
import io.micrometer.common.lang.Nullable;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@Getter
public class PodDto {
    private String uid;

    @Nullable
    private String name;

    @Nullable
    private Map<String, String> annotations = new HashMap<String, String>();

    @Nullable
    private String clusterName;
    private OffsetDateTime creationTimestamp;

    @Nullable
    private Long deletionGracePeriodSeconds;

    @Nullable
    private OffsetDateTime deletionTimestamp;

    @Nullable
    private List<String> finalizers = new ArrayList<>();
    private String generateName;

    @Nullable
    private Long generation;
    private Map<String, String> labels = new HashMap<String, String>();
    private List<V1ManagedFieldsEntryDto> managedFields = new ArrayList<>();
    private String namespace;
    private List<V1OwnerReferenceDto> ownerReferences;
    private String resourceVersion;

    @Nullable
    private String selfLink;
}
