# Database Integration and Data Flow in Audino Healthcare System

## Database Architecture Overview

### File-Based JSON Database Structure
```
/src/main/resources/data/
├── patients.json           # Patient records and medical history
├── medications.json        # Master medication catalog  
├── prescriptions.json      # Prescription records and medication lists
└── interaction-rules.json  # Drug interaction safety rules
```

---

## Data Loading Process

### 1. Application Startup Data Loading

#### MainController.initialize() - Entry Point
```java
@Override
public void initialize(URL location, ResourceBundle resources) {
    dataService = new DataService();        // Create data service instance
    interactionEngine = new InteractionEngine();
    
    setupDate();
    loadData();                             // Load all JSON data
    setupPatientListView();
    setupMedicationComboBox();
    // ... other UI setup
}
```

#### loadData() - Master Data Loading Method
```java
private void loadData() {
    statusLabel.setText("Loading data from database...");
    try {
        dataService.loadAllData();                           // Load from JSON files
        patientList.setAll(dataService.getAllPatients());    // Populate UI lists
        medicationList.setAll(dataService.getAllMedications());
        prescriptionList.setAll(dataService.getAllPrescriptions());
        dataLoadedSuccessfully = true;
        statusLabel.setText("Data loaded successfully.");
    } catch (Exception e) {
        dataLoadedSuccessfully = false;
        statusLabel.setText("Error loading data.");
        showErrorAlert("Data Loading Error", "Could not load application data from the database.", e.getMessage());
    }
}
```

### 2. DataService.loadAllData() - Core Loading Logic

```java
public void loadAllData() {
    try {
        // Configure Jackson ObjectMapper for date handling
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Load each JSON file into memory
        loadPatients();           // patients.json → List<Patient>
        loadMedications();        // medications.json → List<Medication>
        loadPrescriptions();      // prescriptions.json → List<Prescription>
        loadInteractionRules();   // interaction-rules.json → List<InteractionRule>
        
        System.out.println("All data loaded successfully:");
        System.out.println("- " + patients.size() + " patients");
        System.out.println("- " + medications.size() + " medications");
        System.out.println("- " + prescriptions.size() + " prescriptions");
        System.out.println("- " + interactionRules.size() + " interaction rules");
        
    } catch (Exception e) {
        throw new RuntimeException("Failed to load application data", e);
    }
}
```

### 3. Individual File Loading Methods

#### Loading Patients
```java
private void loadPatients() {
    try {
        InputStream inputStream = getClass().getResourceAsStream("/data/patients.json");
        if (inputStream != null) {
            // Jackson converts JSON array to Java List<Patient>
            patients = objectMapper.readValue(inputStream, new TypeReference<List<Patient>>() {});
            System.out.println("Loaded " + patients.size() + " patients from database");
        } else {
            patients = new ArrayList<>();  // Initialize empty list if file not found
            System.out.println("patients.json not found, starting with empty patient list");
        }
    } catch (Exception e) {
        throw new RuntimeException("Error loading patients from database", e);
    }
}
```

#### Loading Medications 
```java
private void loadMedications() {
    try {
        InputStream inputStream = getClass().getResourceAsStream("/data/medications.json");
        if (inputStream != null) {
            // Jackson handles complex object mapping with nested arrays
            medications = objectMapper.readValue(inputStream, new TypeReference<List<Medication>>() {});
            System.out.println("Loaded " + medications.size() + " medications from database");
        } else {
            medications = new ArrayList<>();
            System.out.println("medications.json not found, starting with empty medication list");
        }
    } catch (Exception e) {
        throw new RuntimeException("Error loading medications from database", e);
    }
}
```

#### Loading Prescriptions with Relationship Mapping
```java
private void loadPrescriptions() {
    try {
        InputStream inputStream = getClass().getResourceAsStream("/data/prescriptions.json");
        if (inputStream != null) {
            prescriptions = objectMapper.readValue(inputStream, new TypeReference<List<Prescription>>() {});
            
            // CRITICAL: Link medications to prescribed drugs
            linkMedicationsToPrescribedDrugs();
            
            System.out.println("Loaded " + prescriptions.size() + " prescriptions from database");
        } else {
            prescriptions = new ArrayList<>();
            System.out.println("prescriptions.json not found, starting with empty prescription list");
        }
    } catch (Exception e) {
        throw new RuntimeException("Error loading prescriptions from database", e);
    }
}
```

#### Relationship Linking Logic
```java
private void linkMedicationsToPrescribedDrugs() {
    // After loading JSON, we need to link medicationId references to actual Medication objects
    for (Prescription prescription : prescriptions) {
        for (PrescribedDrug drug : prescription.getPrescribedDrugs()) {
            // Find medication by ID and set the reference
            medications.stream()
                .filter(med -> med.getMedicationId().equals(drug.getMedicationId()))
                .findFirst()
                .ifPresent(drug::setMedication);  // Set the full Medication object
        }
    }
}
```

---

## Data Persistence Process

### 1. Save Prescription Workflow

#### MainController.handleSave() - UI Trigger
```java
@FXML
private void handleSave() {
    if (currentPrescription != null && !currentPrescription.isEmpty()) {
        // Set prescription status and alerts
        currentPrescription.setStatus(PrescriptionStatus.APPROVED);
        currentPrescription.setAlerts(alertList);
        
        // Delegate to DataService for persistence
        dataService.savePrescription(currentPrescription);
        
        statusLabel.setText("Prescription saved successfully!");
        updateUIState();
    } else {
        showWarningAlert("Cannot Save", "There is no active or non-empty prescription to save.");
    }
}
```

#### DataService.savePrescription() - Core Save Logic
```java
public void savePrescription(Prescription prescription) {
    try {
        // Remove any existing prescription for this patient (business rule: one prescription per patient)
        prescriptions.removeIf(p -> p.getPatient().getPatientId().equals(prescription.getPatient().getPatientId()));
        
        // Add the new/updated prescription
        prescriptions.add(prescription);
        
        // Persist to JSON file
        savePrescriptionsToFile();
        
        System.out.println("Prescription saved for patient: " + prescription.getPatient().getFullName());
        
    } catch (Exception e) {
        throw new RuntimeException("Failed to save prescription", e);
    }
}
```

### 2. JSON File Writing Process

#### savePrescriptionsToFile() - File Persistence
```java
private void savePrescriptionsToFile() {
    try {
        // Get the file path (create if doesn't exist)
        File prescriptionsFile = new File("src/main/resources/data/prescriptions.json");
        prescriptionsFile.getParentFile().mkdirs();  // Create directories if needed
        
        // Configure Jackson for pretty printing
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);  // Pretty JSON
        
        // Write List<Prescription> to JSON file
        mapper.writeValue(prescriptionsFile, prescriptions);
        
        System.out.println("Prescriptions saved to file: " + prescriptionsFile.getAbsolutePath());
        
    } catch (Exception e) {
        throw new RuntimeException("Failed to save prescriptions to file", e);
    }
}
```

### 3. Patient Save Process

#### DataService.savePatient() - New Patient Persistence
```java
public void savePatient(Patient patient) {
    try {
        // Add to in-memory list
        if (patient.getPatientId() == null) {
            // Generate new ID for new patients
            patient.setPatientId("PAT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        
        // Remove existing patient with same ID (for updates)
        patients.removeIf(p -> p.getPatientId().equals(patient.getPatientId()));
        
        // Add new/updated patient
        patients.add(patient);
        
        // Persist to file
        savePatientsToFile();
        
    } catch (Exception e) {
        throw new RuntimeException("Failed to save patient", e);
    }
}

private void savePatientsToFile() {
    try {
        File patientsFile = new File("src/main/resources/data/patients.json");
        patientsFile.getParentFile().mkdirs();
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        // Convert List<Patient> to formatted JSON
        mapper.writeValue(patientsFile, patients);
        
    } catch (Exception e) {
        throw new RuntimeException("Failed to save patients to file", e);
    }
}
```

---

## Real-Time Data Operations

### 1. Search Operations

#### Patient Search - In-Memory Filtering
```java
public List<Patient> searchPatients(String query) {
    if (query == null || query.trim().isEmpty()) {
        return new ArrayList<>(patients);  // Return all patients
    }
    
    String searchTerm = query.toLowerCase().trim();
    return patients.stream()
        .filter(patient -> 
            patient.getFirstName().toLowerCase().contains(searchTerm) ||
            patient.getLastName().toLowerCase().contains(searchTerm) ||
            patient.getPatientId().toLowerCase().contains(searchTerm)
        )
        .collect(Collectors.toList());
}
```

#### Medication Search - Dynamic Filtering
```java
public List<Medication> searchMedications(String query) {
    if (query == null || query.trim().isEmpty()) {
        return new ArrayList<>(medications);
    }
    
    String searchTerm = query.toLowerCase().trim();
    return medications.stream()
        .filter(med ->
            med.getDisplayName().toLowerCase().contains(searchTerm) ||
            med.getGenericName().toLowerCase().contains(searchTerm) ||
            med.getMedicationClasses().stream()
                .anyMatch(cls -> cls.toLowerCase().contains(searchTerm))
        )
        .sorted((m1, m2) -> m1.getDisplayName().compareToIgnoreCase(m2.getDisplayName()))
        .collect(Collectors.toList());
}
```

### 2. Prescription Retrieval

#### getActivePrescriberionForPatient() - Patient-Specific Queries
```java
public Prescription getActivePrescriberionForPatient(String patientId) {
    return prescriptions.stream()
        .filter(prescription -> prescription.getPatient().getPatientId().equals(patientId))
        .filter(prescription -> prescription.getStatus() == PrescriptionStatus.APPROVED)
        .findFirst()  // Get most recent prescription
        .orElse(null);
}
```

---

## Refresh and Data Synchronization

### handleRefresh() - Reload from Database
```java
@FXML
private void handleRefresh() {
    // Check for unsaved changes first
    if (currentPrescription != null && 
        currentPrescription.getStatus() == PrescriptionStatus.DRAFT && 
        !currentPrescription.isEmpty()) {
        
        boolean confirmed = showConfirmationAlert("Unsaved Changes", 
            "You have unsaved changes to the current prescription. Refreshing will discard these changes. Are you sure?");
        if (!confirmed) {
            return;  // User cancelled refresh
        }
    }

    final String selectedPatientId = (selectedPatient != null) ? selectedPatient.getPatientId() : null;

    // Reload all data from JSON files
    loadData();

    // Restore patient selection if one was selected
    if (selectedPatientId != null) {
        patientList.stream()
            .filter(p -> selectedPatientId.equals(p.getPatientId()))
            .findFirst()
            .ifPresent(p -> {
                patientListView.getSelectionModel().select(p);
                patientListView.scrollTo(p);
                // This triggers handlePatientSelection() and reloads prescription from database
            });
    }
    
    statusLabel.setText("Data refreshed from database. Any unsaved changes have been discarded.");
}
```

---

## Error Handling and Data Integrity

### File System Error Handling
```java
private void loadPatients() {
    try {
        InputStream inputStream = getClass().getResourceAsStream("/data/patients.json");
        if (inputStream != null) {
            patients = objectMapper.readValue(inputStream, new TypeReference<List<Patient>>() {});
        } else {
            patients = new ArrayList<>();
            System.out.println("patients.json not found, starting with empty patient list");
        }
    } catch (JsonParseException e) {
        throw new RuntimeException("Invalid JSON format in patients.json", e);
    } catch (JsonMappingException e) {
        throw new RuntimeException("JSON structure doesn't match Patient class", e);
    } catch (IOException e) {
        throw new RuntimeException("Could not read patients.json file", e);
    }
}
```

### Data Validation During Save
```java
public void savePrescription(Prescription prescription) {
    // Validate prescription before saving
    if (prescription == null) {
        throw new IllegalArgumentException("Prescription cannot be null");
    }
    if (prescription.getPatient() == null) {
        throw new IllegalArgumentException("Prescription must have a patient");
    }
    if (prescription.getPrescribedDrugs().isEmpty()) {
        throw new IllegalArgumentException("Prescription must have at least one medication");
    }
    
    // Business rule: Only one active prescription per patient
    prescriptions.removeIf(p -> p.getPatient().getPatientId().equals(prescription.getPatient().getPatientId()));
    prescriptions.add(prescription);
    
    savePrescriptionsToFile();
}
```

---

## Key Database Features

### Advantages:
- No External Dependencies - No database server required  
- Human Readable - JSON files can be manually inspected/edited  
- Version Control Friendly - Git can track changes to data files  
- Simple Backup - Just copy the data folder  
- Cross-Platform - Works on any OS with Java  
- Development Friendly - Easy to reset/modify test data  

### Limitations:
- Single User - No concurrent access support  
- Limited Scalability - Performance degrades with large datasets  
- No ACID Transactions - No atomicity guarantees  
- Manual Relationships - No foreign key constraints  
- No Query Language - Must use Java streams for complex queries  

### Best Use Cases:
- Small to medium medical practices (< 10,000 patients)  
- Single-user desktop applications  
- Development and testing environments  
- Applications requiring simple data persistence  
- Scenarios where database setup complexity is undesirable