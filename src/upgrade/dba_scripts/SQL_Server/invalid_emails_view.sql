/**
***     ======================================================================================================================
***
***              @version $Revision$ ($Author$) $Date$
***
***             Procedure and view to locate invalid emails
***
***     =======================================================================================================================
**/

USE master;

Create FUNCTION ValidateEmail(@email varChar(255))
RETURNS bit
AS
BEGIN
RETURN
        (SELECT CASE    WHEN    (CHARINDEX(' ',@email) != 0  
                                OR CHARINDEX('/',@email) != 0
                                OR CHARINDEX(':',@email) != 0
                                OR CHARINDEX(',',@email) != 0
                                OR CHARINDEX(';',@email) != 0) THEN 0
                        WHEN    @email NOT LIKE '%@%.%' THEN 0
                        ELSE 1
        END;
        )
END;

USE sl_0823;

CREATE VIEW invalid_email AS
        (SELECT pl.SADDR1 AS property_list,
                p.scode AS property,
                t.SCODE AS lease_id,
                ISNULL(t.SFIRSTNAME, '') + ' ' + t.SLASTNAME AS tenant,
                t.SUNITCODE AS unit,
		t.SADDR1 AS address,
                t.SCITY AS city,
                t.SSTATE AS province,
		t.semail 
	FROM    TENANT t
	JOIN    PROPERTY p ON (t.HPROPERTY = p.HMY)
	JOIN    tenstatus ts ON (t.ISTATUS = ts.istatus)
	JOIN    LISTPROP lp ON (p.HMY = lp.HPROPERTY)
	JOIN    PROPERTY pl ON (lp.HPROPLIST = pl.HMY)
	WHERE   ts.status IN ('Current','Notice')
        AND     pl.SADDR1 LIKE '%Vista%'
        AND     ISNULL(t.semail,'' ) != ''
        AND     master.dbo.ValidateEmail(t.semail) != 1 );      
	       
