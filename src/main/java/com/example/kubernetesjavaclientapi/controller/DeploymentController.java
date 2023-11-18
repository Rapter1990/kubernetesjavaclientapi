package com.example.kubernetesjavaclientapi.controller;

import com.example.kubernetesjavaclientapi.dto.deployment.DeploymentDto;
import com.example.kubernetesjavaclientapi.payload.request.deployment.CreateDeploymentRequest;
import com.example.kubernetesjavaclientapi.payload.request.deployment.DeleteDeploymentRequest;
import com.example.kubernetesjavaclientapi.payload.request.deployment.EditDeploymentRequest;
import com.example.kubernetesjavaclientapi.service.DeploymentService;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class {@link DeploymentController} for handling Kubernetes deployment-related operations.
 */
@RestController
@RequestMapping("/api/v1/kubernetes/deployments")
@RequiredArgsConstructor
@Slf4j
public class DeploymentController {

    private final DeploymentService deploymentService;

    /**
     * Retrieves a list of Deployments.
     *
     * @return List of DeploymentDto containing deployment information.
     * @throws Exception if an error occurs while retrieving the deployments.
     */
    @GetMapping("/listDeployments")
    public List<DeploymentDto> listDeployments() throws Exception {
        return deploymentService.listDeployments();
    }

    /**
     * Creates a new Deployment based on the provided request.
     *
     * @param request CreateDeploymentRequest containing deployment creation parameters.
     * @return ResponseEntity indicating the status of the creation operation.
     */
    @PostMapping("/createDeployment")
    public ResponseEntity<String> createDeployment(@RequestBody CreateDeploymentRequest request) {
        try {
            V1Deployment deployment = deploymentService.createDeployment(request);

            return ResponseEntity.status(HttpStatus.CREATED).body("Deployment created successfully: " + deployment.getMetadata().getName());
        } catch (ApiException e) {
            log.error("Error creating deployment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating deployment: " + e.getResponseBody());
        }
    }

    /**
     * Edits an existing Deployment based on the provided request.
     *
     * @param request EditDeploymentRequest containing deployment modification parameters.
     * @return ResponseEntity indicating the status of the edit operation.
     */
    @PutMapping("/editDeployment")
    public ResponseEntity<String> editDeployment(@RequestBody EditDeploymentRequest request){
        try {
            V1Deployment editDeployment = deploymentService.editDeployment(request);
            return ResponseEntity.status(HttpStatus.OK).body("Service edited successfully: " + editDeployment.getMetadata().getName());
        } catch (ApiException e) {
            log.error("Error editing deployment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error editing deployment: " + e.getResponseBody());
        }
    }

    /**
     * Deletes an existing Deployment based on the provided request.
     *
     * @param request DeleteDeploymentRequest containing deployment deletion parameters.
     * @return ResponseEntity indicating the status of the deletion operation.
     */
    @DeleteMapping("/deleteDeployment")
    public ResponseEntity<String> deleteDeployment(@RequestBody DeleteDeploymentRequest request){

        try {
            V1Status deletionStatus = deploymentService.deleteDeployment(request);

            // Check the deletion status or use it in the response if needed
            log.info("Namespace deletion status: {}", deletionStatus.getMessage());

            return ResponseEntity.status(HttpStatus.OK).body("Deployment deleted successfully: " + request.getName());
        } catch (ApiException e) {
            // Handle the exception appropriately
            log.error("Error deleting deployment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting deployment: " + e.getResponseBody());
        }

    }

}
