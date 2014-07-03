/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             buildings renamed 
***
***     =====================================================================================================================
**/

-- cogir 

BEGIN TRANSACTION;

    UPDATE  cogir.building 
    SET     property_code = 'colb0002'
    WHERE   property_code = 'colb002r';
    
    UPDATE  cogir.building 
    SET     property_code = 'west0025'
    WHERE   property_code = 'west025r';
    
    UPDATE  cogir.building 
    SET     property_code = 'wate0840'
    WHERE   property_code = 'wate840r';
    
    UPDATE  cogir.building 
    SET     property_code = 'ride0100'
    WHERE   property_code = 'ride100r';
    
    UPDATE  cogir.building 
    SET     property_code = 'rich0033'
    WHERE   property_code = 'rich033r';
    
COMMIT;

-- dms

BEGIN TRANSACTION;

    UPDATE  dms.building 
    SET     property_code = 'mark0155'
    WHERE   property_code = 'mark155r';
    
COMMIT;

-- greenwin

BEGIN TRANSACTION;

    UPDATE  greenwin.building 
    SET     property_code = 'chan0286'
    WHERE   property_code = 'chan286r';
    
    UPDATE  greenwin.building 
    SET     property_code = 'chan0294'
    WHERE   property_code = 'chan294r';
    
    UPDATE  greenwin.building 
    SET     property_code = 'west0093'
    WHERE   property_code = 'west093r';
    
    UPDATE  greenwin.building 
    SET     property_code = 'erb0285'
    WHERE   property_code = 'erb285r';
    
    UPDATE  greenwin.building 
    SET     property_code = 'conn0135'
    WHERE   property_code = 'conn135r';
    
    UPDATE  greenwin.building 
    SET     property_code = 'rose0001'
    WHERE   property_code = 'rose001r';
    
    
COMMIT;

-- larlyn

BEGIN TRANSACTION;

    UPDATE  larlyn.building 
    SET     property_code = '15th2014'
    WHERE   property_code = '15t2014r';
    
    UPDATE  larlyn.building 
    SET     property_code = '23rd1304'
    WHERE   property_code = '23r1304r';
    
    UPDATE  larlyn.building 
    SET     property_code = '23rd3210'
    WHERE   property_code = '23r3210r';
    
    UPDATE  larlyn.building 
    SET     property_code = '32nd2201'
    WHERE   property_code = '32n2201r';
    
    UPDATE  larlyn.building 
    SET     property_code = '44th0915'
    WHERE   property_code = '44th915r';
    
    UPDATE  larlyn.building 
    SET     property_code = 'berk0037'
    WHERE   property_code = 'berk037r';
    
    UPDATE  larlyn.building 
    SET     property_code = 'cent1219'
    WHERE   property_code = 'cen1219r';
    
    UPDATE  larlyn.building 
    SET     property_code = 'colu0175'
    WHERE   property_code = 'colu175r';
    
    UPDATE  larlyn.building 
    SET     property_code = 'colu0590'
    WHERE   property_code = 'colu590r';
    
    UPDATE  larlyn.building 
    SET     property_code = 'manh0711'
    WHERE   property_code = 'manh711r';
    
    UPDATE  larlyn.building 
    SET     property_code = 'mayo0256'
    WHERE   property_code = 'mayo256r';
    
    UPDATE  larlyn.building 
    SET     property_code = 'scen1603'
    WHERE   property_code = 'sce1603r';

    
COMMIT;

-- metcap

BEGIN TRANSACTION;

    UPDATE  metcap.building 
    SET     property_code = 'kipp0740'
    WHERE   property_code = 'kipp740r';
    
    UPDATE  metcap.building 
    SET     property_code = 'cart0010'
    WHERE   property_code = 'cart010r';
    
    UPDATE  metcap.building 
    SET     property_code = 'ragl0036'
    WHERE   property_code = 'ragl036r';


    
COMMIT;

-- sterling

BEGIN TRANSACTION;

    UPDATE  sterling.building 
    SET     property_code = 'west2292'
    WHERE   property_code = 'wes2292r';
    
    UPDATE  sterling.building 
    SET     property_code = 'maxw0131'
    WHERE   property_code = 'maxw131r';
    
COMMIT;
    
    




