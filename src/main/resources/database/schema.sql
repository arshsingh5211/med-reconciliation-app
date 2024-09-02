BEGIN TRANSACTION;

DROP TABLE IF EXISTS Patient, PatientInfo, Medication, Interaction, Doctor, PatientDoctor, PatientDisease, AuditLog CASCADE;
DROP SEQUENCE IF EXISTS seq_medication_id, seq_interaction_id, seq_auditlog_id;

-- Create sequences
CREATE SEQUENCE seq_medication_id
  INCREMENT BY 1
  NO MAXVALUE
  NO MINVALUE
  CACHE 1;

CREATE SEQUENCE seq_interaction_id
  INCREMENT BY 1
  NO MAXVALUE
  NO MINVALUE
  CACHE 1;

CREATE SEQUENCE seq_auditlog_id
  INCREMENT BY 1
  NO MAXVALUE
  NO MINVALUE
  CACHE 1;

-- Patient Table
CREATE TABLE Patient (
    patient_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    first_name varchar(50) NOT NULL,
    last_name varchar(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- PatientInfo Table
CREATE TABLE PatientInfo (
    info_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    patient_id UUID NOT NULL,
    dob DATE,
    phone_number varchar(15),
    street_address varchar(100),
    city varchar(50),
    state varchar(20),
    zip_code varchar(10),
    emergency_contact_name varchar(100),
    emergency_contact_phone varchar(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_patient_info FOREIGN KEY (patient_id) REFERENCES Patient(patient_id)
);

-- Doctor Table
CREATE TABLE Doctor (
    doctor_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    first_name varchar(50) NOT NULL,
    last_name varchar(50) NOT NULL,
    specialty varchar(100),
    phone_number varchar(15),
    street_address varchar(100),
    city varchar(50),
    state varchar(20),
    zip_code varchar(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- PatientDoctor Table
CREATE TABLE PatientDoctor (
    patient_doctor_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    patient_id UUID NOT NULL,
    doctor_id UUID NOT NULL,
    type_of_care varchar(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_patient_doctor_patient FOREIGN KEY (patient_id) REFERENCES Patient(patient_id),
    CONSTRAINT FK_patient_doctor_doctor FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id)
);

-- Disease Table
CREATE TABLE Disease (
    disease_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name varchar(100) NOT NULL,
    severity varchar(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- PatientDisease Table
CREATE TABLE PatientDisease (
    patient_disease_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    patient_id UUID NOT NULL,
    disease_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_patient_disease_patient FOREIGN KEY (patient_id) REFERENCES Patient(patient_id),
    CONSTRAINT FK_patient_disease_disease FOREIGN KEY (disease_id) REFERENCES Disease(disease_id)
);

-- Medication Table
CREATE TABLE Medication (
    medication_id int DEFAULT nextval('seq_medication_id'::regclass) PRIMARY KEY,
    name varchar(100) NOT NULL,
    dosage varchar(50),
    frequency varchar(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- PatientMedication Table (Junction Table)
CREATE TABLE PatientMedication (
    patient_medication_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    patient_id UUID NOT NULL,
    medication_id int NOT NULL,
    dosage varchar(50),
    frequency varchar(50),
    start_date DATE,
    end_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_patient_medication_patient FOREIGN KEY (patient_id) REFERENCES Patient(patient_id),
    CONSTRAINT FK_patient_medication_medication FOREIGN KEY (medication_id) REFERENCES Medication(medication_id)
);

-- Interaction Table
CREATE TABLE Interaction (
    interaction_id int DEFAULT nextval('seq_interaction_id'::regclass) PRIMARY KEY,
    medication_a_id int NOT NULL,
    medication_b_id int NOT NULL,
    severity varchar(10) CHECK (severity IN ('mild', 'moderate', 'severe')),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_interaction_med_a FOREIGN KEY (medication_a_id) REFERENCES Medication(medication_id),
    CONSTRAINT FK_interaction_med_b FOREIGN KEY (medication_b_id) REFERENCES Medication(medication_id)
);

-- Audit Log Table
CREATE TABLE AuditLog (
    log_id int DEFAULT nextval('seq_auditlog_id'::regclass) PRIMARY KEY,
    entity_type varchar(50) NOT NULL,
    entity_id varchar(36) NOT NULL, -- Storing UUIDs and integers as varchars
    action varchar(10) NOT NULL,
    changed_by varchar(100),
    change_details TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert test data into Patient table
INSERT INTO Patient (first_name, last_name)
VALUES
('Bruce', 'Wayne'),
('Peter', 'Parker'),
('Diana', 'Prince'),
('Clark', 'Kent');

-- Insert test data into PatientInfo table
INSERT INTO PatientInfo (patient_id, dob, phone_number, street_address, city, state, zip_code, emergency_contact_name, emergency_contact_phone)
VALUES
((SELECT patient_id FROM Patient WHERE first_name = 'Bruce' AND last_name = 'Wayne'), '1939-05-01', '555-0000', '1007 Mountain Drive', 'Gotham', 'NJ', '07001', 'Alfred Pennyworth', '555-0001'),
((SELECT patient_id FROM Patient WHERE first_name = 'Peter' AND last_name = 'Parker'), '1962-08-10', '555-0002', '20 Ingram Street', 'Queens', 'NY', '11375', 'Aunt May', '555-0003'),
((SELECT patient_id FROM Patient WHERE first_name = 'Diana' AND last_name = 'Prince'), '1941-10-21', '555-0004', 'Themyscira Island', 'Themyscira', 'GR', '00001', 'Steve Trevor', '555-0005'),
((SELECT patient_id FROM Patient WHERE first_name = 'Clark' AND last_name = 'Kent'), '1938-06-01', '555-0006', '344 Clinton St', 'Metropolis', 'NY', '10001', 'Lois Lane', '555-0007');

-- Insert test data into Doctor table
INSERT INTO Doctor (first_name, last_name, specialty, phone_number, street_address, city, state, zip_code)
VALUES
('Leslie', 'Thompkins', 'Psychiatrist', '555-1001', '1 Doctor’s Way', 'Gotham', 'NJ', '07001'),
('Curt', 'Connors', 'Geneticist', '555-2002', '2 Genetic Lane', 'New York', 'NY', '10001'),
('Julia', 'Kapatelis', 'Anthropologist', '555-3003', '3 Scholar Blvd', 'Themyscira', 'GR', '00001'),
('Emil', 'Hamilton', 'Physicist', '555-4004', '4 Science Rd', 'Metropolis', 'NY', '10001');

-- Insert test data into PatientDoctor table
INSERT INTO PatientDoctor (patient_id, doctor_id, type_of_care)
VALUES
((SELECT patient_id FROM Patient WHERE first_name = 'Bruce' AND last_name = 'Wayne'), (SELECT doctor_id FROM Doctor WHERE first_name = 'Leslie' AND last_name = 'Thompkins'), 'Primary Care'),
((SELECT patient_id FROM Patient WHERE first_name = 'Peter' AND last_name = 'Parker'), (SELECT doctor_id FROM Doctor WHERE first_name = 'Curt' AND last_name = 'Connors'), 'Primary Care'),
((SELECT patient_id FROM Patient WHERE first_name = 'Diana' AND last_name = 'Prince'), (SELECT doctor_id FROM Doctor WHERE first_name = 'Julia' AND last_name = 'Kapatelis'), 'Specialist'),
((SELECT patient_id FROM Patient WHERE first_name = 'Clark' AND last_name = 'Kent'), (SELECT doctor_id FROM Doctor WHERE first_name = 'Emil' AND last_name = 'Hamilton'), 'Primary Care');

-- Insert test data into Disease table
INSERT INTO Disease (name, severity)
VALUES
('PTSD', 'Severe'),
('Spider Bite Mutation', 'Mild'),
('None', 'None'),
('Kryptonian Physiology', 'Severe');

-- Insert test data into PatientDisease table
INSERT INTO PatientDisease (patient_id, disease_id)
VALUES
((SELECT patient_id FROM Patient WHERE first_name = 'Bruce' AND last_name = 'Wayne'), (SELECT disease_id FROM Disease WHERE name = 'PTSD')),
((SELECT patient_id FROM Patient WHERE first_name = 'Peter' AND last_name = 'Parker'), (SELECT disease_id FROM Disease WHERE name = 'Spider Bite Mutation')),
((SELECT patient_id FROM Patient WHERE first_name = 'Diana' AND last_name = 'Prince'), (SELECT disease_id FROM Disease WHERE name = 'None')),
((SELECT patient_id FROM Patient WHERE first_name = 'Clark' AND last_name = 'Kent'), (SELECT disease_id FROM Disease WHERE name = 'Kryptonian Physiology'));

-- Insert test data into Medication table
INSERT INTO Medication (name, dosage, frequency)
VALUES
('Battranquil', '50 mg', 'Twice a day'),
('Websilin', '200 mg', 'Once a day'),
('Amazonian Elixir', '5 ml', 'Once a week'),
('Kryptoplex', '1000 mg', 'Once a month');

-- Insert test data into PatientMedication table
INSERT INTO PatientMedication (patient_id, medication_id, dosage, frequency, start_date, end_date)
VALUES
((SELECT patient_id FROM Patient WHERE first_name = 'Bruce' AND last_name = 'Wayne'), (SELECT medication_id FROM Medication WHERE name = 'Battranquil'), '50 mg', 'Twice a day', '2024-01-01', '2024-12-31'),
((SELECT patient_id FROM Patient WHERE first_name = 'Peter' AND last_name = 'Parker'), (SELECT medication_id FROM Medication WHERE name = 'Websilin'), '200 mg', 'Once a day', '2024-01-01', '2024-12-31'),
((SELECT patient_id FROM Patient WHERE first_name = 'Diana' AND last_name = 'Prince'), (SELECT medication_id FROM Medication WHERE name = 'Amazonian Elixir'), '5 ml', 'Once a week', '2024-01-01', '2024-12-31'),
((SELECT patient_id FROM Patient WHERE first_name = 'Clark' AND last_name = 'Kent'), (SELECT medication_id FROM Medication WHERE name = 'Kryptoplex'), '1000 mg', 'Once a month', '2024-01-01', '2024-12-31');

-- Insert test data into Interaction table
INSERT INTO Interaction (medication_a_id, medication_b_id, severity, description)
VALUES
((SELECT medication_id FROM Medication WHERE name = 'Battranquil'), (SELECT medication_id FROM Medication WHERE name = 'Websilin'), 'moderate', 'Battranquil may interact with Websilin, causing drowsiness.'),
((SELECT medication_id FROM Medication WHERE name = 'Amazonian Elixir'), (SELECT medication_id FROM Medication WHERE name = 'Kryptoplex'), 'severe', 'Amazonian Elixir and Kryptoplex should not be taken together.');

-- Insert test data into AuditLog table
INSERT INTO AuditLog (entity_type, entity_id, action, changed_by, change_details)
VALUES
('Patient', (SELECT CAST(patient_id AS varchar) FROM Patient WHERE first_name = 'Bruce' AND last_name = 'Wayne'), 'INSERT', 'system', 'Initial data load for Bruce Wayne'),
('Medication', (SELECT CAST(medication_id AS varchar) FROM Medication WHERE name = 'Battranquil'), 'INSERT', 'system', 'Initial data load for Battranquil');

COMMIT TRANSACTION;