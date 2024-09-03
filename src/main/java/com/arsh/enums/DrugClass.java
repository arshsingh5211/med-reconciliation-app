package com.arsh.enums;

    public enum DrugClass {
        // Cardiovascular
        ANTIHYPERTENSIVES("Cardiovascular", "ACE Inhibitors"),
        BETA_BLOCKERS("Cardiovascular", "Beta Blockers"),
        CALCIUM_CHANNEL_BLOCKERS("Cardiovascular", "Calcium Channel Blockers"),
        STATINS("Cardiovascular", "Statins"),
        DIURETICS("Cardiovascular", "Diuretics"),

        // Central Nervous System
        ANTIDEPRESSANTS("Central Nervous System", "SSRIs"),
        ANTIPSYCHOTICS("Central Nervous System", "Atypical Antipsychotics"),
        ANXIOLYTICS("Central Nervous System", "Benzodiazepines"),
        ANTICONVULSANTS("Central Nervous System", "Anticonvulsants"),
        STIMULANTS("Central Nervous System", "Stimulants"),

        // Endocrine
        INSULINS("Endocrine", "Insulins"),
        ORAL_HYPOGLYCEMICS("Endocrine", "Biguanides"),
        THYROID_HORMONES("Endocrine", "Thyroid Hormones"),
        CORTICOSTEROIDS("Endocrine", "Corticosteroids"),

        // Respiratory
        BRONCHODILATORS("Respiratory", "Beta-2 Agonists"),
        CORTICOSTEROIDS_RESP("Respiratory", "Inhaled Corticosteroids"),
        ANTICHOLINERGICS("Respiratory", "Anticholinergics"),
        LEUKOTRIENE_MODIFIERS("Respiratory", "Leukotriene Receptor Antagonists"),

        // Gastrointestinal
        ANTACIDS("Gastrointestinal", "Antacids"),
        H2_BLOCKERS("Gastrointestinal", "H2 Blockers"),
        PPIS("Gastrointestinal", "Proton Pump Inhibitors"),
        LAXATIVES("Gastrointestinal", "Laxatives"),
        ANTIEMETICS("Gastrointestinal", "5-HT3 Antagonists"),

        // Antibiotics
        PENICILLINS("Antibiotics", "Penicillins"),
        CEPHALOSPORINS("Antibiotics", "Cephalosporins"),
        FLUOROQUINOLONES("Antibiotics", "Fluoroquinolones"),
        MACROLIDES("Antibiotics", "Macrolides"),
        TETRACYCLINES("Antibiotics", "Tetracyclines"),

        // Pain Management
        NSAIDS("Analgesic", "NSAIDs"),
        OPIOIDS("Analgesic", "Opioids"),
        Pain_Reliever("Analgesic", "Pain reliever"),

        // Anticoagulants
        COUMARINS("Anticoagulants", "Coumarins"),
        DIRECT_THROMBIN_INHIBITORS("Anticoagulants", "Direct Thrombin Inhibitors"),

        // Immunosuppressants
        CALCINEURIN_INHIBITORS("Immunosuppressants", "Calcineurin Inhibitors"),
        MTOR_INHIBITORS("Immunosuppressants", "mTOR Inhibitors"),

        // Oncology
        ALKYLATING_AGENTS("Oncology", "Alkylating Agents"),
        ANTIMETABOLITES("Oncology", "Antimetabolites"),
        TAXANES("Oncology", "Taxanes"),

        // Antivirals
        NRTIS("Antivirals", "NRTIs"),
        PROTEASE_INHIBITORS("Antivirals", "Protease Inhibitors");

        private final String drugClass;
        private final String subCategory;

        DrugClass(String drugClass, String subCategory) {
            this.drugClass = drugClass;
            this.subCategory = subCategory;
        }

        public String getDrugClass() {
            return drugClass;
        }

        public String getSubCategory() {
            return subCategory;
        }

        public static DrugClass fromString(String value) {
            for (DrugClass drugClass : DrugClass.values()) {
                if (drugClass.getDrugClass().equalsIgnoreCase(value) ||
                        drugClass.name().equalsIgnoreCase(value)) {
                    return drugClass;
                }
            }
            throw new IllegalArgumentException("No enum constant for value: " + value);
        }
    }
