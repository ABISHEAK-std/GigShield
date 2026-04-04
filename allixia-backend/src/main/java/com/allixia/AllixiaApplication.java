package com.allixia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AllixiaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AllixiaApplication.class, args);
        System.out.println("\n" +
                "╔═══════════════════════════════════════════════════════════════╗\n" +
                "║                                                               ║\n" +
                "║                    ALLIXIA Backend Started                    ║\n" +
                "║          AI-Powered Parametric Insurance Platform             ║\n" +
                "║                                                               ║\n" +
                "║   Health Check: http://localhost:8080/api/health             ║\n" +
                "║                                                               ║\n" +
                "╚═══════════════════════════════════════════════════════════════╝\n");
    }
}
