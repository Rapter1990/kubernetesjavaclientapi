package com.example.kubernetesjavaclientapi.payload.request;

import lombok.Builder;
import lombok.Data;

/**
 * A request class {@link CreatePodRequest} representing the information needed to create a new Kubernetes Pod.
 */
@Data
@Builder
public class CreatePodRequest {
    private String namespace;
    private String podName;
}
