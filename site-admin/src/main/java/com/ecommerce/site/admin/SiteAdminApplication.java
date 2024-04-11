package com.ecommerce.site.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@SpringBootApplication
@EnableAspectJAutoProxy
@EnableDiscoveryClient
public class SiteAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(SiteAdminApplication.class, args);
    }

}
