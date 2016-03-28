SELECT 
    label as care_site_name, 
    NULL as place_of_service_concept_id, 
    NULL as location_id,
    cuid as care_site_source_value,
    NULL as place_of_service_source_value
FROM mimic2v26.d_careunits;

