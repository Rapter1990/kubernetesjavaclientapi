package com.example.kubernetesjavaclientapi.controller;

import com.example.kubernetesjavaclientapi.base.BaseControllerTest;
import com.example.kubernetesjavaclientapi.dto.service.ServiceDto;
import com.example.kubernetesjavaclientapi.payload.request.service.CreateServiceRequest;
import com.example.kubernetesjavaclientapi.payload.request.service.DeleteServiceRequest;
import com.example.kubernetesjavaclientapi.payload.request.service.EditServiceRequest;
import com.example.kubernetesjavaclientapi.service.KubeServiceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServicePort;
import io.kubernetes.client.openapi.models.V1ServiceSpec;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ServiceControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private KubeServiceService kubeServiceService;

    private static final String BASE_PATH = "/api/v1/kubernetes/services";


    @Test
    void testListServices() throws Exception {

        // Given
        List<ServiceDto> mockServiceList = Arrays.asList(
                ServiceDto.builder().uid("1").name("Service1").namespace("Namespace1").build(),
                ServiceDto.builder().uid("2").name("Service2").namespace("Namespace2").build()
        );

        // When
        when(kubeServiceService.listServices()).thenReturn(mockServiceList);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/listServices"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testCreateService() throws Exception {

        // Given
        CreateServiceRequest createServiceRequest = CreateServiceRequest.builder()
                .name("SampleService")
                .build();

        // Create a mock V1Service
        V1Service mockService = new V1Service();
        mockService.setMetadata(new V1ObjectMeta().name(createServiceRequest.getName()));
        V1ServiceSpec serviceSpec = new V1ServiceSpec();
        V1ServicePort servicePort = new V1ServicePort();
        servicePort.setPort(80); // Set your desired port number
        serviceSpec.setPorts(Collections.singletonList(servicePort));
        mockService.setSpec(serviceSpec);

        // When
        when(kubeServiceService.createService(createServiceRequest)).thenReturn(mockService);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + "/createService")
                        .content(objectMapper.writeValueAsString(createServiceRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.valueOf(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")))
                .andExpect(MockMvcResultMatchers.content().string("Service created successfully: " + createServiceRequest.getName()));

    }

    @Test
    void testEditService() throws Exception {

        EditServiceRequest editServiceRequest = EditServiceRequest.builder()
                .existingName("new-kubernetes-service")
                .updatedName("updated-kubernetes-service")
                .build();

        // Create a mock V1Service
        V1Service mockService = new V1Service();
        mockService.setMetadata(new V1ObjectMeta().name(editServiceRequest.getUpdatedName()));
        V1ServiceSpec serviceSpec = new V1ServiceSpec();
        V1ServicePort servicePort = new V1ServicePort();
        servicePort.setPort(80); // Set your desired port number
        serviceSpec.setPorts(Collections.singletonList(servicePort));
        mockService.setSpec(serviceSpec);

        when(kubeServiceService.editService(any())).thenReturn(mockService);

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH + "/editService")
                        .content(objectMapper.writeValueAsString(editServiceRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.valueOf(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Service edited successfully: " + editServiceRequest.getUpdatedName()));

    }

    @Test
    void testDeleteService() throws Exception {
        DeleteServiceRequest deleteServiceRequest = DeleteServiceRequest.builder()
                .name("test-kubernetes-service")
                .build();

        // Create a mock V1Service
        V1Service mockService = new V1Service();
        mockService.setMetadata(new V1ObjectMeta().name(deleteServiceRequest.getName()));
        V1ServiceSpec serviceSpec = new V1ServiceSpec();
        V1ServicePort servicePort = new V1ServicePort();
        servicePort.setPort(80); // Set your desired port number
        serviceSpec.setPorts(Collections.singletonList(servicePort));
        mockService.setSpec(serviceSpec);

        when(kubeServiceService.deleteService(deleteServiceRequest)).thenReturn(mockService);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + "/deleteService")
                        .content(objectMapper.writeValueAsString(deleteServiceRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.valueOf(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Service deleted successfully: " + mockService.getMetadata().getName()));
    }

}