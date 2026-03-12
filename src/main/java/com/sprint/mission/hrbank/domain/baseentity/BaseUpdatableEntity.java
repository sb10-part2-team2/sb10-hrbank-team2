package com.sprint.mission.hrbank.domain.baseentity;

import java.time.Instant;
import org.springframework.data.annotation.LastModifiedDate;

public abstract class BaseUpdatableEntity extends BaseEntity {

  @LastModifiedDate
  private Instant updatedAt;


}
