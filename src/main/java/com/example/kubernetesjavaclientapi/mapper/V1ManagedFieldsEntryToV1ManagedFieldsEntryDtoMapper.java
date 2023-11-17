package com.example.kubernetesjavaclientapi.mapper;

import com.example.kubernetesjavaclientapi.dto.pod.V1ManagedFieldsEntryDto;
import io.kubernetes.client.openapi.models.V1ManagedFieldsEntry;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface V1ManagedFieldsEntryToV1ManagedFieldsEntryDtoMapper extends BaseMapper<V1ManagedFieldsEntry, V1ManagedFieldsEntryDto> {

    /**
     * Initializes the mapper.
     *
     * @return the initialized mapper object.
     */
    static V1ManagedFieldsEntryToV1ManagedFieldsEntryDtoMapper initialize() {
        return Mappers.getMapper(V1ManagedFieldsEntryToV1ManagedFieldsEntryDtoMapper.class);
    }

}
