SELECT 
    icustay_id as visit_occurrence_id, 
    subject_id as person_id, 
    9203 as visit_concept_id, 
    begintime as visit_start_date, 
    pg_catalog.time(begintime) as visit_start_time, 
    endtime as visit_end_date, 
    pg_catalog.time(endtime) as visit_end_time, 
    NULL as visit_type_concept_id, 
    NULL as provider_id,
    NULL as care_site_id,
    NULL as visit_source_value,
    NULL as visit_source_concept_id
FROM mimic2v26.icustay_days;

