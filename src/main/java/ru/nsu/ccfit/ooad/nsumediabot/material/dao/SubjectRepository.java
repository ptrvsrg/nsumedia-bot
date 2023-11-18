package ru.nsu.ccfit.ooad.nsumediabot.material.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository
        extends JpaRepository<Subject, Long> {

    @Query("SELECT sub FROM Subject sub WHERE sub.specialization.name = :specializationName AND " +
            "sub.semester = :semester ORDER BY sub.name ASC")
    List<Subject> findAllBySpecializationAndSemester(@Param("specializationName") String specializationName,
            @Param("semester") Integer semester);

    @Query("SELECT sub FROM Subject sub WHERE sub.specialization.name = :specializationName AND " +
            "sub.semester = :semester AND sub.name = :subjectName")
    Optional<Subject> findOne(@Param("specializationName") String specializationName,
            @Param("semester") Integer semester,
            @Param("subjectName") String subjectName);
}