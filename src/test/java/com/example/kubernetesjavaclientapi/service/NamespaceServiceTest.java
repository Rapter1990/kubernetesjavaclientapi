package com.example.kubernetesjavaclientapi.service;

import com.example.kubernetesjavaclientapi.base.BaseServiceTest;
import com.example.kubernetesjavaclientapi.payload.request.namespace.CreateNamespaceRequest;
import com.example.kubernetesjavaclientapi.payload.request.namespace.DeleteNamespaceRequest;
import com.example.kubernetesjavaclientapi.payload.request.namespace.EditNamespaceRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NamespaceServiceTest extends BaseServiceTest {

    @InjectMocks
    private NamespaceService namespaceService;

    @Mock
    private CoreV1Api coreV1Api;

    @Mock
    private AppsV1Api appsV1Api;

    @Test
    void testListNameSpaces() throws ApiException {
        // Given
        V1NamespaceList namespaceList = new V1NamespaceList();
        V1Namespace namespace = new V1Namespace();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName("test-namespace");
        namespace.setMetadata(metadata);
        namespaceList.setItems(Collections.singletonList(namespace));

        // When
        when(coreV1Api.listNamespace(
                null, null, null, null, null, null, null, null, null, null))
                .thenReturn(namespaceList);

        // Then
        var result = namespaceService.listNameSpaces();

        assertEquals(1, result.size());
        assertEquals("test-namespace", result.get(0).getNamespace());

        // Verify
        verify(coreV1Api, times(1)).listNamespace(
                null, null, null, null, null, null, null, null, null, null);

    }

    @Test
    void testCreateNamespace() throws ApiException {

        // Given
        CreateNamespaceRequest request = CreateNamespaceRequest.builder()
                .name("test-namespace")
                .build();


        V1Namespace createdNamespace = new V1Namespace();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(request.getName());
        createdNamespace.setMetadata(metadata);

        // When
        when(coreV1Api.createNamespace(any(), any(), any(), any(), any()))
                .thenReturn(createdNamespace);

        // Then
        var result = namespaceService.createNamespace(request);

        assertEquals(request.getName(), result.getMetadata().getName());

        // Verify
        verify(coreV1Api, times(1)).createNamespace(
                any(), any(), any(), any(), any());

    }

    @Test
    void testEditNamespace() throws ApiException {

        // Given
        EditNamespaceRequest request = EditNamespaceRequest.builder()
                .existingName("old-namespace")
                .updatedName("new-namespace")
                .build();

        V1Namespace existingNamespace = new V1Namespace();
        V1ObjectMeta existingMetadata = new V1ObjectMeta();
        existingMetadata.setName(request.getExistingName());
        existingNamespace.setMetadata(existingMetadata);

        V1Namespace updatedNamespace = new V1Namespace();
        V1ObjectMeta updatedMetadata = new V1ObjectMeta();
        updatedMetadata.setName(request.getUpdatedName());
        updatedNamespace.setMetadata(updatedMetadata);

        V1DeploymentList deploymentList = new V1DeploymentList();
        V1Deployment deployment = new V1Deployment();
        deployment.setMetadata(new V1ObjectMeta());
        deployment.getMetadata().setName("test-deployment");
        deploymentList.setItems(Collections.singletonList(deployment));

        when(coreV1Api.readNamespace(any(), isNull()))
                .thenReturn(existingNamespace);
        when(coreV1Api.createNamespace(any(), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(updatedNamespace);
        when(appsV1Api.listNamespacedDeployment(any(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull()))
                .thenReturn(deploymentList);

        // When
        var result = namespaceService.editNamespace(request);

        assertEquals(request.getUpdatedName(), result.getMetadata().getName());

        // Then
        verify(coreV1Api, times(1)).readNamespace(any(), isNull());
        verify(coreV1Api, times(1)).createNamespace(any(), isNull(), isNull(), isNull(), isNull());
        verify(appsV1Api, times(1)).listNamespacedDeployment(any(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull());
    }

    @Test
    void testDeleteNamespace() throws ApiException {
        // Given
        DeleteNamespaceRequest request = DeleteNamespaceRequest.builder()
                .name("test-namespace")
                .build();

        V1Status deletionStatus = new V1Status();
        deletionStatus.setStatus("Success");
        deletionStatus.setMessage("Namespace deleted successfully");

        when(coreV1Api.deleteNamespace(any(), isNull(), isNull(), isNull(), isNull(), isNull(), any()))
                .thenReturn(deletionStatus);

        // When
        var result = namespaceService.deleteNamespace(request);

        assertEquals(deletionStatus.getStatus(), result.getStatus());
        assertEquals(deletionStatus.getMessage(), result.getMessage());

        // Then
        verify(coreV1Api, times(1)).deleteNamespace(any(), isNull(), isNull(), isNull(), isNull(), isNull(), any());
    }



}