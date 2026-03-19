package com.sprint.mission.hrbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // 스케줄링 기능 활성화
@EnableJpaAuditing
@SpringBootApplication
public class HrbankApplication {

  public static void main(String[] args) {
    SpringApplication.run(HrbankApplication.class, args);
  }

}