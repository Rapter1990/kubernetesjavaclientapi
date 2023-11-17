package com.example.kubernetesjavaclientapi.payload.request.pods;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * A request class {@link EditPodRequest} representing the information needed to edit a Kubernetes Pod.
 */
@Data
@Builder
public class EditPodRequest {
    private String namespace;
    private String podName;
    Map<String, String> updatedLabels;
}
