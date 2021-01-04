package com.kgc.kmall.kmallsearchservice.config;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfig {
    @Value("${JestClient.host}")
    public String ClientHost;

    @Bean
    public JestClient getJestCline() {
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder(ClientHost)
                .multiThreaded(true)
                .build());
        return factory.getObject();
    }
}
