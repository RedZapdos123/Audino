# Audino Healthcare System - Object Diagram

## Object Diagram: Adding Medication Workflow

```
┌─────────────────────────────────────────────────────────────┐
│                    mainController : MainController          │
│ ─────────────────────────────────────────────────────────── │
│ selectedPatient = patient1                                  │
│ currentPrescription = prescription1                         │
│ dataService = dataService1                                  │
│ interactionEngine = interactionEngine1                     │
└─────────────────────────────────────────────────────────────┘
                                │
                                │ selectedPatient
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                    patient1 : Patient                      │
│ ─────────────────────────────────────────────────────────── │
│ patientId = "PAT-12345678"                                  │
│ firstName = "John"                                          │
│ lastName = "Doe"                                            │
│ dateOfBirth = 1990-05-15                                    │
│ gender = "Male"                                             │
│ allergies = ["Penicillin", "Sulfa"]                        │
│ chronicConditions = ["Hypertension", "Diabetes"]           │
└─────────────────────────────────────────────────────────────┘
                                │
                                │ has
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                prescription1 : Prescription                 │
│ ─────────────────────────────────────────────────────────── │
│ prescriptionId = "RX-87654321"                              │
│ patient = patient1                                          │
│ prescriber = "Dr. User"                                     │
│ dateCreated = 2025-11-13T10:30:00                          │
│ status = DRAFT                                              │
│ prescribedDrugs = [prescribedDrug1, prescribedDrug2]       │
│ alerts = [alert1, alert2]                                  │
└─────────────────────────────────────────────────────────────┘
                                │
                                │ contains
                                ▼
┌─────────────────────────────────────────────────────────────┐
│              prescribedDrug1 : PrescribedDrug               │
│ ─────────────────────────────────────────────────────────── │
│ medicationId = "MED-001"                                    │
│ medication = medication1                                    │
│ dosage = "10mg"                                             │
│ frequency = "Twice daily"                                   │
│ duration = "7 days"                                         │
│ instructions = "Take with food"                             │
│ prescriber = "Dr. User"                                     │
└─────────────────────────────────────────────────────────────┘
                                │
                                │ references
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                 medication1 : Medication                    │
│ ─────────────────────────────────────────────────────────── │
│ medicationId = "MED-001"                                    │
│ displayName = "Lisinopril"                                  │
│ genericName = "Lisinopril"                                  │
│ medicationType = TABLET                                     │
│ medicationClasses = ["ACE Inhibitor"]                      │
│ strength = "10"                                             │
│ unit = "mg"                                                 │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│              prescribedDrug2 : PrescribedDrug               │
│ ─────────────────────────────────────────────────────────── │
│ medicationId = "MED-002"                                    │
│ medication = medication2                                    │
│ dosage = "500mg"                                            │
│ frequency = "Once daily"                                    │
│ duration = "30 days"                                        │
│ instructions = "Take in morning"                            │
│ prescriber = "Dr. User"                                     │
└─────────────────────────────────────────────────────────────┘
                                │
                                │ references
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                 medication2 : Medication                    │
│ ─────────────────────────────────────────────────────────── │
│ medicationId = "MED-002"                                    │
│ displayName = "Metformin"                                   │
│ genericName = "Metformin HCl"                               │
│ medicationType = TABLET                                     │
│ medicationClasses = ["Biguanide", "Antidiabetic"]          │
│ strength = "500"                                            │
│ unit = "mg"                                                 │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                 dataService1 : DataService                 │
│ ─────────────────────────────────────────────────────────── │
│ patients = [patient1, patient2, patient3]                  │
│ medications = [medication1, medication2, medication3...]    │
│ prescriptions = [prescription1, prescription2]             │
│ interactionRules = [rule1, rule2, rule3...]                │
│ objectMapper = ObjectMapper instance                       │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│            interactionEngine1 : InteractionEngine          │
│ ─────────────────────────────────────────────────────────── │
│ strategies = [allergyStrategy, drugDrugStrategy,            │
│              conditionStrategy]                             │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                    alert1 : InteractionAlert               │
│ ─────────────────────────────────────────────────────────── │
│ alertLevel = WARNING                                        │
│ alertType = "Drug-Condition Interaction"                   │
│ message = "Lisinopril may affect blood pressure monitoring │
│           in hypertensive patients"                         │
│ acknowledged = false                                        │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                    alert2 : InteractionAlert               │
│ ─────────────────────────────────────────────────────────── │
│ alertLevel = INFO                                           │
│ alertType = "Drug-Drug Synergy"                             │
│ message = "Lisinopril and Metformin work well together     │
│           for diabetic patients with hypertension"         │
│ acknowledged = false                                        │
└─────────────────────────────────────────────────────────────┘
```

## Object Relationships in Runtime:
1. **MainController** holds references to active Patient and current Prescription
2. **Patient** object contains personal data and medical history
3. **Prescription** aggregates multiple PrescribedDrug objects
4. **PrescribedDrug** references Medication from the master medication list
5. **DataService** manages persistence of all entities to JSON files
6. **InteractionEngine** processes current prescription and generates alerts