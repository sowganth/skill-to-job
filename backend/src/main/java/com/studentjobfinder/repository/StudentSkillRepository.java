package com.studentjobfinder.repository;

import com.studentjobfinder.entity.StudentSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentSkillRepository extends JpaRepository<StudentSkill, Long> {

    @Query("SELECT ss FROM StudentSkill ss JOIN FETCH ss.skill WHERE ss.student.id = :studentId")
    List<StudentSkill> findByStudentIdWithSkill(@Param("studentId") Long studentId);

    List<StudentSkill> findByStudentId(Long studentId);

    Optional<StudentSkill> findByStudentIdAndSkillId(Long studentId, Long skillId);

    boolean existsByStudentIdAndSkillId(Long studentId, Long skillId);

    void deleteByStudentIdAndSkillId(Long studentId, Long skillId);

    long countByStudentId(Long studentId);
}
