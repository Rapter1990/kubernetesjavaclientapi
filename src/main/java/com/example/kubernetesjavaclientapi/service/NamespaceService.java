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

@Service
@RequiredArgsConstructor
@Slf4j
public class NamespaceService {

    private final CoreV1Api coreV1Api;

    private final AppsV1Api appsV1Api;

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

    public V1Namespace createNamespace(CreateNamespaceRequest request) throws ApiException {

        V1Namespace namespace = new V1Namespace();
        namespace.metadata(new V1ObjectMeta().name(request.getName()));

        return coreV1Api.createNamespace(namespace,
                null,
                null,
                null,
                null);
    }

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
