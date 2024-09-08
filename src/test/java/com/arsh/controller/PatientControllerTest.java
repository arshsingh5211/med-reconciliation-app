package com.arsh.controller;

import com.arsh.dto.PatientDTO;
import com.arsh.model.Disease;
import com.arsh.model.Doctor;
import com.arsh.service.MedicationService;
import com.arsh.service.PatientService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    @MockBean
    private MedicationService medicationService;

    UUID patientId = UUID.randomUUID();
    List<Disease> emptyDiseases = new ArrayList<>();
    PatientDTO mockPatientJokic = new PatientDTO(patientId, "Nikola", "Jokic",  LocalDate.parse("1995-02-19"), "555-7890",
            "123 Hoops Avenue", "Sombor", "VO", "25000", "Natalija Macesic", "555-1010",
            new Doctor(UUID.fromString("3345efee-abcd-1234-9abc-5678901234ff"), "Stefan", "Vuković", "Sports Medicine",
                    "555-1010", "10 Doctor’s Lane", "Belgrade", "VO", "11000", null, null),
            emptyDiseases);
    UUID jamalPatientId = UUID.randomUUID();
    PatientDTO mockPatientMurray = new PatientDTO(jamalPatientId, "Jamal", "Murray", LocalDate.parse("1997-02-23"), "555-1234",
            "345 Point Guard St", "Kitchener", "ON", "N2H 5L6", "Roger Murray", "555-5678",
            new Doctor(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), "Paul", "Smith", "Orthopedic Surgeon",
                    "555-2020", "1 Surgery Lane", "Toronto", "ON", "M5B 2K3", null, null),
            emptyDiseases);

    UUID aaronPatientId = UUID.randomUUID();
    PatientDTO mockPatientGordon = new PatientDTO(aaronPatientId, "Aaron", "Gordon", LocalDate.parse("1995-09-16"), "555-4321",
            "567 Power Forward Rd", "San Jose", "CA", "95112", "Shelly Davis", "555-9876",
            new Doctor(UUID.fromString("789e1234-e56b-78d9-c456-426614174999"), "John", "Doe", "Cardiologist",
                    "555-3030", "2 Heart Blvd", "San Francisco", "CA", "94103", null, null),
            emptyDiseases);

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getPatient() throws Exception {
        when(patientService.getPatient(patientId)).thenReturn(mockPatientJokic);

        mockMvc.perform(get("/patients/" + patientId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Nikola"))
                .andExpect(jsonPath("$.lastName").value("Jokic"))
                .andExpect(jsonPath("$.dob").value("1995-02-19"))
                .andExpect(jsonPath("$.phoneNumber").value("555-7890"))
                .andExpect(jsonPath("$.streetAddress").value("123 Hoops Avenue"))
                .andExpect(jsonPath("$.city").value("Sombor"))
                .andExpect(jsonPath("$.state").value("VO"))
                .andExpect(jsonPath("$.zipCode").value("25000"))
                .andExpect(jsonPath("$.emergencyContactName").value("Natalija Macesic"))
                .andExpect(jsonPath("$.emergencyContactPhone").value("555-1010"))
                .andExpect(jsonPath("$.primaryDoctor.firstName").value("Stefan"))
                .andExpect(jsonPath("$.primaryDoctor.lastName").value("Vuković"))
                .andExpect(jsonPath("$.primaryDoctor.specialty").value("Sports Medicine"))
                .andExpect(jsonPath("$.primaryDoctor.phoneNumber").value("555-1010"))
                .andExpect(jsonPath("$.primaryDoctor.streetAddress").value("10 Doctor’s Lane"))
                .andExpect(jsonPath("$.primaryDoctor.city").value("Belgrade"))
                .andExpect(jsonPath("$.primaryDoctor.state").value("VO"))
                .andExpect(jsonPath("$.primaryDoctor.zipCode").value("11000"));
    }

    @Test
    void getAllPatients() throws Exception {
        List<PatientDTO> mockPatientList = new ArrayList<>();
        mockPatientList.add(mockPatientJokic);
        mockPatientList.add(mockPatientMurray);
        mockPatientList.add(mockPatientGordon);

        when(patientService.getAllPatients()).thenReturn(mockPatientList);

        mockMvc.perform(get("/patients").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(mockPatientList.size()));

        for (int i = 0; i < mockPatientList.size(); i++) {
            PatientDTO patient = mockPatientList.get(i);
            mockMvc.perform(get("/patients").accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath(String.format("$[%d].firstName", i)).value(patient.getFirstName()))
                    .andExpect(jsonPath(String.format("$[%d].lastName", i)).value(patient.getLastName()))
                    .andExpect(jsonPath(String.format("$[%d].dob", i)).value(patient.getDob().toString()))
                    .andExpect(jsonPath(String.format("$[%d].phoneNumber", i)).value(patient.getPhoneNumber()))
                    .andExpect(jsonPath(String.format("$[%d].streetAddress", i)).value(patient.getStreetAddress()))
                    .andExpect(jsonPath(String.format("$[%d].city", i)).value(patient.getCity()))
                    .andExpect(jsonPath(String.format("$[%d].state", i)).value(patient.getState()))
                    .andExpect(jsonPath(String.format("$[%d].zipCode", i)).value(patient.getZipCode()))
                    .andExpect(jsonPath(String.format("$[%d].emergencyContactName", i)).value(patient.getEmergencyContactName()))
                    .andExpect(jsonPath(String.format("$[%d].emergencyContactPhone", i)).value(patient.getEmergencyContactPhone()))
                    .andExpect(jsonPath(String.format("$[%d].primaryDoctor.firstName", i)).value(patient.getPrimaryDoctor().getFirstName()))
                    .andExpect(jsonPath(String.format("$[%d].primaryDoctor.lastName", i)).value(patient.getPrimaryDoctor().getLastName()));
        }
    }

    @Test
    void savePatient() {
    }

    @Test
    void deletePatient() {
    }

    @Test
    void getMedicationForPatient() {
    }

    @Test
    void getMedicationListByPatientId() {
    }

    @Test
    void addMedicationToPatientList() {
    }

    @Test
    void updateMedicationInfo() {
    }

    @Test
    void deleteMedicationInfo() {
    }
}