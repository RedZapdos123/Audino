# Four Pillars of OOP in Audino Healthcare System

## 1. ENCAPSULATION
*Hiding internal details and providing controlled access*

### Real Examples from the Code:

#### A. Patient Class - Data Protection
```java
// Patient.java
public class Patient {
    private String patientId;        // Hidden from outside
    private String firstName;        // Private data
    private String lastName;         // Cannot be directly changed
    private List<String> allergies;  // Protected list
    
    // Controlled access through methods
    public String getPatientId() { 
        return patientId; 
    }
    
    public String getFullName() { 
        return firstName + " " + lastName;  // Combines private fields safely
    }
    
    public List<String> getAllergies() { 
        return new ArrayList<>(allergies);  // Returns copy, not original
    }
    
    public void addAllergy(String allergy) {
        if (allergy != null && !allergy.trim().isEmpty()) {  // Validates before adding
            allergies.add(allergy);
        }
    }
}
```
**Implementation:** Other classes cannot directly access patient data. They must use the provided methods, which include validation.

#### B. DataService Class - Hidden Database Operations
```java
// DataService.java
public class DataService {
    private List<Patient> patients;           // Hidden storage
    private ObjectMapper objectMapper;       // Hidden JSON processor
    private List<Prescription> prescriptions; // Private data
    
    // Simple public interface
    public void savePatient(Patient patient) {
        // Complex operations hidden inside:
        // - Validation
        // - JSON conversion
        // - File writing
        // - Error handling
        patients.add(patient);
        savePatientsToFile();  // Internal method
    }
    
    // Users call this simple method - complexity is hidden
    public List<Patient> searchPatients(String query) {
        // Complex filtering logic hidden here
        return patients.stream()
            .filter(p -> p.getFullName().toLowerCase().contains(query.toLowerCase()))
            .collect(Collectors.toList());
    }
}
```
**Benefit:** Controllers do not need to know about JSON files, ObjectMapper, or file operations. The savePatient() method handles all complexity internally.

---

## 2. INHERITANCE
*One class inherits properties and behaviors from another*

### Real Examples from the Code:

#### A. Custom Exception Classes
```java
// Custom exception hierarchy
public class DataServiceException extends Exception {
    public DataServiceException(String message) {
        super(message);  // Inherits from Exception class
    }
}

public class PatientNotFoundException extends DataServiceException {
    public PatientNotFoundException(String patientId) {
        super("Patient not found with ID: " + patientId);  // Inherits constructor
    }
}
```
**Implementation:** PatientNotFoundException automatically gets all the features of Exception (like stack traces, error messages) plus adds its own specific behavior.

#### B. Model Base Class Pattern
```java
// Base entity class implementation
public abstract class BaseEntity {
    protected String id;                    // All entities have ID
    protected LocalDateTime createdDate;    // All entities have creation date
    
    public String getId() { return id; }
    public LocalDateTime getCreatedDate() { return createdDate; }
}

// Classes inherit common features
public class Patient extends BaseEntity {
    private String firstName;
    private String lastName;
    // Patient gets 'id' and 'createdDate' automatically from BaseEntity
}

public class Medication extends BaseEntity {
    private String displayName;
    private String genericName;
    // Medication also gets 'id' and 'createdDate' from BaseEntity
}
```
**Benefit:** No need to write ID and creation date code in every class. Write once, use everywhere.

#### C. Controller Inheritance from JavaFX
```java
// MainController inherits JavaFX capabilities
public class MainController implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inherits the contract from Initializable interface
        // Must implement this method
        setupPatientListView();
        setupMedicationComboBox();
    }
}
```
**Implementation:** JavaFX automatically calls the initialize() method when the window loads because the controller implements this interface.

---

## 3. POLYMORPHISM
*Same method name, different behaviors depending on the object*

### Real Examples from the Code:

#### A. Strategy Pattern for Interaction Checking
```java
// Same interface, different implementations
public interface InteractionCheckStrategy {
    List<InteractionAlert> checkInteraction(Patient patient, List<PrescribedDrug> drugs);
}

// Different classes, same method name, different behavior
public class AllergyCheckStrategy implements InteractionCheckStrategy {
    @Override
    public List<InteractionAlert> checkInteraction(Patient patient, List<PrescribedDrug> drugs) {
        // Checks for allergy conflicts
        List<InteractionAlert> alerts = new ArrayList<>();
        for (String allergy : patient.getAllergies()) {
            for (PrescribedDrug drug : drugs) {
                if (drug.getMedication().getMedicationClasses().contains(allergy)) {
                    alerts.add(new InteractionAlert(AlertLevel.CRITICAL, "Allergy Alert", 
                        "Patient allergic to " + allergy));
                }
            }
        }
        return alerts;
    }
}

public class DrugDrugCheckStrategy implements InteractionCheckStrategy {
    @Override
    public List<InteractionAlert> checkInteraction(Patient patient, List<PrescribedDrug> drugs) {
        // Checks for drug-drug interactions
        List<InteractionAlert> alerts = new ArrayList<>();
        for (int i = 0; i < drugs.size(); i++) {
            for (int j = i + 1; j < drugs.size(); j++) {
                // Complex drug interaction logic here
                if (drugsInteract(drugs.get(i), drugs.get(j))) {
                    alerts.add(new InteractionAlert(AlertLevel.WARNING, "Drug Interaction", 
                        "Possible interaction between medications"));
                }
            }
        }
        return alerts;
    }
}

// In InteractionEngine - same call, different behaviors
public class InteractionEngine {
    private List<InteractionCheckStrategy> strategies = new ArrayList<>();
    
    public void checkAllInteractions() {
        for (InteractionCheckStrategy strategy : strategies) {
            // Same method call 'checkInteraction()' but different behavior each time
            List<InteractionAlert> alerts = strategy.checkInteraction(patient, drugs);
            allAlerts.addAll(alerts);
        }
    }
}
```
**Implementation:** The same method checkInteraction() produces different results depending on which strategy object is used.

#### B. Collection Polymorphism in Controllers
```java
// MainController.java
public class MainController {
    // Same List interface, different implementations
    private final ObservableList<Patient> patientList = FXCollections.observableArrayList();
    private final ObservableList<Medication> medicationList = FXCollections.observableArrayList();
    private final ObservableList<PrescribedDrug> prescribedDrugList = FXCollections.observableArrayList();
    
    // Same method calls work on all lists
    public void clearAllLists() {
        patientList.clear();        // Same method name
        medicationList.clear();     // Same method name
        prescribedDrugList.clear(); // Same method name
        
        // Same behavior (clearing) but different internal implementations
    }
}
```

---

## 4. ABSTRACTION
*Hiding complex implementation details behind simple interfaces*

### Real Examples from the Code:

#### A. DataService Abstraction
```java
// Complex operations hidden behind simple methods
public class DataService {
    
    // Simple interface - complex implementation hidden
    public void savePatient(Patient patient) {
        // This method internally handles:
        // 1. Validates patient data
        // 2. Generates unique ID if needed
        // 3. Converts to JSON format
        // 4. Handles file I/O operations
        // 5. Manages error conditions
        // 6. Updates in-memory lists
        
        // Callers do not need to know about this complexity
    }
    
    // Another abstraction example
    public List<Patient> searchPatients(String query) {
        // Simple to use - just pass a search string
        // But internally handles:
        // - Case-insensitive matching
        // - Multiple field searching
        // - Performance optimization
        // - Result sorting
        
        // Callers do not care about the implementation details
    }
}
```

#### B. MainController UI Abstraction
```java
// MainController.java
public class MainController {
    
    // Simple method call abstracts complex UI operations
    private void updateUIState() {
        // This one method call handles:
        // - Enabling/disabling multiple buttons
        // - Updating status labels
        // - Refreshing table views
        // - Managing prescription status
        // - Coordinating multiple UI components
        
        boolean patientSelected = selectedPatient != null;
        boolean prescriptionLoaded = currentPrescription != null;
        
        // Complex logic simplified into clear boolean conditions
        addMedicationBtn.setDisable(!prescriptionLoaded);
        saveBtn.setDisable(!hasUnsavedChanges || !isDraft);
        newPrescriptionBtn.setDisable(!patientSelected);
    }
    
    // Another abstraction - interaction checking
    private void checkInteractions() {
        // Simple method call that abstracts:
        // - Async processing
        // - Multiple strategy execution
        // - Thread management
        // - UI thread safety
        // - Error handling
        
        CompletableFuture<List<InteractionAlert>> future = interactionEngine.checkAllInteractionsAsync();
        // Callers do not need to know about CompletableFuture complexity
    }
}
```

#### C. Prescription Class Business Logic Abstraction
```java
// Prescription.java
public class Prescription {
    private List<PrescribedDrug> prescribedDrugs = new ArrayList<>();
    
    // Simple interface hides validation complexity
    public void addPrescribedDrug(PrescribedDrug drug) {
        // Simple call, but internally:
        // - Validates drug is not null
        // - Checks for duplicates
        // - Updates prescription status
        // - Maintains data integrity
        
        if (drug != null && !prescribedDrugs.contains(drug)) {
            prescribedDrugs.add(drug);
            setStatus(PrescriptionStatus.DRAFT);  // Auto-update status
        }
    }
    
    // Business logic abstracted into simple method
    public boolean isEmpty() {
        return prescribedDrugs.isEmpty();  // Simple but essential business rule
    }
    
    // Complex calculation hidden behind simple interface
    public int getTotalMedicationCount() {
        return prescribedDrugs.size();  // Could be more complex in real system
    }
}
```

---

## Summary

### 1. ENCAPSULATION = Keep private things private
- Patient data is protected - only accessible through safe methods
- Database operations are hidden in DataService
- UI components are private in controllers

### 2. INHERITANCE = Child gets parent features
- Exception classes inherit from Java Exception class
- Controllers inherit from JavaFX interfaces
- All entities can inherit common ID and date fields

### 3. POLYMORPHISM = Same name, different behaviors
- Different interaction checking strategies with same method name
- Different alert types responding to same interface
- Same list operations working on different data types

### 4. ABSTRACTION = Hide complexity behind simple interfaces
- savePatient() - simple call, complex operations hidden
- updateUIState() - one call updates entire interface
- checkInteractions() - simple call, complex async processing hidden

The Audino project demonstrates professional OOP design with all four pillars properly implemented for maintainable and extensible code.