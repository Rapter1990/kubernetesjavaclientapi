package com.example.kubernetesjavaclientapi.payload.request.namespace;

import lombok.Builder;
import lombok.Data;

/**
 * A request class {@link EditNamespaceRequest} representing the information needed to edit a Kubernetes namespace.
 */
@Data
@Builder
public class EditNamespaceRequest {
    private String existingName;
    private String updatedName;
}
