package com.sprint.mission.hrbank.domain.employee.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//QueryDSL 쿼리를 만들 수 있는 JPAQueryFactory를 Spring Bean으로 등록하는 설정
@Configuration
public class QuerydslConfig {

  //EntityManger를 가져온다
  @PersistenceContext
  private EntityManager em;

  // 쿼리 생성하는 쿼리 팩토리를 빈으로 설정 -> 여러 레포지토리에서도 자동으로 DI 받을 수 있게끔 한다.
  @Bean
  public JPAQueryFactory jpaQueryFactory() {
    return new JPAQueryFactory(em);
  }
}
