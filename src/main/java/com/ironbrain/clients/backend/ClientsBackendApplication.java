package com.ironbrain.clients.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableCaching
@SpringBootApplication
@EnableJpaRepositories
public class ClientsBackendApplication
{

    public static void main( String[] args )
    {
        SpringApplication.run(ClientsBackendApplication.class, args);
    }
}
