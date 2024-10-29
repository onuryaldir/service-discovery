package com.lokal.lokalgw;

import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import reactor.netty.http.client.HttpClient;


@EnableDiscoveryClient
@SpringBootApplication
public class LokalGwApplication {

    public static void main(String[] args) {
        SpringApplication.run(LokalGwApplication.class, args);


    }
    @Bean
    public HttpClient httpClient() {
        return HttpClient.create().resolver(DefaultAddressResolverGroup.INSTANCE);
    }
}
