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
    phone_number varchar(15),
    street_address varchar(100),
    city varchar(50),
    state varchar(20),
    zip_code varchar(10),
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

-- Insert test data into Patient table
INSERT INTO Patient (first_name, last_name, dob, primary_doctor, diseases, emergency_contact_name, emergency_contact_phone)
VALUES
('Bruce', 'Wayne', '1939-05-01', 'Dr. Leslie Thompkins', 'PTSD', 'Alfred Pennyworth', '555-0001'),
('Peter', 'Parker', '1962-08-10', 'Dr. Curt Connors', 'Spider Bite Mutation', 'Aunt May', '555-0002'),
('Diana', 'Prince', '1941-10-21', 'Dr. Julia Kapatelis', 'None', 'Steve Trevor', '555-0003'),
('Clark', 'Kent', '1938-06-01', 'Dr. Emil Hamilton', 'Kryptonian Physiology', 'Lois Lane', '555-0004');

-- Insert test data into Medication table
INSERT INTO Medication (name, dosage, frequency, patient_id)
VALUES
('Battranquil', '50 mg', 'Twice a day', 1),  -- Bruce Wayne
('Websilin', '200 mg', 'Once a day', 2),    -- Peter Parker
('Amazonian Elixir', '5 ml', 'Once a week', 3),  -- Diana Prince
('Kryptoplex', '1000 mg', 'Once a month', 4); -- Clark Kent

-- Insert test data into Interaction table
INSERT INTO Interaction (medication_a_id, medication_b_id, severity, description)
VALUES
(1, 2, 'moderate', 'Battranquil may interact with Websilin, causing drowsiness.'),
(3, 4, 'severe', 'Amazonian Elixir and Kryptoplex should not be taken together.');

-- Insert test data into AuditLog table
INSERT INTO AuditLog (entity_type, entity_id, action, changed_by, change_details)
VALUES
('Patient', 1, 'INSERT', 'system', 'Initial data load for Bruce Wayne'),
('Medication', 1, 'INSERT', 'system', 'Initial data load for Battranquil');

COMMIT TRANSACTION;