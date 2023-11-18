package com.example.kubernetesjavaclientapi.service;

import com.example.kubernetesjavaclientapi.base.BaseServiceTest;
import com.example.kubernetesjavaclientapi.dto.deployment.DeploymentDto;
import com.example.kubernetesjavaclientapi.payload.request.deployment.CreateDeploymentRequest;
import com.example.kubernetesjavaclientapi.payload.request.deployment.DeleteDeploymentRequest;
import com.example.kubernetesjavaclientapi.payload.request.deployment.EditDeploymentRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentList;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Status;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeploymentServiceTest extends BaseServiceTest {

    @InjectMocks
    private DeploymentService deploymentService;

    @Mock
    private AppsV1Api appsV1Api;


    @Test
    void testListDeployments() throws ApiException {
        // Given
        V1Deployment deployment = new V1Deployment();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setUid("1");
        metadata.setName("example-deployment");
        metadata.setNamespace("default");
        deployment.metadata(metadata);

        V1DeploymentList deploymentList = new V1DeploymentList();
        deploymentList.setItems(Collections.singletonList(deployment));

        List<DeploymentDto> mockDeploymentDtoList = deploymentList.getItems().stream()
                .map(deploymentInfo ->
                        DeploymentDto.builder()
                                .uid(deploymentInfo.getMetadata().getUid())
                                .name(deploymentInfo.getMetadata().getName())
                                .namespace(deploymentInfo.getMetadata().getNamespace())
                                .build())
                .collect(Collectors.toList());

        when(appsV1Api.listNamespacedDeployment(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()))
                .thenReturn(deploymentList);

        // When
        List<DeploymentDto> result = deploymentService.listDeployments();

        assertEquals(mockDeploymentDtoList.size(), result.size());
        assertEquals(mockDeploymentDtoList.get(0).getUid(), result.get(0).getUid());
        assertEquals(mockDeploymentDtoList.get(0).getName(), result.get(0).getName());
        assertEquals(mockDeploymentDtoList.get(0).getNamespace(), result.get(0).getNamespace());

        // Verify that the appsV1Api.listNamespacedDeployment method was called with the expected parameters
        verify(appsV1Api, times(1)).listNamespacedDeployment(
                        any(),
                        any(),
                        any(),
                        any(),
                        any(),
                        any(),
                        any(),
                        any(),
                        any(),
                        any(),
                        any());

    }

    @Test
    void testCreateDeployment() throws ApiException {

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

        when(appsV1Api.createNamespacedDeployment(
                any(),
                any(),
                any(),
                any(),
                any(),
                any()))
                .thenReturn(createdDeployment);

        // When
        V1Deployment result = deploymentService.createDeployment(request);

        // Then
        assertNotNull(result);
        assertNotNull(result.getMetadata());
        assertEquals(request.getName(), result.getMetadata().getName());

        // Verify that the appsV1Api.createNamespacedDeployment method was called with the expected parameters
        verify(appsV1Api, times(1)).createNamespacedDeployment(
                any(),
                any(),
                any(),
                any(),
                any(),
                any());
    }


    @Test
    void editDeployment_ShouldReturnV1Deployment() throws ApiException {

        // Given
        EditDeploymentRequest request = EditDeploymentRequest.builder()
                .name("example-deployment")
                .replicas(5)
                .labels(Collections.singletonMap("app", "edited-app"))
                .containerName("edited-container")
                .image("edited-docker-image:latest")
                .containerPort(8081)
                .build();

        V1Deployment existingDeployment = new V1Deployment();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(request.getName());
        existingDeployment.metadata(metadata);

        // When
        when(appsV1Api.readNamespacedDeployment(anyString(), anyString(), any()))
                .thenReturn(existingDeployment);


        when(appsV1Api.replaceNamespacedDeployment(anyString(), anyString(), any(), any(), any(), any(), any()))
                .thenReturn(existingDeployment);

        // Then
        V1Deployment result = deploymentService.editDeployment(request);


        assertNotNull(result);
        assertNotNull(result.getMetadata());
        assertEquals(request.getName(), result.getMetadata().getName());

        // Verify
        verify(appsV1Api, times(1)).readNamespacedDeployment(
                anyString(),
                anyString(),
                any());

        verify(appsV1Api, times(1)).replaceNamespacedDeployment(
                anyString(),
                anyString(),
                any(),
                any(),
                any(),
                any(),
                any());
    }

    @Test
    void testDeleteDeployment() throws ApiException {

        // Given
        DeleteDeploymentRequest request = DeleteDeploymentRequest.builder()
                .name("example-deployment")
                .build();

        V1Status deletionStatus = new V1Status();
        deletionStatus.setMessage("Deployment deleted successfully");

        // When
        when(appsV1Api.deleteNamespacedDeployment(anyString(), anyString(), any(), any(), any(), any(), any(), any()))
                .thenReturn(deletionStatus);

        // Then
        V1Status result = deploymentService.deleteDeployment(request);

        assertNotNull(result);
        assertEquals("Deployment deleted successfully", result.getMessage());

        // Verify
        verify(appsV1Api, times(1)).deleteNamespacedDeployment(
                anyString(),
                anyString(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any());
    }

}