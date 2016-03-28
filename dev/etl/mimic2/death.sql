SELECT 
    subject_id as person_id, 
    dod as death_date, 
    0 as death_type_concept_id, 
    NULL as cause_concept_id, 
    NULL as cause_source_value, 
    NULL as cause_source_concept_id
FROM mimic2v26.d_patients
WHERE hospital_expire_flg = 'Y';

