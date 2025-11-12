# Audino Healthcare System - Sequence Diagram

## Sequence Diagram: Add Medication Use Case

```
User          MainController    DataService    InteractionEngine    JSONFiles
 │                 │                │                 │               │
 │ selectPatient() │                │                 │               │
 ├─────────────────►               │                 │               │
 │                 │                │                 │               │
 │                 │ getActivePrescriberionForPatient(patientId)      │
 │                 ├────────────────►               │               │
 │                 │                │                 │               │
 │                 │                │ readFromJSON() │               │
 │                 │                ├─────────────────────────────────►
 │                 │                │                 │               │
 │                 │                │ prescriptions.json             │
 │                 │                ◄─────────────────────────────────┤
 │                 │                │                 │               │
 │                 │ return Prescription             │               │
 │                 ◄────────────────┤                 │               │
 │                 │                │                 │               │
 │ updateUI()      │                │                 │               │
 ◄─────────────────┤                │                 │               │
 │                 │                │                 │               │
 │ searchMedication│                │                 │               │
 ├─────────────────►               │                 │               │
 │                 │                │                 │               │
 │                 │ searchMedications(query)        │               │
 │                 ├────────────────►               │               │
 │                 │                │                 │               │
 │                 │ return filteredList             │               │
 │                 ◄────────────────┤                 │               │
 │                 │                │                 │               │
 │ selectMedication│                │                 │               │
 │ & enterDosage   │                │                 │               │
 ├─────────────────►               │                 │               │
 │                 │                │                 │               │
 │ clickAddMedication()             │                 │               │
 ├─────────────────►               │                 │               │
 │                 │                │                 │               │
 │                 │ validateInput()│                 │               │
 │                 ├──────────┐     │                 │               │
 │                 │          │     │                 │               │
 │                 │◄─────────┘     │                 │               │
 │                 │                │                 │               │
 │                 │ addPrescribedDrug()             │               │
 │                 ├──────────┐     │                 │               │
 │                 │          │     │                 │               │
 │                 │◄─────────┘     │                 │               │
 │                 │                │                 │               │
 │                 │ updatePrescriptionTable()       │               │
 │                 ├──────────┐     │                 │               │
 │                 │          │     │                 │               │
 │                 │◄─────────┘     │                 │               │
 │                 │                │                 │               │
 │                 │ checkAllInteractionsAsync()     │               │
 │                 ├──────────────────────────────────►               │
 │                 │                │                 │               │
 │                 │                │                 │ processRules() │
 │                 │                │                 ├───────┐       │
 │                 │                │                 │       │       │
 │                 │                │                 │◄──────┘       │
 │                 │                │                 │               │
 │                 │ return CompletableFuture<List<InteractionAlert>> │
 │                 ◄──────────────────────────────────┤               │
 │                 │                │                 │               │
 │                 │ updateAlertsView()              │               │
 │                 ├──────────┐     │                 │               │
 │                 │          │     │                 │               │
 │                 │◄─────────┘     │                 │               │
 │                 │                │                 │               │
 │ showDraftStatus │                │                 │               │
 ◄─────────────────┤                │                 │               │
 │                 │                │                 │               │
 │ clickSave()     │                │                 │               │
 ├─────────────────►               │                 │               │
 │                 │                │                 │               │
 │                 │ savePrescription()              │               │
 │                 ├────────────────►               │               │
 │                 │                │                 │               │
 │                 │                │ writeToJSON()  │               │
 │                 │                ├─────────────────────────────────►
 │                 │                │                 │               │
 │                 │                │ prescriptions.json             │
 │                 │                ◄─────────────────────────────────┤
 │                 │                │                 │               │
 │                 │ return success │                 │               │
 │                 ◄────────────────┤                 │               │
 │                 │                │                 │               │
 │ showSuccessMsg  │                │                 │               │
 ◄─────────────────┤                │                 │               │
```

## Key Interactions:

### 1. Patient Selection Phase
- User selects patient → Controller loads existing prescription from DataService
- DataService reads from JSON files to get prescription data
- UI updates with patient information and prescription history

### 2. Medication Search Phase 
- User types in search field → Controller calls DataService.searchMedications()
- Real-time filtering of medication list from in-memory data
- ComboBox updates with filtered results

### 3. Add Medication Phase
- User fills form and clicks Add → Controller validates input locally
- Medication added to current prescription object (in-memory only)
- Prescription table updated immediately
- Interaction engine called asynchronously for safety checks

### 4. Interaction Checking (Async)
- InteractionEngine runs strategies in parallel
- Checks drug-drug, drug-allergy, and condition interactions
- Returns CompletableFuture with alerts list
- UI updated with interaction alerts when processing completes

### 5. Save Phase
- User clicks Save → Controller calls DataService.savePrescription()
- DataService writes updated prescription to JSON file
- Prescription status changed from DRAFT to APPROVED
- Success message shown to user

## Asynchronous Operations:
- **Interaction Checking**: Non-blocking background process
- **File I/O**: JSON read/write operations
- **UI Updates**: Platform.runLater() for thread-safe UI updates