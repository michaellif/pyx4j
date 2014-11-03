/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***
***     metcap site export
***
***     ===========================================================================================================
**/        

CREATE SCHEMA _tmp_ OWNER vista;

CREATE TABLE _tmp_.media_file_blob AS 
(   SELECT * FROM metcap.media_file_blob) ;

CREATE TABLE _tmp_.site_image_resource AS 
(   SELECT * FROM metcap.site_image_resource) ;

CREATE TABLE _tmp_.portal_banner_image AS
(   SELECT * FROM metcap.portal_banner_image) ;
