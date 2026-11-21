package com.studentjobfinder.repository;

import com.studentjobfinder.entity.JobRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRoleRepository extends JpaRepository<JobRole, Long> {

    @Query("SELECT DISTINCT j FROM JobRole j LEFT JOIN FETCH j.jobSkills js LEFT JOIN FETCH js.skill")
    List<JobRole> findAllWithSkills();

    @Query("SELECT j FROM JobRole j LEFT JOIN FETCH j.jobSkills js LEFT JOIN FETCH js.skill WHERE j.id = :id")
    Optional<JobRole> findByIdWithSkills(@Param("id") Long id);

    List<JobRole> findByCategoryIgnoreCase(String category);

    List<JobRole> findByTitleContainingIgnoreCase(String keyword);

    @Query("SELECT DISTINCT j FROM JobRole j LEFT JOIN FETCH j.jobSkills js LEFT JOIN FETCH js.skill " +
           "WHERE LOWER(j.title) LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "   OR LOWER(j.company) LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "   OR LOWER(j.category) LIKE LOWER(CONCAT('%', :kw, '%'))")
    List<JobRole> searchJobs(@Param("kw") String keyword);
}
