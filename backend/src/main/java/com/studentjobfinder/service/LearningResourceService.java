package com.studentjobfinder.service;

import com.studentjobfinder.dto.LearningResourceResponse;
import com.studentjobfinder.entity.LearningResource;
import com.studentjobfinder.entity.Skill;
import com.studentjobfinder.exception.ResourceNotFoundException;
import com.studentjobfinder.repository.LearningResourceRepository;
import com.studentjobfinder.repository.SkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LearningResourceService {

    private final LearningResourceRepository resourceRepository;
    private final SkillRepository skillRepository;

    public LearningResourceService(LearningResourceRepository resourceRepository,
                                   SkillRepository skillRepository) {
        this.resourceRepository = resourceRepository;
        this.skillRepository = skillRepository;
    }

    @Transactional(readOnly = true)
    public List<LearningResourceResponse> getAllResources(Long skillId) {
        List<LearningResource> list;
        if (skillId != null) {
            list = resourceRepository.findBySkillId(skillId);
        } else {
            list = resourceRepository.findAll();
        }
        return list.stream().map(LearningResourceResponse::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LearningResourceResponse> getResourcesForMissingSkills(Collection<String> missingSkillNames) {
        if (missingSkillNames == null || missingSkillNames.isEmpty()) {
            return Collections.emptyList();
        }

        List<LearningResource> results = new java.util.ArrayList<>();
        for (String skillName : missingSkillNames) {
            String normalized = Skill.normalize(skillName);
            List<LearningResource> matching = resourceRepository.findBySkillNormalizedName(normalized);
            results.addAll(matching);
        }

        return results.stream().map(LearningResourceResponse::new).collect(Collectors.toList());
    }

    public static boolean isValidUrl(String url) {
        if (url == null || url.isBlank()) return false;
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            return "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
        } catch (Exception e) {
            return false;
        }
    }
}
