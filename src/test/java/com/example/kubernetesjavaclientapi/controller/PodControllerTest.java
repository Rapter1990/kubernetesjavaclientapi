package com.example.kubernetesjavaclientapi.controller;

import com.example.kubernetesjavaclientapi.base.BaseControllerTest;
import com.example.kubernetesjavaclientapi.dto.pod.PodDto;
import com.example.kubernetesjavaclientapi.payload.request.CreatePodRequest;
import com.example.kubernetesjavaclientapi.payload.request.DeletePodRequest;
import com.example.kubernetesjavaclientapi.payload.request.EditPodRequest;
import com.example.kubernetesjavaclientapi.service.PodService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Pod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PodControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PodService podService;

    private static final String BASE_PATH = "/api/v1/kubernetes/pods";

    @Test
    public void testListPods() throws Exception {
        // Given
        PodDto podDto = PodDto.builder()
                .uid("TestUid")
                .name("TestPod")
                .build();

        // When
        when(podService.listPods()).thenReturn(Collections.singletonList(podDto));

        // sng<Then
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/listPods")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].uid").value("TestUid"))
                .andExpect(jsonPath("$[0].name").value("TestPod"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andReturn();
    }

    @Test
    void testCreatePod() throws Exception {

        // Given
        CreatePodRequest createPodRequest = CreatePodRequest.builder()
                .namespace("default")
                .podName("test-pod")
                .build();

        V1Pod createdPod = new V1Pod();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(createPodRequest.getPodName());
        createdPod.setMetadata(metadata);

        // When
        when(podService.createPod(any())).thenReturn(createdPod);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + "/createPod")
                        .content(objectMapper.writeValueAsString(createPodRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("Pod created successfully: " + createPodRequest.getPodName()));
    }

    @Test
    public void testEditPod() throws Exception {

        // Given
        EditPodRequest editPodRequest = EditPodRequest.builder()
                .namespace("default")
                .podName("test-pod")
                .updatedLabels(Map.of("key1", "value1", "key2", "value2"))
                .build();

        V1Pod editedPod = new V1Pod();

        // Ensure metadata is initialized
        if (editedPod.getMetadata() == null) {
            editedPod.setMetadata(new V1ObjectMeta());
        }

        editedPod.getMetadata().setName(editPodRequest.getPodName());

        when(podService.editPod(any())).thenReturn(editedPod);

        // When/Then
        mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH + "/editPod")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editPodRequest)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Pod edited successfully: " + editPodRequest.getPodName()));

        // Verify that podService.editPod is called with the correct argument
        verify(podService, times(1)).editPod(eq(editPodRequest));
    }

    @Test
    public void testDeletePod() throws Exception {

        // Given
        DeletePodRequest deletePodRequest = DeletePodRequest.builder()
                .namespace("default")
                .podName("test-pod")
                .build();

        // When/Then
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + "/deletePod")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deletePodRequest)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Pod deleted successfully: " + deletePodRequest.getPodName()));

        // Verify that podService.deletePod is called with the correct argument
        verify(podService, times(1)).deletePod(any());
    }

}