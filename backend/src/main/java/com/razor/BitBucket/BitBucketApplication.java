package com.razor.BitBucket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.razor.BitBucket.repository")
@EntityScan(basePackages = "com.razor.BitBucket.model")
public class BitBucketApplication {
    public static void main(String[] args) {
        SpringApplication.run(BitBucketApplication.class, args);
    }
}
