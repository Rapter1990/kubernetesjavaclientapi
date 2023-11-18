package com.example.kubernetesjavaclientapi.controller;

import com.example.kubernetesjavaclientapi.base.BaseControllerTest;
import com.example.kubernetesjavaclientapi.dto.deployment.DeploymentDto;
import com.example.kubernetesjavaclientapi.payload.request.deployment.CreateDeploymentRequest;
import com.example.kubernetesjavaclientapi.payload.request.deployment.DeleteDeploymentRequest;
import com.example.kubernetesjavaclientapi.payload.request.deployment.EditDeploymentRequest;
import com.example.kubernetesjavaclientapi.service.DeploymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DeploymentControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DeploymentService deploymentService;

    private static final String BASE_PATH = "/api/v1/kubernetes/deployments";

    @Test
    void testListDeployments() throws Exception {

        List<DeploymentDto> mockDeploymentDtoList = Arrays.asList(
                DeploymentDto.builder().name("deployment1").build(),
                DeploymentDto.builder().name("deployment2").build()
        );

        when(deploymentService.listDeployments()).thenReturn(mockDeploymentDtoList);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/listDeployments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("deployment1"))
                .andExpect(jsonPath("$[1].name").value("deployment2"));

    }

    @Test
    void testCreateDeployment() throws Exception {

        // Given
        CreateDeploymentRequest request = CreateDeploymentRequest.builder()
                .name("example-deployment")
                .replicas(3)
                .labels(Collections.singletonMap("app", "example-app"))
                .containerName("example-container")
                .image("your-docker-image:latest")
                .containerPort(8080)
                .build();

        V1Deployment createdDeployment = new V1Deployment();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(request.getName());
        createdDeployment.metadata(metadata);

        // When
        when(deploymentService.createDeployment(any())).thenReturn(createdDeployment);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + "/createDeployment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(content().contentType(MediaType.valueOf(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")))
                .andExpect(status().isCreated())
                .andExpect(content().string("Deployment created successfully: " + request.getName()));

    }

    @Test
    void testEditDeployment() throws Exception {

        // Given
        EditDeploymentRequest request = EditDeploymentRequest.builder()
                .name("example-deployment")
                .replicas(5)
                .labels(Collections.singletonMap("app", "edited-app"))
                .containerName("edited-container")
                .image("edited-docker-image:latest")
                .containerPort(8081)
                .build();

        V1Deployment editedDeployment = new V1Deployment();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(request.getName());
        editedDeployment.metadata(metadata);

        // When
        when(deploymentService.editDeployment(any())).thenReturn(editedDeployment);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH + "/editDeployment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(content().contentType(MediaType.valueOf(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(content().string("Service edited successfully: " + request.getName()));

    }

    @Test
    void testDeleteDeployment() throws Exception {

        // Given
        DeleteDeploymentRequest request = DeleteDeploymentRequest.builder()
                .name("example-deployment")
                .build();

        V1Status deletionStatus = new V1Status();
        deletionStatus.setMessage("Deployment deleted successfully");

        // When
        when(deploymentService.deleteDeployment(any())).thenReturn(deletionStatus);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/kubernetes/deployments/deleteDeployment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(content().contentType(MediaType.valueOf(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(content().string("Deployment deleted successfully: " + request.getName()));

    }

}
