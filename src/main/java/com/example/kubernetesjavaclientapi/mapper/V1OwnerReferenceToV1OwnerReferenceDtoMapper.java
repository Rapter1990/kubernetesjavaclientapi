package com.example.kubernetesjavaclientapi.mapper;

import com.example.kubernetesjavaclientapi.dto.pod.V1OwnerReferenceDto;
import io.kubernetes.client.openapi.models.V1OwnerReference;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface V1OwnerReferenceToV1OwnerReferenceDtoMapper extends BaseMapper<V1OwnerReference, V1OwnerReferenceDto> {

    /**
     * Initializes the mapper.
     *
     * @return the initialized mapper object.
     */
    static V1OwnerReferenceToV1OwnerReferenceDtoMapper initialize() {
        return Mappers.getMapper(V1OwnerReferenceToV1OwnerReferenceDtoMapper.class);
    }
}
