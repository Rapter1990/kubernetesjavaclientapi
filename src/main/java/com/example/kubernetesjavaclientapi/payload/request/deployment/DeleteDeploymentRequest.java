package com.example.kubernetesjavaclientapi.payload.request.deployment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a request {@link DeleteDeploymentRequest} for deleting a Kubernetes deployment.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeleteDeploymentRequest {

    private String name;
}
