package com.sprint.mission.hrbank.domain.dashboard.service;

import com.sprint.mission.hrbank.domain.dashboard.dto.response.EmployeeDistributionResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashBoardService {

  public List<EmployeeDistributionResponse> getEmployeeDistribution(String groupBy) {
    Map<String, Long> distributionData = new HashMap<>();
    if ("position".equalsIgnoreCase(groupBy)) {
      distributionData.put("백엔드 개발자", 40L);
      distributionData.put("프론트엔드 개발자", 30L);
      distributionData.put("디자이너", 10L);
      distributionData.put("기획자", 20L);
    } else {
      distributionData.put("개발팀", 70L);
      distributionData.put("디자인팀", 10L);
      distributionData.put("기획팀", 20L);
    }

    long totalEmployees = distributionData.values().stream()
        .mapToLong(Long::longValue)
        .sum();

    return distributionData.entrySet().stream()
        .map(entry -> {
          String label = entry.getKey();
          long count = entry.getValue();

          double percentage = 0.0;
          if (totalEmployees > 0) {
            percentage = (double) count / totalEmployees * 100;
            percentage = Math.round(percentage * 10.0) / 10.0;
          }

          return new EmployeeDistributionResponse(label, count, percentage);
        })
        .collect(Collectors.toList());
  }
}
