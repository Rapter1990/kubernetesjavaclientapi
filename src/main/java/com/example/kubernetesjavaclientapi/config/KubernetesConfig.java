package com.example.kubernetesjavaclientapi.config;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class {@link KubernetesConfig} for setting up Kubernetes-related beans.
 */
@Configuration
public class KubernetesConfig {

    /**
     * Creates and configures an instance of {@link ApiClient} using default settings.
     *
     * @return An initialized {@link ApiClient} instance.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public ApiClient apiClient() throws Exception {
        return Config.defaultClient();
    }

    /**
     * Creates and configures an instance of {@link CoreV1Api} using the provided {@link ApiClient}.
     *
     * @param apiClient The {@link ApiClient} to use for the {@link CoreV1Api} instance.
     * @return An initialized {@link CoreV1Api} instance.
     */
    @Bean
    public CoreV1Api coreV1Api(ApiClient apiClient) {
        return new CoreV1Api(apiClient);
    }

    /**
     * Creates and configures an instance of {@link AppsV1Api} using the provided {@link ApiClient}.
     *
     * @param apiClient The {@link ApiClient} to use for the {@link AppsV1Api} instance.
     * @return An initialized {@link AppsV1Api} instance.
     */
    @Bean
    public AppsV1Api appsV1Api(ApiClient apiClient) {
        return new AppsV1Api(apiClient);
    }
}
