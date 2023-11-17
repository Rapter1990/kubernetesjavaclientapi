package com.example.kubernetesjavaclientapi.payload.request.namespace;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EditNamespaceRequest {
    private String existingName;
    private String updatedName;
}
