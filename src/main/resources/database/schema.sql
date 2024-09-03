BEGIN TRANSACTION;

DROP TABLE IF EXISTS Patient, PatientInfo, Medication, MedicationInfo, Doctor, PatientDoctor, PatientDisease, Disease CASCADE;
DROP SEQUENCE IF EXISTS seq_medication_info_id, seq_patient_disease_id, seq_patient_doctor_id, seq_disease_id, seq_patient_info_id;

-- Sequences for IDs
CREATE SEQUENCE seq_medication_info_id INCREMENT BY 1 MINVALUE 1 CACHE 1;
CREATE SEQUENCE seq_medication_id INCREMENT BY 1 MINVALUE 1 CACHE 1;
CREATE SEQUENCE seq_patient_disease_id INCREMENT BY 1 MINVALUE 1 CACHE 1;
CREATE SEQUENCE seq_patient_doctor_id INCREMENT BY 1 MINVALUE 1 CACHE 1;
CREATE SEQUENCE seq_disease_id INCREMENT BY 1 MINVALUE 1 CACHE 1;
CREATE SEQUENCE seq_patient_info_id INCREMENT BY 1 MINVALUE 1 CACHE 1;
CREATE SEQUENCE seq_auditlog_id INCREMENT BY 1 MINVALUE 1 CACHE 1;

-- Doctor Table
CREATE TABLE Doctor (
    doctor_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    specialty VARCHAR(100),
    phone_number VARCHAR(15),
    street_address VARCHAR(100),
    city VARCHAR(50),
    state VARCHAR(20),
    zip_code VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Patient Table
CREATE TABLE Patient (
    patient_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- PatientInfo Table
CREATE TABLE PatientInfo (
    info_id INT DEFAULT nextval('seq_patient_info_id'::regclass) PRIMARY KEY,
    patient_id UUID NOT NULL,
    dob DATE,
    phone_number VARCHAR(15),
    street_address VARCHAR(100),
    city VARCHAR(50),
    state VARCHAR(20),
    zip_code VARCHAR(10),
    primary_doctor UUID NOT NULL,
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_patient_info FOREIGN KEY (patient_id) REFERENCES Patient(patient_id),
    CONSTRAINT FK_patient_info_doctor FOREIGN KEY (primary_doctor) REFERENCES Doctor(doctor_id)
);

-- PatientDoctor Table
CREATE TABLE PatientDoctor (
    patient_doctor_id INT DEFAULT nextval('seq_patient_doctor_id'::regclass) PRIMARY KEY,
    patient_id UUID NOT NULL,
    doctor_id UUID NOT NULL,
    specialty VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_patient_doctor_patient FOREIGN KEY (patient_id) REFERENCES Patient(patient_id),
    CONSTRAINT FK_patient_doctor_doctor FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id)
);

-- Disease Table
CREATE TABLE Disease (
    disease_id INT DEFAULT nextval('seq_disease_id'::regclass) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    severity VARCHAR(20), -- Mild, Moderate, Severe, etc.
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- PatientDisease Table
CREATE TABLE PatientDisease (
    patient_disease_id INT DEFAULT nextval('seq_patient_disease_id'::regclass) PRIMARY KEY,
    patient_id UUID NOT NULL,
    disease_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_patient_disease_patient FOREIGN KEY (patient_id) REFERENCES Patient(patient_id),
    CONSTRAINT FK_patient_disease_disease FOREIGN KEY (disease_id) REFERENCES Disease(disease_id)
);

-- Medication Table (General medication data)
CREATE TABLE Medication (
    medication_id INT DEFAULT nextval('seq_medication_id'::regclass) PRIMARY KEY,
    brand_name VARCHAR(100),
    generic_name VARCHAR(100),
    drug_class VARCHAR(50), -- e.g., Antihypertensive, Analgesic, etc.
    sub_category VARCHAR(50),
    is_generic BOOLEAN,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_brand_or_generic CHECK (
        (brand_name IS NOT NULL AND brand_name <> '')
        OR
        (generic_name IS NOT NULL AND generic_name <> '')
    )
);


-- MedicationInfo Table (Patient-specific medication data)
CREATE TABLE MedicationInfo (
    medication_info_id INT DEFAULT nextval('seq_medication_info_id'::regclass) PRIMARY KEY,
    medication_id INT NOT NULL,
    patient_id UUID NOT NULL,
    dosage VARCHAR(50),
    frequency VARCHAR(50),
    route VARCHAR(50),   -- Oral, IV, etc.
    is_prn BOOLEAN,
    date_started DATE,
    is_current BOOLEAN,
    prescribing_doctor UUID,
    pharmacy VARCHAR(100),
    comments TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_medication_info_patient FOREIGN KEY (patient_id) REFERENCES Patient(patient_id),
    CONSTRAINT FK_medication_info_medication FOREIGN KEY (medication_id) REFERENCES Medication(medication_id),
    CONSTRAINT FK_medication_info_doctor FOREIGN KEY (prescribing_doctor) REFERENCES Doctor(doctor_id)
);

-- Audit Log Table
CREATE TABLE AuditLog (
    log_id INT DEFAULT nextval('seq_auditlog_id'::regclass) PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id VARCHAR(36) NOT NULL, -- Storing UUIDs as varchars
    action VARCHAR(10) NOT NULL,
    changed_by VARCHAR(100),
    change_details TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert test data into Doctor table
INSERT INTO Doctor (first_name, last_name, specialty, phone_number, street_address, city, state, zip_code)
VALUES
('Leslie', 'Thompkins', 'Psychiatrist', '555-1001', '1 Doctor’s Way', 'Gotham', 'NJ', '07001'),
('Curt', 'Connors', 'Geneticist', '555-2002', '2 Genetic Lane', 'New York', 'NY', '10001'),
('Julia', 'Kapatelis', 'Anthropologist', '555-3003', '3 Scholar Blvd', 'Themyscira', 'GR', '00001'),
('Emil', 'Hamilton', 'Physicist', '555-4004', '4 Science Rd', 'Metropolis', 'NY', '10001');

-- Insert test data into Patient table
INSERT INTO Patient (first_name, last_name)
VALUES
('Bruce', 'Wayne'),
('Peter', 'Parker'),
('Diana', 'Prince'),
('Clark', 'Kent');

-- Insert test data into PatientInfo table
INSERT INTO PatientInfo (patient_id, dob, phone_number, street_address, city, state, zip_code, primary_doctor, emergency_contact_name, emergency_contact_phone)
VALUES
((SELECT patient_id FROM Patient WHERE first_name = 'Bruce' AND last_name = 'Wayne'), '1939-05-01', '555-0000', '1007 Mountain Drive', 'Gotham', 'NJ', '07001', (SELECT doctor_id FROM Doctor WHERE first_name = 'Leslie' AND last_name = 'Thompkins'), 'Alfred Pennyworth', '555-0001'),
((SELECT patient_id FROM Patient WHERE first_name = 'Peter' AND last_name = 'Parker'), '1962-08-10', '555-0002', '20 Ingram Street', 'Queens', 'NY', '11375', (SELECT doctor_id FROM Doctor WHERE first_name = 'Curt' AND last_name = 'Connors'), 'Aunt May', '555-0003'),
((SELECT patient_id FROM Patient WHERE first_name = 'Diana' AND last_name = 'Prince'), '1941-10-21', '555-0004', 'Themyscira Island', 'Themyscira', 'GR', '00001', (SELECT doctor_id FROM Doctor WHERE first_name = 'Julia' AND last_name = 'Kapatelis'), 'Steve Trevor', '555-0005'),
((SELECT patient_id FROM Patient WHERE first_name = 'Clark' AND last_name = 'Kent'), '1938-06-01', '555-0006', '344 Clinton St', 'Metropolis', 'NY', '10001', (SELECT doctor_id FROM Doctor WHERE first_name = 'Emil' AND last_name = 'Hamilton'), 'Lois Lane', '555-0007');

-- Insert test data into Disease table
INSERT INTO Disease (name, severity)
VALUES
('Asthma', 'Moderate'),
('Diabetes', 'Severe'),
('Hypertension', 'Mild'),
('Cancer', 'Severe');

-- Insert test data into PatientDisease table
INSERT INTO PatientDisease (patient_id, disease_id)
VALUES
((SELECT patient_id FROM Patient WHERE first_name = 'Bruce' AND last_name = 'Wayne'), (SELECT disease_id FROM Disease WHERE name = 'Hypertension')),
((SELECT patient_id FROM Patient WHERE first_name = 'Peter' AND last_name = 'Parker'), (SELECT disease_id FROM Disease WHERE name = 'Diabetes')),
((SELECT patient_id FROM Patient WHERE first_name = 'Diana' AND last_name = 'Prince'), (SELECT disease_id FROM Disease WHERE name = 'Cancer')),
((SELECT patient_id FROM Patient WHERE first_name = 'Clark' AND last_name = 'Kent'), (SELECT disease_id FROM Disease WHERE name = 'Asthma'));

-- Insert test data into Medication table
INSERT INTO Medication (brand_name, generic_name, drug_class, sub_category, is_generic)
VALUES
('Tylenol', 'Acetaminophen', 'Analgesic', 'Pain reliever', false),
('Lipitor', 'Atorvastatin', 'Antihyperlipidemic', 'Statin', false),
(NULL, 'Metformin', 'Antidiabetic', 'Biguanides', true),
('Advil', 'Ibuprofen', 'Anti-inflammatory', 'NSAID', false),
('Lasix', 'Furosemide', 'Diuretic', 'Loop diuretics', false);

-- Insert test data into MedicationInfo table
INSERT INTO MedicationInfo (medication_id, patient_id, dosage, frequency, route, is_prn, date_started, is_current, prescribing_doctor, pharmacy, comments)
VALUES
  ((SELECT medication_id FROM Medication WHERE brand_name = 'Tylenol'), (SELECT patient_id FROM Patient WHERE first_name = 'Bruce' AND last_name = 'Wayne'), '81mg', 'Daily', 'Oral', FALSE, '2023-01-01', TRUE, (SELECT doctor_id FROM Doctor WHERE first_name = 'Leslie' AND last_name = 'Thompkins'), 'Gotham Pharmacy', 'Take with food'),
  ((SELECT medication_id FROM Medication WHERE generic_name = 'Metformin'), (SELECT patient_id FROM Patient WHERE first_name = 'Peter' AND last_name = 'Parker'), '500mg', 'Twice Daily', 'Oral', FALSE, '2023-01-01', TRUE, (SELECT doctor_id FROM Doctor WHERE first_name = 'Curt' AND last_name = 'Connors'), 'Queens Pharmacy', 'Monitor blood sugar levels'),
  ((SELECT medication_id FROM Medication WHERE brand_name = 'Advil'), (SELECT patient_id FROM Patient WHERE first_name = 'Diana' AND last_name = 'Prince'), 'Varies', 'Weekly', 'IV', TRUE, '2023-01-01', TRUE, (SELECT doctor_id FROM Doctor WHERE first_name = 'Julia' AND last_name = 'Kapatelis'), 'Themyscira Pharmacy', 'Administer under supervision'),
  ((SELECT medication_id FROM Medication WHERE generic_name = 'Furosemide'), (SELECT patient_id FROM Patient WHERE first_name = 'Clark' AND last_name = 'Kent'), '10mg', 'Daily', 'Oral', FALSE, '2023-01-01', TRUE, (SELECT doctor_id FROM Doctor WHERE first_name = 'Emil' AND last_name = 'Hamilton'), 'Metropolis Pharmacy', 'Take at the same time daily');

-- Insert test data into AuditLog table
INSERT INTO AuditLog (entity_type, entity_id, action, changed_by, change_details)
VALUES
('Patient', '1', 'UPDATE', 'admin', 'Updated address for Bruce Wayne'),
('Medication', '1', 'INSERT', 'admin', 'Added new medication Aspirin'),
('Doctor', '1', 'DELETE', 'admin', 'Removed doctor Leslie Thompkins');

COMMIT TRANSACTION;
