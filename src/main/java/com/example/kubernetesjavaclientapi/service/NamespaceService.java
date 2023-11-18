package com.example.kubernetesjavaclientapi.service;

import com.example.kubernetesjavaclientapi.dto.namespace.NameSpaceDto;
import com.example.kubernetesjavaclientapi.payload.request.namespace.CreateNamespaceRequest;
import com.example.kubernetesjavaclientapi.payload.request.namespace.DeleteNamespaceRequest;
import com.example.kubernetesjavaclientapi.payload.request.namespace.EditNamespaceRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class {@link NamespaceService} for managing Kubernetes namespaces and related operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NamespaceService {

    private final CoreV1Api coreV1Api;

    private final AppsV1Api appsV1Api;

    /**
     * Retrieves a list of namespaces and maps them to NameSpaceDto objects.
     *
     * @return List of NameSpaceDto objects representing the namespaces.
     * @throws ApiException if there is an error while retrieving the namespaces.
     */
    public List<NameSpaceDto> listNameSpaces() throws ApiException {

        V1NamespaceList list = coreV1Api.listNamespace(
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

        return list.getItems().stream()
                .map(item -> {
                    V1ObjectMeta data = item.getMetadata();
                    return NameSpaceDto.builder()
                            .uid(data.getUid())
                            .namespace(data.getName())
                            .build();

                }).collect(Collectors.toList());

    }

    /**
     * Creates a new namespace based on the provided request.
     *
     * @param request The CreateNamespaceRequest containing information for creating the namespace.
     * @return The created V1Namespace object.
     * @throws ApiException if there is an error while creating the namespace.
     */
    public V1Namespace createNamespace(CreateNamespaceRequest request) throws ApiException {

        V1Namespace namespace = new V1Namespace();
        namespace.metadata(new V1ObjectMeta().name(request.getName()));

        return coreV1Api.createNamespace(namespace,
                null,
                null,
                null,
                null);
    }

    /**
     * Edits an existing namespace based on the provided request.
     *
     * @param request The EditNamespaceRequest containing information for editing the namespace.
     * @return The edited V1Namespace object.
     * @throws ApiException if there is an error while editing the namespace.
     */
    public V1Namespace editNamespace(EditNamespaceRequest request) throws ApiException {

        // Read the existing namespace
        V1Namespace existingNamespace = coreV1Api.readNamespace(request.getExistingName(), null);

        // Create a new namespace with the updated name
        V1Namespace updatedNamespace = new V1Namespace()
                .metadata(new V1ObjectMeta().name(request.getUpdatedName()));

        // Create the new namespace
        coreV1Api.createNamespace(updatedNamespace, null, null, null, null);

        // Move resources from the existing namespace to the updated one
        moveDeployments(request.getExistingName(), request.getUpdatedName());

        // Optionally, delete the existing namespace
        coreV1Api.deleteNamespace(request.getExistingName(), null, null, null, null, null, null);

        return updatedNamespace;
    }

    /**
     * Deletes an existing namespace based on the provided request.
     *
     * @param request The DeleteNamespaceRequest containing information for deleting the namespace.
     * @return The V1Status object representing the deletion status.
     * @throws ApiException if there is an error while deleting the namespace.
     */
    public V1Status deleteNamespace(DeleteNamespaceRequest request) throws ApiException {

        V1DeleteOptions deleteOptions = new V1DeleteOptions();
        return coreV1Api.deleteNamespace(request.getName(),
                null,
                null,
                null,
                null,
                null,
                deleteOptions);
    }

    /**
     * Moves deployments from the existing namespace to the updated namespace.
     *
     * @param existingNamespace The current namespace.
     * @param updatedNamespace  The updated namespace.
     * @throws ApiException if there is an error while moving deployments.
     */
    private void moveDeployments(String existingNamespace, String updatedNamespace) throws ApiException {
        V1DeploymentList deploymentList = appsV1Api.listNamespacedDeployment(existingNamespace,
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

        for (V1Deployment deployment : deploymentList.getItems()) {
            // Update the namespace in metadata
            deployment.getMetadata().setNamespace(updatedNamespace);

            // Create the deployment in the updated namespace
            appsV1Api.createNamespacedDeployment(updatedNamespace, deployment, null, null, null, null);

            // Delete the deployment in the existing namespace
            appsV1Api.deleteNamespacedDeployment(deployment.getMetadata().getName(), existingNamespace, null, null, null, null, null, null);
        }

    }

}
