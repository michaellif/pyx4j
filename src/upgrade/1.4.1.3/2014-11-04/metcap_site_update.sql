/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***
***     metcap site update
***
***     ===========================================================================================================
**/        

\i tmp_metcap_export.sql

BEGIN TRANSACTION;

    -- delete all images refernced from site_image_resource
    
    DELETE FROM metcap.media_file_blob
    WHERE   id IN   (SELECT     file_blob_key
                    FROM        metcap.site_image_resource);
                    
    -- insert new images from tmp table into media_file_blob
    
    INSERT INTO metcap.media_file_blob (id,name,data,content_type,updated,created)
    (SELECT nextval('public.media_file_blob_seq') AS id, 
            t0.name, t0.data, t0.content_type, 
            DATE_TRUNC('second', current_timestamp)::timestamp AS updated,
            DATE_TRUNC('second', current_timestamp)::timestamp AS created
    FROM    _tmp_.media_file_blob t0
    JOIN    _tmp_.site_image_resource t1 ON (t0.id = t1.file_blob_key));


    UPDATE  metcap.site_descriptor
    SET     skin = 'skin1',
            enabled = TRUE,
            crm_logo = NULL;
    
    -- delete from metcap.site_descriptor$logo
    
    DELETE FROM metcap.site_descriptor$logo;
    
    -- delete from metcap.site_logo_image_resource
    
    DELETE FROM metcap.site_logo_image_resource;
    
    
    -- delete from site_descriptor$portal_banner
    
    DELETE FROM metcap.site_descriptor$portal_banner;
    
    -- delete from portal_banner_image
    
    DELETE FROM metcap.portal_banner_image;
    
    -- delete from site_image_resource
    
    DELETE FROM metcap.site_image_resource;
    
    -- insert into site_image_resource
    
    INSERT INTO metcap.site_image_resource(id,file_file_name,file_updated_timestamp,
    file_cache_version,file_file_size,file_content_mime_type,caption,
    description,file_blob_key)
    (SELECT nextval('public.site_image_resource_seq') AS id,
            t0.file_file_name,t0.file_updated_timestamp,
            t0.file_cache_version,t0.file_file_size,
            t0.file_content_mime_type,t0.caption,
            t0.description,b.id AS file_blob_key
    FROM    _tmp_.site_image_resource t0 
    JOIN    _tmp_.media_file_blob t1 ON (t1.id = t0.file_blob_key)
    JOIN   metcap.media_file_blob b ON (md5(t1.data) = md5(b.data)
                                        AND t1.name = b.name));
                                        
                                        
    -- update site_descriptor
    
    UPDATE  metcap.site_descriptor AS d 
    SET     crm_logo = i.id
    FROM    metcap.site_image_resource i
    WHERE   i.file_file_name = 'logo-crm.png';
    
    
    -- insert into site_logo_image_resource
    
    INSERT INTO metcap.site_logo_image_resource(id,locale)
    (SELECT nextval('public.site_logo_image_resource_seq') AS id,
            l.id AS locale
    FROM    metcap.available_locale l);
    
    UPDATE  metcap.site_logo_image_resource AS sl
    SET     small = t.small
    FROM    (SELECT i.id AS small
            FROM    metcap.site_image_resource i 
            JOIN    _tmp_.site_image_resource t0 ON (i.file_file_name = t0.file_file_name)
            JOIN    (SELECT DISTINCT small FROM _tmp_.site_logo_image_resource) AS t1 ON (t1.small = t0.id)) AS t;
            
    UPDATE  metcap.site_logo_image_resource AS sl
    SET     large = t.large
    FROM    (SELECT i.id AS large
            FROM    metcap.site_image_resource i 
            JOIN    _tmp_.site_image_resource t0 ON (i.file_file_name = t0.file_file_name)
            JOIN    (SELECT DISTINCT large FROM _tmp_.site_logo_image_resource) AS t1 ON (t1.large = t0.id)) AS t;
            
    UPDATE  metcap.site_logo_image_resource AS sl
    SET     logo_label = t.logo_label
    FROM    (SELECT i.id AS logo_label
            FROM    metcap.site_image_resource i 
            JOIN    _tmp_.site_image_resource t0 ON (i.file_file_name = t0.file_file_name)
            JOIN    (SELECT DISTINCT logo_label FROM _tmp_.site_logo_image_resource) AS t1 ON (t1.logo_label = t0.id)) AS t;
    
    -- insert into portal_banner_image
     
    INSERT INTO metcap.portal_banner_image(id,locale,image)
    (SELECT nextval('public.portal_banner_image_seq') AS id,
            l.id AS locale, i.id AS image
    FROM    metcap.available_locale l,
            metcap.site_image_resource i
    WHERE   i.file_file_name = 'portal-banner.png');
    
    
    -- insert into metcap.site_descriptor$logo
    
    INSERT INTO metcap.site_descriptor$logo(id,owner,value)
    (SELECT nextval('public.site_descriptor$logo_seq') AS id,
            s.id AS owner, l.id AS value
    FROM    metcap.site_descriptor s,
            metcap.site_logo_image_resource l);
    
    
    -- insert into site_descriptor$portal_banner
   
    INSERT INTO metcap.site_descriptor$portal_banner(id,owner,value)
    (SELECT nextval('public.site_descriptor$portal_banner_seq') AS id,
            s.id AS owner, b.id AS value
    FROM    metcap.portal_banner_image b,
            metcap.site_descriptor s);
    
    
   
    
    
    UPDATE  metcap.site_palette
    SET     object1 = 207,
            object2 = 123,
            contrast1 = 123,
            contrast2 = 123,
            contrast3 = 123,
            contrast4 = 123,
            contrast5 = 123,
            contrast6 = 123,
            foreground = 220,
            form_background = 270,
            site_background = 207;
            
    -- update operations 
    
    UPDATE  _admin_.admin_pmc_vista_features AS f 
    SET     white_label_portal = TRUE
    FROM    _admin_.admin_pmc AS a
    WHERE   a.features = f.id
    AND     a.namespace = 'metcap';
            
COMMIT;

DROP SCHEMA _tmp_ CASCADE;
