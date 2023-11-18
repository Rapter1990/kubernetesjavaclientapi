package com.example.kubernetesjavaclientapi.payload.request.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a request {@link EditServiceRequest} for editing a Kubernetes service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EditServiceRequest {

    private String existingName;
    private String updatedName;
}
