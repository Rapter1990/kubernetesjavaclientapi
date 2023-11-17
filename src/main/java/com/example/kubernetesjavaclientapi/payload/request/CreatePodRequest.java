package com.example.kubernetesjavaclientapi.payload.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatePodRequest {
    private String namespace;
    private String podName;
}
