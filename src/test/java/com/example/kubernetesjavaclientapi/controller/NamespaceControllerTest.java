package com.example.kubernetesjavaclientapi.controller;

import com.example.kubernetesjavaclientapi.base.BaseControllerTest;
import com.example.kubernetesjavaclientapi.dto.namespace.NameSpaceDto;
import com.example.kubernetesjavaclientapi.payload.request.namespace.CreateNamespaceRequest;
import com.example.kubernetesjavaclientapi.payload.request.namespace.DeleteNamespaceRequest;
import com.example.kubernetesjavaclientapi.payload.request.namespace.EditNamespaceRequest;
import com.example.kubernetesjavaclientapi.service.NamespaceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Status;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class NamespaceControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NamespaceService namespaceService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_PATH = "/api/v1/kubernetes/namespaces";

    @Test
    public void testListNamespaces() throws Exception {

        // Given
        List<NameSpaceDto> expectedNamespaces = Arrays.asList(
                NameSpaceDto.builder().uid("uid1").namespace("namespace1").build(),
                NameSpaceDto.builder().uid("uid2").namespace("namespace2").build()
        );

        // When
        when(namespaceService.listNameSpaces()).thenReturn(expectedNamespaces);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/listNamespaces"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(expectedNamespaces.size())))
                .andExpect(jsonPath("$[0].uid", Matchers.is("uid1")))
                .andExpect(jsonPath("$[0].namespace", Matchers.is("namespace1")))
                .andExpect(jsonPath("$[1].uid", Matchers.is("uid2")))
                .andExpect(jsonPath("$[1].namespace", Matchers.is("namespace2")));

    }

    @Test
    public void testCreateNamespace() throws Exception {

        // Given
        CreateNamespaceRequest createNamespaceRequest = CreateNamespaceRequest.builder()
                .name("test-namespace")
                .build();

        V1Namespace createdNamespace = new V1Namespace();
        createdNamespace.setMetadata(new V1ObjectMeta());
        createdNamespace.getMetadata().setName(createNamespaceRequest.getName());

        // When
        when(namespaceService.createNamespace(any())).thenReturn(createdNamespace);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + "/createNamespace")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createNamespaceRequest)))
                .andDo(print())
                .andExpect(content().contentType(MediaType.valueOf(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")))
                .andExpect(status().isCreated())
                .andExpect(content().string("Namespace created successfully: " + createNamespaceRequest.getName()));
    }

    @Test
    public void testEditNamespace() throws Exception {

        // Given
        EditNamespaceRequest editNamespaceRequest = EditNamespaceRequest.builder()
                .existingName("old-namespace")
                .updatedName("new-namespace")
                .build();

        V1Namespace editedNamespace = new V1Namespace();
        editedNamespace.setMetadata(new V1ObjectMeta());
        editedNamespace.getMetadata().setName(editNamespaceRequest.getUpdatedName());

        // When
        when(namespaceService.editNamespace(any())).thenReturn(editedNamespace);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH + "/editNamespace")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editNamespaceRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                .andExpect(content().string("Namespace edited successfully: " + editNamespaceRequest.getUpdatedName()));
    }

    @Test
    public void testDeleteNamespace() throws Exception {

        // Given
        DeleteNamespaceRequest deleteNamespaceRequest = DeleteNamespaceRequest.builder()
                .name("namespace-to-delete")
                .build();

        V1Status deletionStatus = new V1Status();
        deletionStatus.setStatus("Success");
        deletionStatus.setMessage("Namespace deleted successfully");

        // When
        when(namespaceService.deleteNamespace(any())).thenReturn(deletionStatus);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + "/deleteNamespace")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteNamespaceRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                .andExpect(content().string("Namespace deleted successfully: " + deleteNamespaceRequest.getName()));

    }

}