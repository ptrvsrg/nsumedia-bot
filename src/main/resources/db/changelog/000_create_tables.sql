CREATE TABLE specializations
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255)                            NOT NULL UNIQUE,
    years INTEGER                                 NOT NULL,
    CONSTRAINT pk_specialization PRIMARY KEY (id),
    CHECK ( years >= 1 AND years <= 6 )
);

CREATE FUNCTION get_specialization_semesters(specializationId BIGINT)
    RETURNS BIGINT
AS
'
    SELECT spec.years
    FROM specializations spec
    WHERE spec.id = specializationId
    LIMIT 1;
' LANGUAGE SQL;

CREATE TABLE subjects
(
    id                BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name              VARCHAR(255)                            NOT NULL,
    semester          INTEGER                                 NOT NULL,
    specialization_id BIGINT                                  NOT NULL,
    CONSTRAINT pk_subjects PRIMARY KEY (id),
    CONSTRAINT fk_subjects_on_specialization_id FOREIGN KEY (specialization_id) REFERENCES specializations (id) ON DELETE CASCADE,
    CONSTRAINT uk_subjects_on_name_semester_specialization_id UNIQUE (name, semester, specialization_id),
    CONSTRAINT check_subjects_on_name CHECK ( LENGTH(name) > 0 ),
    CONSTRAINT check_subjects_on_semester CHECK ( get_specialization_semesters(specialization_id) * 2 >= semester AND semester >= 1 )
);

CREATE TABLE materials
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name       VARCHAR(255)                            NOT NULL UNIQUE,
    subject_id BIGINT                                  NOT NULL,
    link       VARCHAR(255)                            NOT NULL,
    CONSTRAINT pk_materials PRIMARY KEY (id),
    CONSTRAINT fk_materials_on_subject_id FOREIGN KEY (subject_id) REFERENCES subjects (id) ON DELETE CASCADE,
    CONSTRAINT fk_materials_on_disk_file_id FOREIGN KEY (disk_file_id) REFERENCES disk_files (id) ON DELETE CASCADE
);
