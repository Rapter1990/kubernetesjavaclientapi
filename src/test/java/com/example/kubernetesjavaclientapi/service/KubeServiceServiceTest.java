package com.example.kubernetesjavaclientapi.service;

import com.example.kubernetesjavaclientapi.base.BaseServiceTest;
import com.example.kubernetesjavaclientapi.dto.service.ServiceDto;
import com.example.kubernetesjavaclientapi.payload.request.service.CreateServiceRequest;
import com.example.kubernetesjavaclientapi.payload.request.service.DeleteServiceRequest;
import com.example.kubernetesjavaclientapi.payload.request.service.EditServiceRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.*;

class KubeServiceServiceTest extends BaseServiceTest {

    @InjectMocks
    private KubeServiceService kubeServiceService;

    @Mock
    private CoreV1Api coreV1Api;

    @Mock
    private AppsV1Api appsV1Api;

    @Test
    void testListServices() throws ApiException {

        // Given
        V1Service service1 = new V1Service()
                .metadata(new V1ObjectMeta().name("Service1").uid("1").namespace("Namespace1"))
                .spec(new V1ServiceSpec())
                .status(new V1ServiceStatus());
        V1Service service2 = new V1Service()
                .metadata(new V1ObjectMeta().name("Service2").uid("2").namespace("Namespace1"))
                .spec(new V1ServiceSpec())
                .status(new V1ServiceStatus());

        V1ServiceList serviceList = new V1ServiceList().items(Arrays.asList(service1, service2));

        List<ServiceDto> collect = serviceList.getItems().stream()
                .map(service ->
                        ServiceDto.builder()
                                .name(service.getMetadata().getName())
                                .uid(service.getMetadata().getUid())
                                .namespace(service.getMetadata().getNamespace())
                                .build())
                .collect(Collectors.toList());

        // When
        when(coreV1Api.listNamespacedService(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(serviceList);


        // Then
        List<ServiceDto> result = kubeServiceService.listServices();

        // Validate the result
        assertEquals(2, result.size());

        // Validate the content of the result based on your actual implementation
        assertEquals("Service1", result.get(0).getName());
        assertEquals("1", result.get(0).getUid());
        assertEquals("Namespace1", result.get(0).getNamespace());

        assertEquals("Service2", result.get(1).getName());
        assertEquals("2", result.get(1).getUid());
        assertEquals("Namespace1", result.get(1).getNamespace());

        verify(coreV1Api, times(1))
                .listNamespacedService(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());

    }

    @Test
    void testCreateService() throws ApiException {
        // Given
        CreateServiceRequest createServiceRequest = CreateServiceRequest.builder().name("SampleService").build();

        V1Service expectedService = new V1Service();
        V1ObjectMeta expectedMetadata = new V1ObjectMeta();
        expectedMetadata.setName(createServiceRequest.getName());
        expectedService.setMetadata(expectedMetadata);

        V1ServiceSpec expectedSpec = new V1ServiceSpec();
        V1ServicePort expectedServicePort = new V1ServicePort();
        expectedServicePort.setPort(80);
        expectedSpec.setPorts(Collections.singletonList(expectedServicePort));
        expectedService.setSpec(expectedSpec);

        // When
        when(coreV1Api.createNamespacedService(any(), any(), any(), any(), any(), any()))
                .thenReturn(expectedService);

        // Then
        V1Service resultService = kubeServiceService.createService(createServiceRequest);

        assertEquals(expectedService.getMetadata().getName(), resultService.getMetadata().getName());

        // Verify
        verify(coreV1Api, times(1))
                .createNamespacedService("default", expectedService, null, null, null, null);

    }

    @Test
    void testEditService() throws ApiException {

        // Given
        EditServiceRequest editServiceRequest = new EditServiceRequest("ExistingService", "UpdatedService");

        V1Service existingService = new V1Service()
                .metadata(new V1ObjectMeta().name(editServiceRequest.getExistingName()))
                .spec(new V1ServiceSpec()
                        .ports(Arrays.asList(
                                new V1ServicePort().port(80).name("http").protocol("TCP"),
                                new V1ServicePort().port(443).name("https").protocol("TCP")
                        )));

        V1Service updatedService = new V1Service()
                .metadata(new V1ObjectMeta().name(editServiceRequest.getUpdatedName()))
                .spec(new V1ServiceSpec().ports(existingService.getSpec().getPorts()));

        V1DeploymentList deploymentList = new V1DeploymentList();
        V1Deployment deployment = new V1Deployment();
        deployment.setMetadata(new V1ObjectMeta());
        deployment.getMetadata().setName("test-deployment");
        deploymentList.setItems(Collections.singletonList(deployment));

        // When
        when(coreV1Api.readNamespacedService(any(), any(), any()))
                .thenReturn(existingService);

        when(coreV1Api.createNamespacedService(any(), any(), any(), any(), any(), any()))
                .thenReturn(updatedService);

        when(appsV1Api.listNamespacedDeployment(any(),
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

        // Then
        V1Service resultService = kubeServiceService.editService(editServiceRequest);

        assertNotNull(resultService);
        assertEquals(editServiceRequest.getUpdatedName(), resultService.getMetadata().getName());

        // Then
        verify(coreV1Api, times(1))
                .readNamespacedService(any(), any(), any());
        verify(coreV1Api, times(1))
                .createNamespacedService(any(), any(), any(), any(), any(), any());
        verify(appsV1Api, times(1))
                .listNamespacedDeployment(any(), any(), any(), any(), any(),
                        any(), any(), any(), any(), any(), any());

    }

    @Test
    void testDeleteService() throws ApiException {

        // Given
        DeleteServiceRequest deleteServiceRequest = DeleteServiceRequest.builder()
                .name("ServiceToDelete")
                .build();

        V1Service deletedService = new V1Service();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(deleteServiceRequest.getName());
        deletedService.setMetadata(metadata);

        // When
        when(coreV1Api.deleteNamespacedService(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(deletedService);

        // Then
        V1Service resultService = kubeServiceService.deleteService(deleteServiceRequest);

        assertNotNull(resultService);
        assertEquals(deleteServiceRequest.getName(), resultService.getMetadata().getName());

        // Verify
        verify(coreV1Api, times(1))
                .deleteNamespacedService(any(), any(), any(), any(), any(), any(), any(), any());
    }

}