SELECT  
    durgs.subject_id as person_id, 
    0 as durg_concept_id, 
    durgs.charttime as drug_exposure_start_date, 
    NULL as drug_exposure_end_date,
    0 as drug_type_concept_id,
    NULL as stop_reason,
    NULL as refills,
    NULL as quantity,
    NULL as days_supply,
    NULL as sig,
    NULL as route_concept_id,
    durgs.dose as effective_drug_dose, 
    NULL as dose_unit_concept_id,
    NULL as lot_number,
    durgs.cgid as provider_id, 
    durgs.icustay_id as visit_occurrence_id,
    durgs.label as drug_source_value, 
    0 as drug_source_concept_id, 
    durgs.route as route_source_value, 
    durgs.doseuom as dose_unit_source_value
FROM
    (
            SELECT 
                * 
            FROM 
                mimic2v26.medevents, 
                mimic2v26.d_meditems
            WHERE 
                mimic2v26.medevents.itemid =  mimic2v26.d_meditems.itemid
        ) as durgs;


