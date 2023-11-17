package com.example.kubernetesjavaclientapi.payload.request;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class EditPodRequest {
    private String namespace;
    private String podName;
    Map<String, String> updatedLabels;
}
