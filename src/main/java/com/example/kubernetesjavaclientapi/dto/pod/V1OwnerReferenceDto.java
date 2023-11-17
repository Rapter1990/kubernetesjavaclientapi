package com.example.kubernetesjavaclientapi.dto.pod;

import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) {@link V1OwnerReferenceDto} representing Kubernetes V1OwnerReference information.
 */
@Builder
@Getter
public class V1OwnerReferenceDto {
    private String apiVersion;
    private Boolean blockOwnerDeletion;
    private Boolean controller;
    private String kind;
    private String name;
    private String uid;
}
