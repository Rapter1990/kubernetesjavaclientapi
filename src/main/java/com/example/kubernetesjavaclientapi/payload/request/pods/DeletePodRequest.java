package com.example.kubernetesjavaclientapi.payload.request.pods;

import lombok.Builder;
import lombok.Data;

/**
 * A request class {@link DeletePodRequest} representing the information needed to delete a Kubernetes Pod.
 */
@Data
@Builder
public class DeletePodRequest {
    private String namespace;
    private String podName;
}
