# Audino Healthcare System - Activity Diagram

## Activity Diagram: Add Medication Use Case

```
                    ┌─────────────────────┐
                    │       START         │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │  Select Patient     │
                    │  from List          │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │ Load Patient Data   │
                    │ & Existing          │
                    │ Prescription        │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │ Check if Current    │
           ┌────────┤ Prescription Exists?├────────┐
           │        └─────────────────────┘        │
           │ NO                                    │ YES
           ▼                                       ▼
┌─────────────────────┐              ┌─────────────────────┐
│ Create New          │              │ Load Existing       │
│ Prescription        │              │ Prescription        │
│ (DRAFT Status)      │              │ Data               │
└──────────┬──────────┘              └──────────┬──────────┘
           │                                    │
           └─────────────────┬──────────────────┘
                             │
                             ▼
                    ┌─────────────────────┐
                    │ Search & Select     │
                    │ Medication from     │
                    │ ComboBox           │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │ Enter Dosage,       │
                    │ Frequency &         │
                    │ Duration            │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │ Validate Input      │
           ┌────────┤ Data               ├────────┐
           │        └─────────────────────┘        │
           │ Invalid                              │ Valid
           ▼                                       ▼
┌─────────────────────┐              ┌─────────────────────┐
│ Show Warning        │              │ Add Medication to   │
│ Alert with          │              │ Prescription        │
│ Error Message       │              │ (In Memory)         │
└──────────┬──────────┘              └──────────┬──────────┘
           │                                    │
           └─────────────────┬──────────────────┘
                             │
                             ▼
                    ┌─────────────────────┐
                    │ Update Prescription │
                    │ Table View          │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │ Run Interaction     │
                    │ Engine Check        │
                    │ (Async)             │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │ Display Interaction │
                    │ Alerts & Summary    │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │ Set Status to       │
                    │ DRAFT (Unsaved)     │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │ Enable Save Button  │
                    │ & Show Status       │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │ User Clicks         │
           ┌────────┤ Save Button?        ├────────┐
           │        └─────────────────────┘        │
           │ NO                                    │ YES
           ▼                                       ▼
┌─────────────────────┐              ┌─────────────────────┐
│ Medication Added    │              │ Save Prescription   │
│ to Prescription     │              │ to JSON Database    │
│ (Memory Only)       │              └──────────┬──────────┘
└─────────────────────┘                         │
                                                 ▼
                                      ┌─────────────────────┐
                                      │ Set Status to       │
                                      │ APPROVED            │
                                      └──────────┬──────────┘
                                                 │
                                                 ▼
                                      ┌─────────────────────┐
                                      │ Show Success        │
                                      │ Message             │
                                      └──────────┬──────────┘
                                                 │
                                                 ▼
                                      ┌─────────────────────┐
                                      │       END           │
                                      └─────────────────────┘
```

## Decision Points:
- **Patient Selection**: Must select patient before adding medications
- **Prescription Existence**: System checks for existing prescription or creates new one
- **Input Validation**: Dosage format must match medication type
- **Save Decision**: User controls when to persist data to database

## Parallel Processes:
- **Interaction Checking**: Runs asynchronously in background
- **UI Updates**: Real-time updates of prescription table and alerts
- **Search Filtering**: Live medication search as user types