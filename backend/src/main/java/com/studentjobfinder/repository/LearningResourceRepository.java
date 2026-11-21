package com.studentjobfinder.repository;

import com.studentjobfinder.entity.LearningResource;
import com.studentjobfinder.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface LearningResourceRepository extends JpaRepository<LearningResource, Long> {

    List<LearningResource> findBySkillId(Long skillId);

    List<LearningResource> findBySkillIn(Collection<Skill> skills);

    List<LearningResource> findBySkillNormalizedName(String normalizedName);
}
