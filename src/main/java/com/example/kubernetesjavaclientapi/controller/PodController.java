package com.example.kubernetesjavaclientapi.controller;

import com.example.kubernetesjavaclientapi.dto.pod.PodDto;
import com.example.kubernetesjavaclientapi.payload.request.CreatePodRequest;
import com.example.kubernetesjavaclientapi.payload.request.DeletePodRequest;
import com.example.kubernetesjavaclientapi.payload.request.EditPodRequest;
import com.example.kubernetesjavaclientapi.service.PodService;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Pod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class {@link PodController} for handling Kubernetes pod-related operations.
 */
@RestController
@RequestMapping("/api/v1/kubernetes/pods")
@RequiredArgsConstructor
@Slf4j
public class PodController {

    private final PodService podService;

    /**
     * Retrieves a list of Kubernetes pods.
     *
     * @return A list of {@link PodDto} representing Kubernetes pods.
     * @throws Exception if an error occurs during pod retrieval.
     */
    @GetMapping("/listPods")
    public List<PodDto> listPods() throws Exception {
        return podService.listPods();
    }

    /**
     * Creates a new Kubernetes pod based on the provided request.
     *
     * @param request The {@link CreatePodRequest} containing pod creation details.
     * @return A {@link ResponseEntity} indicating the status of the pod creation operation.
     */
    @PostMapping("/createPod")
    public ResponseEntity<String> createPod(@RequestBody CreatePodRequest request) {
        try {
            V1Pod createdPod = podService.createPod(request);
            // Optionally, you can return more information about the created pod.
            return ResponseEntity.status(HttpStatus.CREATED).body("Pod created successfully: " + createdPod.getMetadata().getName());
        } catch (ApiException e) {
            log.error("Error creating pod", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating pod: " + e.getResponseBody());
        }
    }

    /**
     * Creates a new Kubernetes pod based on the provided request.
     *
     * @param request The {@link CreatePodRequest} containing pod creation details.
     * @return A {@link ResponseEntity} indicating the status of the pod creation operation.
     */
    @PutMapping("/editPod")
    public ResponseEntity<String> editPod(@RequestBody EditPodRequest request){
        try {
            V1Pod editedPod = podService.editPod(request);
            // Optionally, you can return more information about the edited pod.
            return ResponseEntity.status(HttpStatus.OK).body("Pod edited successfully: " + editedPod.getMetadata().getName());
        } catch (ApiException e) {
            log.error("Error editing pod", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error editing pod: " + e.getResponseBody());
        }
    }

    /**
     * Deletes an existing Kubernetes pod based on the provided request.
     *
     * @param request The {@link DeletePodRequest} containing pod deletion details.
     * @return A {@link ResponseEntity} indicating the status of the pod deletion operation.
     */
    @DeleteMapping("/deletePod")
    public ResponseEntity<String> deletePod(@RequestBody DeletePodRequest request){
        try {
            podService.deletePod(request);
            // Optionally, you can return more information about the deleted pod.
            return ResponseEntity.status(HttpStatus.OK).body("Pod deleted successfully: " + request.getPodName());
        } catch (ApiException e) {
            log.error("Error deleting pod", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting pod: " + e.getResponseBody());
        }
    }

}
