package com.studentjobfinder.controller;

import com.studentjobfinder.dto.DashboardResponse;
import com.studentjobfinder.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard() {
        DashboardResponse data = dashboardService.getDashboardData();
        return ResponseEntity.ok(data);
    }
}
