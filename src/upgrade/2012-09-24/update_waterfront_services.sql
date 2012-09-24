/**
***	=======================================================================
***	
***		Update for Waterfront service versions, to include parking 
***		that was added at after the lease was created 
***
***	=======================================================================
**/

BEGIN TRANSACTION;

-- Update for services - added parking for already existing leases

INSERT INTO waterfront.service_v$features (id,owner,value,seq) 
(SELECT nextval('public.service_v$features_seq') AS id, a.id AS owner,c.id AS value,0 as seq
FROM	waterfront.service_v a
JOIN	waterfront.service b ON (a.holder = b.id)
JOIN	waterfront.feature c ON (b.catalog = c.catalog)
WHERE 	a.id NOT IN (SELECT DISTINCT owner FROM waterfront.service_v$features)) RETURNING *;


-- Delete locales other than English and Spanish

DELETE FROM waterfront.html_content WHERE locale IN (SELECT id FROM waterfront.available_locale WHERE lang NOT IN ('en'));
DELETE FROM waterfront.legal_terms_content WHERE locale IN (SELECT id FROM waterfront.available_locale WHERE lang NOT IN ('en'));
DELETE FROM waterfront.news WHERE locale IN (SELECT id FROM waterfront.available_locale WHERE lang NOT IN ('en'));
DELETE FROM waterfront.page_descriptor$caption WHERE value IN (SELECT id FROM waterfront.page_caption WHERE locale IN (SELECT id FROM waterfront.available_locale WHERE lang NOT IN ('en')));
DELETE FROM waterfront.page_caption WHERE locale IN (SELECT id FROM waterfront.available_locale WHERE lang NOT IN ('en'));
DELETE FROM waterfront.page_content WHERE locale IN (SELECT id FROM waterfront.available_locale WHERE lang NOT IN ('en'));
DELETE FROM waterfront.portal_image_resource WHERE locale IN (SELECT id FROM waterfront.available_locale WHERE lang NOT IN ('en'));
DELETE FROM waterfront.site_titles WHERE locale IN (SELECT id FROM waterfront.available_locale WHERE lang NOT IN ('en'));
DELETE FROM waterfront.testimonial WHERE locale IN (SELECT id FROM waterfront.available_locale WHERE lang NOT IN ('en'));
DELETE FROM waterfront.available_locale WHERE lang NOT IN ('en');


-- Manual commit after verification


