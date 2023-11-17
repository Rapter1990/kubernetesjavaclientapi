package com.example.kubernetesjavaclientapi.dto.pod;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class V1OwnerReferenceDto {
    private String apiVersion;
    private Boolean blockOwnerDeletion;
    private Boolean controller;
    private String kind;
    private String name;
    private String uid;
}
