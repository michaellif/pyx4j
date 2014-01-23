/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Rename metcap schema and remove all references
***
***     ======================================================================================================================
**/

BEGIN TRANSACTION;

        UPDATE  _admin_.audit_record
        SET     namespace = 'metcap_demo'
        WHERE   namespace = 'metcap';
        
        UPDATE  _admin_.admin_pmc
        SET     name = 'Metcap Demo',
                namespace = 'metcap_demo',
                dns_name = 'metcap-demo'
        WHERE   id = 211;
        
        ALTER SCHEMA metcap RENAME TO metcap_demo;
 
COMMIT;
