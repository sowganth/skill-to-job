package com.studentjobfinder.repository;

import com.studentjobfinder.entity.Skill;
import com.studentjobfinder.entity.SkillCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    Optional<Skill> findByNormalizedName(String normalizedName);

    Optional<Skill> findByNameIgnoreCase(String name);

    boolean existsByNormalizedName(String normalizedName);

    List<Skill> findByCategory(SkillCategory category);

    List<Skill> findAllByOrderByCategoryAscNameAsc();
}
