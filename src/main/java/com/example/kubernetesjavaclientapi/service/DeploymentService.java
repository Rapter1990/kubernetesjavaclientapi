package com.example.kubernetesjavaclientapi.service;

import com.example.kubernetesjavaclientapi.dto.deployment.DeploymentDto;
import com.example.kubernetesjavaclientapi.payload.request.deployment.CreateDeploymentRequest;
import com.example.kubernetesjavaclientapi.payload.request.deployment.DeleteDeploymentRequest;
import com.example.kubernetesjavaclientapi.payload.request.deployment.EditDeploymentRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class {@link DeploymentService} for managing Kubernetes deployment and related operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeploymentService {

    private final AppsV1Api appsV1Api;


    /**
     * Lists Deployments in the "default" namespace.
     *
     * @return List of DeploymentDto containing deployment information.
     * @throws ApiException if an error occurs while interacting with the Kubernetes API.
     */
    public List<DeploymentDto> listDeployments() throws ApiException {

        V1DeploymentList deploymentList = appsV1Api.listNamespacedDeployment(
                "default",
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

        return deploymentList.getItems().stream()
                .map(deployment ->
                        DeploymentDto.builder()
                                .uid(deployment.getMetadata().getUid())
                                .name(deployment.getMetadata().getName())
                                .namespace(deployment.getMetadata().getNamespace())
                                .build())
                .collect(Collectors.toList());

    }

    /**
     * Creates a new Deployment based on the provided CreateDeploymentRequest.
     *
     * @param request CreateDeploymentRequest containing deployment creation parameters.
     * @return The created Deployment.
     * @throws ApiException if an error occurs while interacting with the Kubernetes API.
     */
    public V1Deployment createDeployment(CreateDeploymentRequest request) throws ApiException {

        // Create a basic deployment
        V1Deployment deployment = new V1Deployment();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(request.getName());

        // Set the selector to match the labels in the template
        V1LabelSelector selector = new V1LabelSelector();
        selector.setMatchLabels(request.getLabels());
        V1DeploymentSpec spec = new V1DeploymentSpec();
        spec.setSelector(selector);

        // Set the template with metadata and containers
        V1PodTemplateSpec template = new V1PodTemplateSpec();
        V1ObjectMeta templateMetadata = new V1ObjectMeta();
        templateMetadata.setLabels(request.getLabels());
        template.setMetadata(templateMetadata);

        // Add a container to the template
        V1Container container = new V1Container();
        container.setName(request.getContainerName());
        container.setImage(request.getImage());
        container.setPorts(Collections.singletonList(new V1ContainerPort().containerPort(request.getContainerPort())));
        // Set other container properties as needed
        template.setSpec(new V1PodSpec().containers(Collections.singletonList(container)));
        spec.setTemplate(template);

        // Set the deployment's metadata and spec
        deployment.metadata(metadata);
        deployment.spec(spec);

        // Set the number of replicas
        deployment.getSpec().setReplicas(request.getReplicas());

        return appsV1Api.createNamespacedDeployment("default",
                deployment,
                null,
                null,
                null,
                null);
    }

    /**
     * Edits an existing Deployment based on the provided EditDeploymentRequest.
     *
     * @param request EditDeploymentRequest containing deployment modification parameters.
     * @return The edited Deployment.
     * @throws ApiException if an error occurs while interacting with the Kubernetes API.
     */
    public V1Deployment editDeployment(EditDeploymentRequest request) throws ApiException {

        // Retrieve the existing deployment
        V1Deployment existingDeployment = appsV1Api.readNamespacedDeployment(request.getName(), "default", null);

        // Ensure that spec is initialized
        if (existingDeployment.getSpec() == null) {
            existingDeployment.setSpec(new V1DeploymentSpec());
        }

        // Modify the deployment based on the request
        existingDeployment.getMetadata().setLabels(request.getLabels());

        // Ensure that template is initialized
        if (existingDeployment.getSpec().getTemplate() == null) {
            existingDeployment.getSpec().setTemplate(new V1PodTemplateSpec());
        }

        // Ensure that spec is initialized inside the template
        if (existingDeployment.getSpec().getTemplate().getSpec() == null) {
            existingDeployment.getSpec().getTemplate().setSpec(new V1PodSpec());
        }

        // Ensure that containers list is initialized
        if (existingDeployment.getSpec().getTemplate().getSpec().getContainers() == null) {
            existingDeployment.getSpec().getTemplate().getSpec().setContainers(new ArrayList<>());
        }

        // Ensure that there is at least one container in the list
        if (existingDeployment.getSpec().getTemplate().getSpec().getContainers().isEmpty()) {
            existingDeployment.getSpec().getTemplate().getSpec().getContainers().add(new V1Container());
        }

        // Modify the number of replicas
        existingDeployment.getSpec().setReplicas(request.getReplicas());

        // Modify the container details
        V1Container container = existingDeployment.getSpec().getTemplate().getSpec().getContainers().get(0);
        container.setName(request.getContainerName());
        container.setImage(request.getImage());

        // Ensure that ports list is initialized
        if (container.getPorts() == null) {
            container.setPorts(new ArrayList<>());
        }

        // Ensure that there is at least one port in the list
        if (container.getPorts().isEmpty()) {
            container.getPorts().add(new V1ContainerPort());
        }

        // Modify the container port
        container.getPorts().get(0).setContainerPort(request.getContainerPort());

        // Use the Kubernetes client to update the deployment
        return appsV1Api.replaceNamespacedDeployment(request.getName(), "default", existingDeployment, null, null, null,null);
    }

    /**
     * Deletes an existing Deployment based on the provided DeleteDeploymentRequest.
     *
     * @param request DeleteDeploymentRequest containing deployment deletion parameters.
     * @return The status of the deletion operation.
     * @throws ApiException if an error occurs while interacting with the Kubernetes API.
     */
    public V1Status deleteDeployment(DeleteDeploymentRequest request) throws ApiException {

        return appsV1Api.deleteNamespacedDeployment(request.getName(),
                "default",
                null,
                null,
                null,
                null,
                null,
                null);
    }

}
