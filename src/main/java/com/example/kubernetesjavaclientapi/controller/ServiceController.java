package com.example.kubernetesjavaclientapi.controller;

import com.example.kubernetesjavaclientapi.dto.service.ServiceDto;
import com.example.kubernetesjavaclientapi.payload.request.service.CreateServiceRequest;
import com.example.kubernetesjavaclientapi.payload.request.service.DeleteServiceRequest;
import com.example.kubernetesjavaclientapi.payload.request.service.EditServiceRequest;
import com.example.kubernetesjavaclientapi.service.KubeServiceService;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class {@link ServiceController} for handling Kubernetes service-related operations.
 */
@RestController
@RequestMapping("/api/v1/kubernetes/services")
@RequiredArgsConstructor
@Slf4j
public class ServiceController {

    private final KubeServiceService kubeServiceService;

    /**
     * Endpoint to retrieve a list of all Kubernetes services.
     *
     * @return A list of {@link ServiceDto} representing the Kubernetes services.
     * @throws Exception If an error occurs while fetching the services.
     */
    @GetMapping("/listServices")
    public List<ServiceDto> listServices() throws Exception {
        return kubeServiceService.listServices();
    }

    /**
     * Endpoint to create a new Kubernetes service.
     *
     * @param request The request body containing the details for creating the service.
     * @return A ResponseEntity indicating the result of the service creation.
     */
    @PostMapping("/createService")
    public ResponseEntity<String> createService(@RequestBody CreateServiceRequest request) {
        try {
            V1Service service = kubeServiceService.createService(request);
            // Optionally, you can return more information about the created Namespace.
            return ResponseEntity.status(HttpStatus.CREATED).body("Service created successfully: " + service.getMetadata().getName());
        } catch (ApiException e) {
            log.error("Error creating service", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating Service: " + e.getResponseBody());
        }
    }

    /**
     * Endpoint to edit an existing Kubernetes service.
     *
     * @param request The request body containing the details for editing the service.
     * @return A ResponseEntity indicating the result of the service editing.
     */
    @PutMapping("/editService")
    public ResponseEntity<String> editService(@RequestBody EditServiceRequest request){
        try {
            V1Service editedService = kubeServiceService.editService(request);
            return ResponseEntity.status(HttpStatus.OK).body("Service edited successfully: " + request.getUpdatedName());
        } catch (ApiException e) {
            log.error("Error editing service", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error editing Service: " + e.getResponseBody());
        }
    }

    /**
     * Endpoint to delete a Kubernetes service.
     *
     * @param request The request body containing the details for deleting the service.
     * @return A ResponseEntity indicating the result of the service deletion.
     */
    @DeleteMapping("/deleteService")
    public ResponseEntity<String> deletePod(@RequestBody DeleteServiceRequest request){
        try {
            V1Service v1Service = kubeServiceService.deleteService(request);

            return ResponseEntity.status(HttpStatus.OK).body("Service deleted successfully: " + v1Service.getMetadata().getName());
        } catch (ApiException e) {
            log.error("Error deleting service", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting service: " + e.getResponseBody());
        }
    }

}
