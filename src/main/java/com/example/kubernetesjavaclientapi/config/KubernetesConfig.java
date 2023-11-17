package com.example.kubernetesjavaclientapi.config;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KubernetesConfig {

    @Bean
    public ApiClient apiClient() throws Exception {
        return Config.defaultClient();
    }

    @Bean
    public CoreV1Api coreV1Api(ApiClient apiClient) {
        return new CoreV1Api(apiClient);
    }
}
