package com.studentjobfinder.controller;

import com.studentjobfinder.dto.LearningResourceResponse;
import com.studentjobfinder.service.LearningResourceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/learning-resources")
public class LearningResourceController {

    private final LearningResourceService resourceService;

    public LearningResourceController(LearningResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getResources(
            @RequestParam(required = false) Long skillId) {
        List<LearningResourceResponse> resources = resourceService.getAllResources(skillId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("resources", resources);
        return ResponseEntity.ok(response);
    }
}
