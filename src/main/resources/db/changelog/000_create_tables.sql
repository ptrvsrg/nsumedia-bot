CREATE TABLE specializations
(
    id    INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255)                             NOT NULL UNIQUE,
    years INTEGER                                  NOT NULL,
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
    id                INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name              VARCHAR(255)                             NOT NULL,
    semesters         INTEGER                                  NOT NULL,
    specialization_id INTEGER                                  NOT NULL,
    CONSTRAINT pk_subjects PRIMARY KEY (id),
    CONSTRAINT fk_subjects_on_specialization_id FOREIGN KEY (specialization_id) REFERENCES specializations (id) ON DELETE CASCADE,
    CONSTRAINT uk_subjects_on_name_semester_specialization_id UNIQUE (name, semesters, specialization_id),
    CHECK ( get_specialization_semesters(specialization_id) * 2 >= subjects.semesters )
);

CREATE TABLE disk_files
(
    id       INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    link     VARCHAR(255)                             NOT NULL UNIQUE,
    diskPath VARCHAR(255)                             NOT NULL UNIQUE,
    CONSTRAINT pk_disk_files PRIMARY KEY (id)
);

CREATE TABLE materials
(
    id           INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name         VARCHAR(255)                             NOT NULL UNIQUE,
    subject_id   INTEGER                                  NOT NULL,
    disk_file_id INTEGER                                  NOT NULL,
    CONSTRAINT pk_materials PRIMARY KEY (id),
    CONSTRAINT fk_materials_on_subject_id FOREIGN KEY (subject_id) REFERENCES subjects (id) ON DELETE CASCADE,
    CONSTRAINT fk_materials_on_disk_file_id FOREIGN KEY (disk_file_id) REFERENCES disk_files (id) ON DELETE CASCADE
);
