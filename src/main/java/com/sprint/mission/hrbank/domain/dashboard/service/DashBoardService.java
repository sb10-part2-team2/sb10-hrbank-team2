package com.sprint.mission.hrbank.domain.dashboard.service;

import com.sprint.mission.hrbank.domain.dashboard.dto.response.EmployeeTrendDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashBoardService {

  public List<EmployeeTrendDto> getEmployeeTrend(String from, String to, String unit) {
    List<Map<String, Object>> rawData = List.of(
        Map.of("date", "2025-10", "count", 120L),
        Map.of("date", "2025-11", "count", 125L),
        Map.of("date", "2025-12", "count", 124L),
        Map.of("date", "2026-01", "count", 130L),
        Map.of("date", "2026-02", "count", 145L),
        Map.of("date", "2026-03", "count", 150L)
    );

    List<EmployeeTrendDto> result = new ArrayList<>();
    long previousCount = 0;

    for (int i = 0; i < rawData.size(); i++) {
      Map<String, Object> data = rawData.get(i);
      String date = (String) data.get("date");
      long currentCount = (long) data.get("count");

      long change = 0;
      double changeRate = 0.0;

      if (i > 0) {
        change = currentCount - previousCount;

        if (previousCount > 0) {
          changeRate = (double) change / previousCount * 100;
          changeRate = Math.round(changeRate * 10.0) / 10.0;
        } else if (change > 0) {
          changeRate = 100.0;
        }
      }
      result.add(new EmployeeTrendDto(date, currentCount, change, changeRate));

      previousCount = currentCount;
    }
    return result;
  }
}

