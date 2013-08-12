/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***            fix for pangroup banner
***
***     =====================================================================================================================
**/

BEGIN TRANSACTION;

UPDATE pangroup.site_image_resource
SET file_name = regexp_replace(file_name,' ','_','g');

DELETE FROM pangroup.portal_image_set$image_set;
DELETE FROM pangroup.site_descriptor$banner;

INSERT INTO pangroup.portal_image_set$image_set (id,owner,value,seq)
(SELECT nextval('public.portal_image_set$image_set_seq'),p.id AS owner,
        i.id AS value,0 AS seq
 FROM   pangroup.portal_image_set p,pangroup.site_image_resource i 
 WHERE  i.file_name ~ '^Website');
 
INSERT INTO pangroup.site_descriptor$banner(id,owner,value,seq)
(SELECT nextval('public.site_descriptor$banner_seq') AS id, s.id AS owner,
        i.id AS value, 0 AS seq
 FROM   pangroup.site_descriptor s,pangroup.portal_image_set i); 
 
COMMIT;      
 
 
    
