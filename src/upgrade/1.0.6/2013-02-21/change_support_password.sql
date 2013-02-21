/**     ===========================================================================
***     
***             support@propertyvista.com password update 
***
***     ==========================================================================
**/

BEGIN TRANSACTION;

SELECT  namespace,
        _dba_.update_crm_user_credential(namespace,'support@propertyvista.com','6JRWpQqeP2BXw8wixjYoRk1xI/FmbZYGrdq5gHrRIaNfLQTyvMjgqZQfv6ec4HBb')
FROM    _admin_.admin_pmc
WHERE   status != 'Created'
ORDER BY 1;

COMMIT;

