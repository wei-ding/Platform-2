SELECT 
    subject_id as person_id, 
    0 as condition_concept_id, 
    NULL as condition_start_date, 
    NULL as condition_end_date, 
    0 as condition_type_concept_id, 
    NULL as stop_reason, 
    NULL as provider_id, 
    NULL as visit_occurrence_id,
    code as condition_source_value, 
    NULL as condition_source_concept_id
FROM mimic2v26.icd9;

