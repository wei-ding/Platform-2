SELECT 
    subject_id as person_id,
    charttime as note_date,
    pg_catalog.time(charttime) as note_time, 
    0 as note_type_concept_id,
    text as note_text, 
    cgid as provider_id,
    icustay_id as visit_occurrence_id, 
    category as note_source_value
FROM mimic2v26.noteevents
;


