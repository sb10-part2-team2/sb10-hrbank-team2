package com.sprint.mission.hrbank.domain.changelog;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtil {

  public static String getClientIp(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For"); // 실제 클라이언트 IP 반환

    if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }

    // X-Forwarded-For는 여러 IP가 쉼표로 구분되어 있을 수 있어서 첫 번째만 가져옴
    if (ip != null && ip.contains(",")) {
      ip = ip.split(",")[0].trim();
    }

    return ip;
  }
}