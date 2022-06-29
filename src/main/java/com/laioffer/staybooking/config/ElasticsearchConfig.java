package com.laioffer.staybooking.config;

import org.springframework.context.annotation.Configuration;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

/**
 * Config for Elasticsearch
 */
@Configuration
public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {

    // get elasticsearch.address from application.properties
    @Value("${elasticsearch.address}")
    private String elasticsearchAddress;

    // get elasticsearch.username from application.properties
    @Value("${elasticsearch.username}")
    private String elasticsearchUsername;

    // get elasticsearch.password from application.properties
    @Value("${elasticsearch.password}")
    private String elasticsearchPassword;

    // use this Bean to connect to ElasticSearch
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration
                = ClientConfiguration.builder()
                .connectedTo(elasticsearchAddress)
                .withBasicAuth(elasticsearchUsername, elasticsearchPassword)
                .build();

        return RestClients.create(clientConfiguration).rest();
    }
}


