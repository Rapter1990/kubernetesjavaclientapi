package com.example.kubernetesjavaclientapi.payload.request.namespace;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A request class {@link DeleteNamespaceRequest} representing the information needed to delete a Kubernetes namespace.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeleteNamespaceRequest {
    private String name;
}
