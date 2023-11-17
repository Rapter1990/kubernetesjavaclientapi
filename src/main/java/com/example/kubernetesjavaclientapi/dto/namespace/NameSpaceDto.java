package com.example.kubernetesjavaclientapi.dto.namespace;

import io.micrometer.common.lang.Nullable;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NameSpaceDto {
    private String uid;

    private String namespace;
}
