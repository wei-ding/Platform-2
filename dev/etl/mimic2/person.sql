SELECT 
    subject_id as person_id, 
    CASE
        when sex = 'M' then 8507
        when sex = 'F' then 8532
        else 8851
    END as gender_concept_id,
    extract (year from dob) as year_of_birth,
    extract (month from dob) as month_of_birth, 
    extract (day from dob) as day_of_birth,
    NULL as time_of_birth,
    0 as race_concept_id,
    0 as ethnicity_concept_id, 
    NULL as location_id,
    NULL as provider_id,
    NULL as care_site_id,
    NULL as person_source_value,
    sex as gender_source_value,
    NULL as gender_source_concept_id,
    NULL as race_source_value,
    NULL as race_source_concept_id,
    NULL as ethnicity_source_value,
    NULL as ethnicity_source_concept_id
FROM mimic2v26.d_patients;



