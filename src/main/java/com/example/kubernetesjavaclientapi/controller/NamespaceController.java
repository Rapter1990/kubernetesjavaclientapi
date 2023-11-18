package com.example.kubernetesjavaclientapi.controller;

import com.example.kubernetesjavaclientapi.dto.namespace.NameSpaceDto;
import com.example.kubernetesjavaclientapi.payload.request.namespace.CreateNamespaceRequest;
import com.example.kubernetesjavaclientapi.payload.request.namespace.DeleteNamespaceRequest;
import com.example.kubernetesjavaclientapi.payload.request.namespace.EditNamespaceRequest;
import com.example.kubernetesjavaclientapi.service.NamespaceService;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class {@link NamespaceController} for handling Kubernetes namespace-related operations.
 */
@RestController
@RequestMapping("/api/v1/kubernetes/namespaces")
@RequiredArgsConstructor
@Slf4j
public class NamespaceController {

    private final NamespaceService namespaceService;

    /**
     * Retrieves a list of namespaces.
     *
     * @return List of NameSpaceDto objects representing the namespaces.
     * @throws Exception if there is an error while retrieving the namespaces.
     */
    @GetMapping("/listNamespaces")
    public List<NameSpaceDto> listNamespaces() throws Exception {
        return namespaceService.listNameSpaces();
    }

    /**
     * Creates a new namespace based on the provided request.
     *
     * @param request The CreateNamespaceRequest containing information for creating the namespace.
     * @return ResponseEntity with a success message if the namespace is created successfully,
     *         or an error message if the creation fails.
     */
    @PostMapping("/createNamespace")
    public ResponseEntity<String> createNamespace(@RequestBody CreateNamespaceRequest request) {
        try {
            V1Namespace namespace = namespaceService.createNamespace(request);
            // Optionally, you can return more information about the created Namespace.
            return ResponseEntity.status(HttpStatus.CREATED).body("Namespace created successfully: " + namespace.getMetadata().getName());
        } catch (ApiException e) {
            log.error("Error creating namespace", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating Namespace: " + e.getResponseBody());
        }
    }

    /**
     * Edits an existing namespace based on the provided request.
     *
     * @param request The EditNamespaceRequest containing information for editing the namespace.
     * @return ResponseEntity with a success message if the namespace is edited successfully,
     *         or an error message if the editing fails.
     */
    @PutMapping("/editNamespace")
    public ResponseEntity<String> editNamespace(@RequestBody EditNamespaceRequest request){
        try {
            V1Namespace editedNamespace = namespaceService.editNamespace(request);
            // Optionally, you can return more information about the edited pod.
            return ResponseEntity.status(HttpStatus.OK).body("Namespace edited successfully: " + editedNamespace.getMetadata().getName());
        } catch (ApiException e) {
            log.error("Error editing namespace", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error editing namespace: " + e.getResponseBody());
        }
    }

    /**
     * Deletes an existing namespace based on the provided request.
     *
     * @param request The DeleteNamespaceRequest containing information for deleting the namespace.
     * @return ResponseEntity with a success message if the namespace is deleted successfully,
     *         or an error message if the deletion fails.
     */
    @DeleteMapping("/deleteNamespace")
    public ResponseEntity<String> deleteNamespace(@RequestBody DeleteNamespaceRequest request){

        try {
            V1Status deletionStatus = namespaceService.deleteNamespace(request);

            // Check the deletion status or use it in the response if needed
            log.info("Namespace deletion status: {}", deletionStatus.getMessage());

            return ResponseEntity.status(HttpStatus.OK).body("Namespace deleted successfully: " + request.getName());
        } catch (ApiException e) {
            // Handle the exception appropriately
            log.error("Error deleting namespace: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting namespace: " + e.getResponseBody());
        }

    }

}
