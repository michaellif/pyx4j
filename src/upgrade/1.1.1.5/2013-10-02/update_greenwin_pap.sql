/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Greenwin and larlyn preauthorized payment update
***
***     ======================================================================================================================
**/

BEGIN TRANSACTION;

        UPDATE  greenwin.preauthorized_payment AS p
        SET     effective_from = '01-OCT-2013'
        FROM    (SELECT         p.id
                FROM            greenwin.preauthorized_payment p 
                JOIN            greenwin.lease_participant lp ON (p.tenant = lp.id)
                JOIN            greenwin.lease l ON (lp.lease = l.id)
                JOIN            greenwin.apt_unit a ON (l.unit = a.id)
                JOIN            greenwin.building b ON (a.building = b.id)
                WHERE           b.property_code IN ('albe0383','albe0457','belm0545',
                                'belm0547','belm0565','conf0104','erb0285','oldc0100',
                                'oldc0120','oldc0170','park0400','shak0200','univ0137',
                                'west0093','west0109')) AS t
        WHERE   p.id = t.id;
        
-- COMMIT;


BEGIN TRANSACTION;

        UPDATE  larlyn.preauthorized_payment AS p
        SET     effective_from = '01-OCT-2013'
        FROM    (SELECT         p.id
                FROM            larlyn.preauthorized_payment p 
                JOIN            larlyn.lease_participant lp ON (p.tenant = lp.id)
                JOIN            larlyn.lease l ON (lp.lease = l.id)
                JOIN            larlyn.apt_unit a ON (l.unit = a.id)
                JOIN            larlyn.building b ON (a.building = b.id)
                WHERE           b.property_code IN ('15th2014','23rd1304','23rd3210',
                                '32nd2201','4th1115','5th0407','berk0037','cent1219',
                                'colu0175','colu0590','esqu0804','esqu0841','gov1030b',
                                'gove0681','gove1030','harw1100','mayo0256','scen1603')) AS t
        WHERE   p.id = t.id;
       
       
-- COMMIT;
