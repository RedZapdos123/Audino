# Audino Healthcare System - Class Diagram

## UML Class Diagram

```
┌─────────────────────────────────────┐
│            MainController           │
├─────────────────────────────────────┤
│ - dataService: DataService          │
│ - interactionEngine: InteractionEngine │
│ - selectedPatient: Patient          │
│ - currentPrescription: Prescription │
│ - patientList: ObservableList<Patient> │
│ - medicationList: ObservableList<Medication> │
│ - prescribedDrugList: ObservableList<PrescribedDrug> │
│ - alertList: ObservableList<InteractionAlert> │
├─────────────────────────────────────┤
│ + initialize(): void                │
│ + handlePatientSelection(Patient): void │
│ + handleAddMedication(): void       │
│ + handleSave(): void                │
│ + handleRefresh(): void             │
│ + checkInteractions(): void         │
│ + updateUIState(): void             │
└─────────────────────────────────────┘
                    │
                    │ uses
                    ▼
┌─────────────────────────────────────┐
│            DataService              │
├─────────────────────────────────────┤
│ - objectMapper: ObjectMapper        │
│ - patients: List<Patient>           │
│ - medications: List<Medication>     │
│ - prescriptions: List<Prescription> │
│ - interactionRules: List<InteractionRule> │
├─────────────────────────────────────┤
│ + loadAllData(): void               │
│ + savePatient(Patient): void        │
│ + savePrescription(Prescription): void │
│ + searchPatients(String): List<Patient> │
│ + searchMedications(String): List<Medication> │
│ + getActivePrescriberionForPatient(String): Prescription │
└─────────────────────────────────────┘
                    │
                    │ manages
                    ▼
┌─────────────────────────────────────┐
│              Patient                │
├─────────────────────────────────────┤
│ - patientId: String                 │
│ - firstName: String                 │
│ - lastName: String                  │
│ - dateOfBirth: LocalDate           │
│ - gender: String                    │
│ - allergies: List<String>           │
│ - chronicConditions: List<String>   │
├─────────────────────────────────────┤
│ + getFullName(): String             │
│ + getAge(): int                     │
│ + addAllergy(String): void          │
│ + addChronicCondition(String): void │
└─────────────────────────────────────┘
                    │
                    │ has
                    ▼
┌─────────────────────────────────────┐
│            Prescription             │
├─────────────────────────────────────┤
│ - prescriptionId: String            │
│ - patient: Patient                  │
│ - prescriber: String                │
│ - dateCreated: LocalDateTime        │
│ - status: PrescriptionStatus        │
│ - prescribedDrugs: List<PrescribedDrug> │
│ - alerts: List<InteractionAlert>    │
├─────────────────────────────────────┤
│ + addPrescribedDrug(PrescribedDrug): void │
│ + removePrescribedDrug(PrescribedDrug): void │
│ + isEmpty(): boolean                │
│ + setStatus(PrescriptionStatus): void │
└─────────────────────────────────────┘
                    │
                    │ contains
                    ▼
┌─────────────────────────────────────┐
│            PrescribedDrug           │
├─────────────────────────────────────┤
│ - medicationId: String              │
│ - medication: Medication            │
│ - dosage: String                    │
│ - frequency: String                 │
│ - duration: String                  │
│ - instructions: String              │
│ - prescriber: String                │
├─────────────────────────────────────┤
│ + getMedication(): Medication       │
│ + setMedication(Medication): void   │
│ + getDosage(): String               │
│ + getFrequency(): String            │
└─────────────────────────────────────┘
                    │
                    │ references
                    ▼
┌─────────────────────────────────────┐
│            Medication               │
├─────────────────────────────────────┤
│ - medicationId: String              │
│ - displayName: String               │
│ - genericName: String               │
│ - medicationType: MedicationType    │
│ - medicationClasses: List<String>   │
│ - strength: String                  │
│ - unit: String                      │
├─────────────────────────────────────┤
│ + getDisplayName(): String          │
│ + isValidDosage(String): boolean    │
│ + getMedicationClasses(): List<String> │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│          InteractionEngine          │
├─────────────────────────────────────┤
│ - strategies: List<InteractionCheckStrategy> │
├─────────────────────────────────────┤
│ + checkAllInteractionsAsync(): CompletableFuture<List<InteractionAlert>> │
│ + addStrategy(InteractionCheckStrategy): void │
│ + shutdown(): void                  │
└─────────────────────────────────────┘
                    │
                    │ uses
                    ▼
┌─────────────────────────────────────┐
│    InteractionCheckStrategy         │
│         <<interface>>               │
├─────────────────────────────────────┤
│ + checkInteraction(Patient, List<PrescribedDrug>, List<InteractionRule>, List<Medication>): List<InteractionAlert> │
└─────────────────────────────────────┘
                    △
                    │ implements
    ┌───────────────┼───────────────┐
    │               │               │
┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│AllergyCheck │ │DrugDrugCheck│ │ConditionCheck│
│Strategy     │ │Strategy     │ │Strategy     │
└─────────────┘ └─────────────┘ └─────────────┘

┌─────────────────────────────────────┐
│         InteractionAlert            │
├─────────────────────────────────────┤
│ - alertLevel: AlertLevel            │
│ - alertType: String                 │
│ - message: String                   │
│ - acknowledged: boolean             │
├─────────────────────────────────────┤
│ + acknowledge(): void               │
│ + getFormattedMessage(): String     │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│           AlertLevel                │
│          <<enum>>                   │
├─────────────────────────────────────┤
│ CRITICAL                            │
│ WARNING                             │
│ INFO                                │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│       PrescriptionStatus            │
│          <<enum>>                   │
├─────────────────────────────────────┤
│ DRAFT                               │
│ APPROVED                            │
│ CANCELLED                           │
└─────────────────────────────────────┘
```

## Relationships:
- **MainController** → **DataService**: Uses (Dependency)
- **MainController** → **InteractionEngine**: Uses (Dependency)
- **Patient** → **Prescription**: One-to-Many (Composition)
- **Prescription** → **PrescribedDrug**: One-to-Many (Composition)
- **PrescribedDrug** → **Medication**: Many-to-One (Association)
- **InteractionEngine** → **InteractionCheckStrategy**: One-to-Many (Strategy Pattern)
- **DataService** manages all entities through JSON persistence
