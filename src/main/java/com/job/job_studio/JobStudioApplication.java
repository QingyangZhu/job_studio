package com.job.job_studio;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.job.job_studio.mapper")
public class JobStudioApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobStudioApplication.class, args);
    }

}
