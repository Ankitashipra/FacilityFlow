package com.facilityflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * FacilityFlow — Enterprise Facility & Asset Management Platform.
 * <p>
 * Entry point for the Spring Boot application. Enables JPA auditing (for
 * created/updated metadata), scheduling (ticket escalation, reservation
 * cleanup, daily reports), async execution (email/notification dispatch),
 * and caching (Redis-backed dashboard & room lookups).
 */
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableJpaRepositories(basePackages = "com.facilityflow.repository")
@EntityScan(basePackages = "com.facilityflow.entity")
@EnableCaching
@EnableScheduling
@EnableAsync
public class FacilityFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(FacilityFlowApplication.class, args);
    }
}
