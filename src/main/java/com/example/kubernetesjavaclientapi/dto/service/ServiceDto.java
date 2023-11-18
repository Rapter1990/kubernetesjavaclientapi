package com.example.kubernetesjavaclientapi.dto.service;

import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) {@link ServiceDto} representing information about a Kubernetes Service.
 */
@Builder
@Getter
public class ServiceDto {
    private String uid;
    private String name;

    private String namespace;
}
