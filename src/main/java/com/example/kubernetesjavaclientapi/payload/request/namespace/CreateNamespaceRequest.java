package com.example.kubernetesjavaclientapi.payload.request.namespace;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A request class {@link CreateNamespaceRequest} representing the information needed to create a new Kubernetes namespace.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateNamespaceRequest {
    private String name;
}
