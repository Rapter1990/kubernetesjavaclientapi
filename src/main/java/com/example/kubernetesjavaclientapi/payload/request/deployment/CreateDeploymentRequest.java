package com.example.kubernetesjavaclientapi.payload.request.deployment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Represents a request {@link CreateDeploymentRequest} for creating a Kubernetes deployment.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateDeploymentRequest {

    private String name;
    private int replicas;
    private Map<String, String> labels;
    private String containerName;
    private String image;
    private int containerPort;
}
