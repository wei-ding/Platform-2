SELECT 
    specimens.subject_id as person_id, 
    0 as specimen_concept_id, 
    0 as specimen_type_concept_id, 
    specimens.charttime as specimen_date, 
    pg_catalog.time(specimens.charttime) as specimen_time, 
    NULL as quantity,
    NULL as unit_concept_id, 
    NULL as anatomic_site_concept_id, 
    NULL as disease_status_concept_id,
    specimens.loinc_code as specimen_source_id, 
    specimens.fluid as specimen_source_value, 
    specimens.valueuom as unit_source_value,
    NULL as anatomic_site_source_value,
    NULL as disease_status_source_value
FROM
(
    SELECT 
        * 
    FROM 
        mimic2v26.d_labitems as i, 
        mimic2v26.labevents as e
    WHERE 
        i.itemid =  e.itemid
 ) as specimens;


