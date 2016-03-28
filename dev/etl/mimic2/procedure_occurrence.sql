SELECT 
    procs.subject_id as person_id, 
    0 as procedure_concept_id,
    procs.proc_dt as procedure_date,
    0 as procedure_type_concept_id,
    NULL as modifier_concept_id,
    NULL as quantity,
    NULL as provider_id,
    NULL as visit_occurrence_id,
    procs.code as procedure_source_value,
    NULL as procedure_source_concept_id,
    NULL as qualifier_source_value
FROM
    (
        SELECT 
            * 
        FROM 
            mimic2v26.procedureevents, mimic2v26.d_codeditems
        WHERE 
            mimic2v26.procedureevents.itemid = mimic2v26.d_codeditems.itemid
    ) as procs;

