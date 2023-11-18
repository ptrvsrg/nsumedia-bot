package ru.nsu.ccfit.ooad.nsumediabot.material.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MaterialRepository
        extends JpaRepository<Material, Long> {

    @Query("SELECT m FROM Material m WHERE m.subject.specialization.name = :specializationName AND " +
            "m.subject.semester = :semester AND m.subject.name = :subjectName ORDER BY m.name ASC ")
    List<Material> findAll(@Param("specializationName") String specializationName,
            @Param("semester") Integer semester,
            @Param("subjectName") String subjectName);

    @Query("SELECT COUNT(m) > 0 FROM Material m WHERE m.subject.specialization.name = :specializationName AND " +
            "m.subject.semester = :semester AND m.subject.name = :subjectName AND m.name = :materialName")
    boolean exists(@Param("specializationName") String specializationName,
            @Param("semester") Integer semester,
            @Param("subjectName") String subjectName,
            @Param("materialName") String materialName);

    @Modifying
    @Query("DELETE FROM Material m WHERE m.subject.specialization.name = :specializationName AND " +
            "m.subject.semester = :semester AND m.subject.name = :subjectName AND m.name = :materialName")
    void delete(@Param("specializationName") String specializationName,
            @Param("semester") Integer semester,
            @Param("subjectName") String subjectName,
            @Param("materialName") String materialName);
}