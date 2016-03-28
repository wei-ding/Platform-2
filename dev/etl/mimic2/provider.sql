SELECT 
    cgid as provider_id, 
    NULL as provider_name,
    NULL as NPI,
    NULL as DEA,
    NULL as specialty_concept_id,
    NULL as care_site_id,
    NULL as year_of_birth,
    NULL as gender_concept_id,
    cgid as provider_source_value, 
    label as specialty_source_value, 
    NULL as specialty_concept_id,
    NULL as specialty_source_concept_id,
    NULL as gender_source_value,
    NULL as gender_source_concept_id
FROM 
    mimic2v26.d_caregivers;

