package com.example.kubernetesjavaclientapi.dto.pod;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;

import javax.annotation.Nullable;
import java.time.OffsetDateTime;

@Builder
@Getter
public class V1ManagedFieldsEntryDto {
    private String apiVersion;
    private String fieldsType;
    private Object fieldsV1;
    private String manager;
    private String operation;

    @Nullable
    private String subresource;
    private OffsetDateTime time;
}
