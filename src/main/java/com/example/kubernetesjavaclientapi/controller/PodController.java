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

@RestController
@RequestMapping("/api/v1/kubernetes/pods")
@RequiredArgsConstructor
@Slf4j
public class PodController {

    private final PodService podService;

    @GetMapping("/listPods")
    public List<PodDto> listPods() throws Exception {
        return podService.listPods();
    }

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
