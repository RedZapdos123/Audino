package com.audino.service;

import com.audino.model.Medication;
import com.audino.model.Patient;
import com.audino.model.Prescription;
import com.audino.util.ConfigurationManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataService {

    private final ObjectMapper objectMapper;
    private final ConfigurationManager config;
    private List<Patient> patients = new ArrayList<>();
    private List<Medication> medications = new ArrayList<>();
    private List<Prescription> prescriptions = new ArrayList<>();
    private Map<String, Object> interactionRules;
    public DataService() {
        this.config = ConfigurationManager.getInstance();
        this.objectMapper = config.getObjectMapper();
    }

    public void loadAllData() {
        patients = loadData(config.getPatientsDataFile(), new TypeReference<>() {});
        medications = loadData(config.getMedicationsDataFile(), new TypeReference<>() {});
        interactionRules = loadData(config.getInteractionRulesDataFile(), new TypeReference<>() {});
        prescriptions = loadData(config.getPrescriptionsDataFile(), new TypeReference<>() {});
        System.out.println("All data loaded.");
    }

    private <T> T loadData(String filePath, TypeReference<T> typeRef) {
        try (InputStream inputStream = DataService.class.getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new RuntimeException("Cannot find resource file: " + filePath);
            }
            return objectMapper.readValue(inputStream, typeRef);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load data from " + filePath, e);
        }
    }
    
    public List<Patient> getAllPatients() {
        return new ArrayList<>(patients);
    }

    public List<Medication> getAllMedications() {
        return new ArrayList<>(medications);
    }
    
    public List<Prescription> getAllPrescriptions() {
        return new ArrayList<>(prescriptions);
    }

    public Map<String, Object> getInteractionRules() {
        return interactionRules;
    }
    
    public List<Patient> searchPatients(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllPatients();
        }
        String lowerCaseTerm = searchTerm.toLowerCase();
        return patients.stream()
                .filter(p -> p.getFullName().toLowerCase().contains(lowerCaseTerm))
                .collect(Collectors.toList());
    }

    public List<Medication> searchMedications(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllMedications();
        }
        String lowerCaseTerm = searchTerm.toLowerCase();
        return medications.stream()
                .filter(m -> m.getGenericName().toLowerCase().contains(lowerCaseTerm) ||
                             (m.getBrandName() != null && m.getBrandName().toLowerCase().contains(lowerCaseTerm)))
                .collect(Collectors.toList());
    }

    public void savePatient(Patient patient) {
        patients.add(patient);
        // Persist changes immediately to files
        saveAllData();
    }

    public void updatePatient(Patient patient) {
        // Patient is already in the list by reference
        // Persist changes immediately to files
        saveAllData();
    }

    public void deletePatient(Patient patient) {
        patients.remove(patient);
        // Persist changes immediately to files
        saveAllData();
    }

    public void savePrescription(Prescription prescription) {
        // Remove any existing prescriptions for this patient to ensure only one active prescription
        prescriptions.removeIf(p -> p.getPatientId().equals(prescription.getPatientId()));
        prescriptions.add(prescription);
        // Persist changes immediately to files
        saveAllData();
    }

    public boolean addMedicationToExistingPrescription(String patientId, Medication medication, String dosage, String frequency, String duration, String prescribingPhysician) {
        // Find existing prescription for the patient
        Prescription existingPrescription = prescriptions.stream()
                .filter(p -> p.getPatientId().equals(patientId))
                .findFirst()
                .orElse(null);
        
        if (existingPrescription != null) {
            // Add medication to existing prescription
            existingPrescription.addPrescribedDrug(new com.audino.model.PrescribedDrug(
                medication, dosage, frequency, duration, "", prescribingPhysician));
            // Persist changes immediately to files
            saveAllData();
            return true;
        }
        return false; // No existing prescription found
    }

    public List<Prescription> getPrescriptionsForPatient(Patient patient) {
        return prescriptions.stream()
                .filter(p -> p.getPatientId().equals(patient.getPatientId()))
                .collect(Collectors.toList());
    }
    
    public Prescription getActivePrescriberionForPatient(String patientId) {
        return prescriptions.stream()
                .filter(p -> p.getPatientId().equals(patientId))
                .findFirst()
                .orElse(null);
    }
    
    public void saveAllData() {
        try {
            saveDataToFile(patients, config.getPatientsDataFile());
            saveDataToFile(prescriptions, config.getPrescriptionsDataFile());
            System.out.println("All data saved successfully.");
        } catch (Exception e) {
            System.err.println("Error saving data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void saveAllData(List<Patient> currentPatients, List<Prescription> currentPrescriptions) {
        try {
            saveDataToFile(currentPatients, config.getPatientsDataFile());
            saveDataToFile(currentPrescriptions, config.getPrescriptionsDataFile());
            System.out.println("All data saved successfully.");
        } catch (Exception e) {
            System.err.println("Error saving data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private <T> void saveDataToFile(T data, String resourcePath) throws Exception {
        String projectRoot = System.getProperty("user.dir");
        
        // Save to source directory (src/main/resources)
        String sourceFilePath = Paths.get(projectRoot, "src", "main", "resources", resourcePath).toString();
        File sourceFile = new File(sourceFilePath);
        sourceFile.getParentFile().mkdirs();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(sourceFile, data);
        System.out.println("Saved data to source: " + sourceFilePath);
        
        // Also save to target directory (target/classes) for immediate effect
        String targetFilePath = Paths.get(projectRoot, "target", "classes", resourcePath).toString();
        File targetFile = new File(targetFilePath);
        if (targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(targetFile, data);
            System.out.println("Saved data to target: " + targetFilePath);
        }
    }
}