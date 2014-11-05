/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***
***     metcap site export
***
***     ===========================================================================================================
**/        

CREATE SCHEMA _tmp_;

CREATE TABLE _tmp_.media_file_blob AS 
(   SELECT * FROM metcap.media_file_blob) ;

CREATE TABLE _tmp_.site_image_resource AS 
(   SELECT * FROM metcap.site_image_resource) ;

CREATE TABLE _tmp_.site_logo_image_resource AS
(   SELECT * FROM metcap.site_logo_image_resource);

CREATE TABLE _tmp_.portal_banner_image AS
(   SELECT * FROM metcap.portal_banner_image) ;

CREATE TABLE _tmp_.site_descriptor$logo AS
(   SELECT * FROM metcap.site_descriptor$logo);

CREATE TABLE _tmp_.site_descriptor$portal_banner AS
(   SELECT * FROM metcap.site_descriptor$portal_banner);


-- pg_dump -U psql_dba -h localhost -b  -c -n _tmp_ vista88 > tmp_metcap_export.sql
