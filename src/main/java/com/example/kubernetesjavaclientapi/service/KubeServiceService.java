package com.example.kubernetesjavaclientapi.service;

import com.example.kubernetesjavaclientapi.dto.service.ServiceDto;
import com.example.kubernetesjavaclientapi.payload.request.service.CreateServiceRequest;
import com.example.kubernetesjavaclientapi.payload.request.service.DeleteServiceRequest;
import com.example.kubernetesjavaclientapi.payload.request.service.EditServiceRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class {@link KubeServiceService} for managing Kubernetes namespaces and related operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KubeServiceService {

    private final CoreV1Api coreV1Api;

    private final AppsV1Api appsV1Api;

    /**
     * Retrieves a list of Kubernetes services.
     *
     * @return A list of {@link ServiceDto} representing the Kubernetes services.
     * @throws ApiException If an error occurs while fetching the services.
     */
    public List<ServiceDto> listServices() throws ApiException {

        V1ServiceList serviceList = coreV1Api.listNamespacedService("default",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);

        return serviceList.getItems().stream()
                .map(service ->
                        ServiceDto.builder()
                        .name(service.getMetadata().getName())
                        .uid(service.getMetadata().getUid())
                        .namespace(service.getMetadata().getNamespace())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Creates a new Kubernetes service.
     *
     * @param request The request object containing the details for creating the service.
     * @return The created {@link V1Service} object.
     * @throws ApiException If an error occurs while creating the service.
     */
    public V1Service createService(CreateServiceRequest request) throws ApiException {

        V1Service service = new V1Service();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(request.getName());
        service.setMetadata(metadata);

        // Set the port information
        V1ServiceSpec serviceSpec = new V1ServiceSpec();
        V1ServicePort servicePort = new V1ServicePort();
        servicePort.setPort(80); // Set your desired port number
        serviceSpec.setPorts(Collections.singletonList(servicePort));
        service.setSpec(serviceSpec);

        return coreV1Api.createNamespacedService("default",
                service,
                null,
                null,
                null,
                null);
    }

    /**
     * Edits an existing Kubernetes service.
     *
     * @param request The request object containing the details for editing the service.
     * @return The edited {@link V1Service} object.
     * @throws ApiException If an error occurs while editing the service.
     */
    public V1Service editService(EditServiceRequest request) throws ApiException {

        // Retrieve the existing service
        V1Service existingService = coreV1Api.readNamespacedService(request.getExistingName(), "default", null);

        // Create a new service with the updated name
        V1Service updatedService = new V1Service()
                .metadata(new V1ObjectMeta().name(request.getUpdatedName()))
                .spec(new V1ServiceSpec().ports(existingService.getSpec().getPorts()));

        // Create the new service
        coreV1Api.createNamespacedService("default", updatedService, null, null, null, null);

        moveServiceResources(request.getExistingName(), request.getUpdatedName());

        return updatedService;
    }

    /**
     * Deletes a Kubernetes service.
     *
     * @param request The request object containing the details for deleting the service.
     * @return The deleted {@link V1Service} object.
     * @throws ApiException If an error occurs while deleting the service.
     */
    public V1Service deleteService(DeleteServiceRequest request) throws ApiException {
        V1DeleteOptions deleteOptions = new V1DeleteOptions();
        return coreV1Api.deleteNamespacedService(request.getName(),
                "default",
                null,
                null,
                null,
                null,
                null, deleteOptions);
    }

    /**
     * Moves resources associated with a service to reference the updated service name.
     *
     * @param existingServiceName The name of the existing service.
     * @param updatedServiceName  The updated name of the service.
     * @throws ApiException If an error occurs while moving the resources.
     */
    private void moveServiceResources(String existingServiceName, String updatedServiceName) throws ApiException {
        // Retrieve deployments in the default namespace associated with the existing service
        V1DeploymentList deploymentList = appsV1Api.listNamespacedDeployment("default",
                null,
                null,
                null,
                null,
                "app=" + existingServiceName,
                null,
                null,
                null,
                null,
                null);

        // Update selector labels in each deployment to reference the updated service name
        for (V1Deployment deployment : deploymentList.getItems()) {
            // Ensure that the spec and selector are initialized
            if (deployment.getSpec() == null) {
                deployment.setSpec(new V1DeploymentSpec());
            }

            if (deployment.getSpec().getSelector() == null) {
                deployment.getSpec().setSelector(new V1LabelSelector());
            }

            // Ensure that the matchLabels is initialized
            if (deployment.getSpec().getSelector().getMatchLabels() == null) {
                deployment.getSpec().getSelector().setMatchLabels(new HashMap<>());
            }

            // Update selector labels
            deployment.getSpec().getSelector().getMatchLabels().put("app", updatedServiceName);

            // Update the deployment
            appsV1Api.replaceNamespacedDeployment(deployment.getMetadata().getName(), "default", deployment, null, null, null,null);
        }
    }

}
