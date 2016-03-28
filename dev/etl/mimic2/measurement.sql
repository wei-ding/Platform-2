SELECT 
    labs.subject_id as person_id, 
    0 as measurement_concept_id, 
    labs.charttime as measurement_date, 
    pg_catalog.time(labs.charttime) as measurement_time, 
    0 as measurement_type_concept_id,
    NULL as operator_concept_id,
    labs.valuenum as value_as_number, 
    NULL as value_as_concept_id,
    NULL as unit_concept_id,
    NULL as range_low,
    NULL as range_high,
    NULL as provider_id,
    icustay_id as visit_occurrence_id,
    labs.LOINC_CODE measurement_source_value,
    NULL as measurement_source_concept_id,
    labs.valueuom as unit_source_value,
    NULL as value_source_value
FROM
    (SELECT 
            * 
     FROM 
        mimic2v26.d_labitems, mimic2v26.labevents
     WHERE 
        mimic2v26.d_labitems.itemid =  mimic2v26.labevents.itemid
    ) as labs;


