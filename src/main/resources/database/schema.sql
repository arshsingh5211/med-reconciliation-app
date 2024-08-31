BEGIN TRANSACTION;

DROP TABLE IF EXISTS Patient, Medication, Interaction, AuditLog CASCADE;
DROP SEQUENCE IF EXISTS seq_patient_id, seq_medication_id, seq_interaction_id, seq_auditlog_id;

-- Sequence for Patient IDs
CREATE SEQUENCE seq_patient_id
  INCREMENT BY 1
  NO MAXVALUE
  NO MINVALUE
  CACHE 1;

-- Patient Table
CREATE TABLE Patient (
    patient_id int DEFAULT nextval('seq_patient_id'::regclass) NOT NULL,
    first_name varchar(50) NOT NULL,
    last_name varchar(50) NOT NULL,
    dob DATE,
    primary_doctor varchar(100),
    diseases TEXT,
    emergency_contact_name varchar(100),
    emergency_contact_phone varchar(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT PK_patient PRIMARY KEY (patient_id)
);

-- Sequence for Medication IDs
CREATE SEQUENCE seq_medication_id
  INCREMENT BY 1
  NO MAXVALUE
  NO MINVALUE
  CACHE 1;

-- Medication Table
CREATE TABLE Medication (
    medication_id int DEFAULT nextval('seq_medication_id'::regclass) NOT NULL,
    name varchar(100) NOT NULL,
    dosage varchar(50),
    frequency varchar(50),
    patient_id int NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT PK_medication PRIMARY KEY (medication_id),
    CONSTRAINT FK_medication_patient FOREIGN KEY (patient_id) REFERENCES Patient(patient_id)
);

-- Sequence for Interaction IDs
CREATE SEQUENCE seq_interaction_id
  INCREMENT BY 1
  NO MAXVALUE
  NO MINVALUE
  CACHE 1;

-- Interaction Table (if applicable)
CREATE TABLE Interaction (
    interaction_id int DEFAULT nextval('seq_interaction_id'::regclass) NOT NULL,
    medication_a_id int NOT NULL,
    medication_b_id int NOT NULL,
    severity varchar(10) CHECK (severity IN ('mild', 'moderate', 'severe')),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT PK_interaction PRIMARY KEY (interaction_id),
    CONSTRAINT FK_interaction_med_a FOREIGN KEY (medication_a_id) REFERENCES Medication(medication_id),
    CONSTRAINT FK_interaction_med_b FOREIGN KEY (medication_b_id) REFERENCES Medication(medication_id)
);

-- Sequence for AuditLog IDs
CREATE SEQUENCE seq_auditlog_id
  INCREMENT BY 1
  NO MAXVALUE
  NO MINVALUE
  CACHE 1;

-- Audit Log Table
CREATE TABLE AuditLog (
    log_id int DEFAULT nextval('seq_auditlog_id'::regclass) NOT NULL,
    entity_type varchar(50) NOT NULL,
    entity_id int NOT NULL,
    action varchar(10) NOT NULL,
    changed_by varchar(100),
    change_details TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT PK_auditlog PRIMARY KEY (log_id)
);

COMMIT TRANSACTION;