package ru.nsu.ccfit.ooad.nsumediabot.material.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "specializations")
public class Specialization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "years", nullable = false)
    private Integer years;

    @OneToMany(mappedBy = "specialization")
    private Set<Subject> subjects = new LinkedHashSet<>();

}