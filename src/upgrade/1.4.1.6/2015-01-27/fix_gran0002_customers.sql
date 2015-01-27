/**
***     ===========================================================================================================
***
***     Fix duplicate customer records for dms gran0002 building
***
***     ===========================================================================================================
**/                                                    

/*
SELECT  c.id, c.person_name_first_name, c.person_name_last_name,
        c.person_email, l.lease_id, l.status, a.info_unit_number,
        lp.id_discriminator, lp.participant_id
FROM    dms.building b
JOIN    dms.apt_unit a ON (b.id = a.building)
JOIN    dms.lease l ON (a.id = l.unit)
JOIN    dms.lease_participant lp ON (l.id = lp.lease) 
JOIN    dms.customer c ON (c.id = lp.customer)
WHERE   b.property_code = 'gran0002'
ORDER BY 3,2,6;

*/

BEGIN TRANSACTION;

    WITH t AS ( SELECT  c.id, COALESCE(c.person_name_first_name,'') AS first_name,
                        COALESCE(c.person_name_last_name,'') AS last_name,
                        c.person_email, l.lease_id, l.status, a.info_unit_number
                FROM    dms.building b
                JOIN    dms.apt_unit a ON (b.id = a.building)
                JOIN    dms.lease l ON (a.id = l.unit)
                JOIN    dms.lease_participant lp ON (l.id = lp.lease) 
                JOIN    dms.customer c ON (c.id = lp.customer)
                WHERE   b.property_code = 'gran0002')
    UPDATE  dms.lease_participant AS lp
    SET     customer = t0.id 
    FROM    (SELECT * FROM t WHERE status = 'Completed') AS t0,
            (SELECT * FROM t WHERE status = 'Active') AS t1
    WHERE   lp.customer = t1.id
    AND     t0.first_name = t1.first_name
    AND     t0.last_name = t1.last_name
    AND     t0.info_unit_number = t1.info_unit_number
    AND     t0.lease_id != t1.lease_id
    AND     t0.last_name NOT IN ('Kay','Deosaran');
    
    UPDATE  dms.lease_participant
    SET     customer = 98468
    WHERE   customer = 159598;
    
COMMIT;
    

            
