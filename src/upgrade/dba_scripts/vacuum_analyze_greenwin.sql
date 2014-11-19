/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             script to vacuum analyze greenwin table that prevent user
***             from login 
***             
***             Primarily to be used on qa environments after prod data copy
***
***     ======================================================================================================================
**/


VACUUM ANALYZE  greenwin.lease_participant;
VACUUM ANALYZE  greenwin.lease_term_participant;
VACUUM ANALYZE  greenwin.lease_term;
VACUUM ANALYZE  greenwin.lease_term_v;
VACUUM ANALYZE  greenwin.lease;
VACUUM ANALYZE  greenwin.apt_unit;
VACUUM ANALYZE  greenwin.building;
VACUUM ANALYZE  greenwin.customer;
