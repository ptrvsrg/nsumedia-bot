package ru.nsu.ccfit.ooad.nsumediabot.material.dao;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpecializationRepository
        extends JpaRepository<Specialization, Long> {

    @NotNull
    @Override
    @Query("SELECT s FROM Specialization s ORDER BY s.name ASC")
    List<Specialization> findAll();

    @Query("SELECT s FROM Specialization s WHERE s.name = :name")
    Optional<Specialization> findByName(@Param("name") String name);
}