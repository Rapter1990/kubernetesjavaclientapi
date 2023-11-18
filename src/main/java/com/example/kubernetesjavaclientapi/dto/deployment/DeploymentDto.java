package com.example.kubernetesjavaclientapi.dto.deployment;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DeploymentDto {

    private String uid;
    private String name;

    private String namespace;
}
