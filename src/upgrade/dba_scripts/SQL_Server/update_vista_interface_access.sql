/**
***     ======================================================================================================================
***
***              @version $Revision$ ($Author$) $Date$
***
***             Update vista interface access 
***
***     =======================================================================================================================
**/

BEGIN TRANSACTION;

WITH t AS (
SELECT hprop,68 as hinterfaceconfig,itype
FROM sl_0722.dbo.InterfaceLogin 
WHERE sInterface = 'Property Vista'
EXCEPT 
SELECT hprop,68 as hinterfaceconfig,itype
FROM sl_0422.dbo.InterfaceLogin 
WHERE sInterface = 'Property Vista')
INSERT INTO sl_0422.dbo.InterfaceLogin (hprop,hinterfaceconfig,itype,sinterface)
(SELECT t.hprop,t.hinterfaceconfig,t.itype,'Property Vista' 
FROM t 
JOIN sl_0422.dbo.property p ON (p.hmy = t.hprop)
WHERE p.scode NOT LIKE 'x%')

COMMIT;

SELECT * FROM sl_0422.dbo.interfacelogin WHERE sInterface = 'Property Vista';