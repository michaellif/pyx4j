/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Per-building statistics functions and views
***
***     ======================================================================================================================
**/


CREATE OR REPLACE FUNCTION _dba_.get_building_units(    v_schema_name   TEXT, 
                                                        v_property_code TEXT, 
                                                        OUT total_units INT,
                                                        OUT active_leases INT,
                                                        OUT avg_tpu NUMERIC(4,1),
                                                        OUT reg_units      INT,
                                                        OUT pct_reg_units NUMERIC(4,1),
                                                        OUT total_tenants  INT,
                                                        OUT reg_tenants    INT,
                                                        OUT pct_reg_tenants NUMERIC(4,1))
AS
$$      
BEGIN
      
        EXECUTE   'SELECT COUNT(a.id)::int '
                ||'FROM '||v_schema_name||'.apt_unit a '
                ||'JOIN '||v_schema_name||'.building b ON (b.id = a.building) '
                ||'WHERE b.property_code = '''||v_property_code||''' '
                INTO total_units;
                
        EXECUTE 'SELECT COUNT(l.id) '
                ||'FROM '||v_schema_name||'.lease l '
                ||'JOIN '||v_schema_name||'.apt_unit a ON (l.unit = a.id) '
                ||'JOIN '||v_schema_name||'.building b ON (a.building = b.id) '
                ||'WHERE l.status = ''Active'' '
                ||'AND b.property_code = '''||v_property_code||''' '
                INTO active_leases;
        
        EXECUTE 'SELECT AVG(tpu) '
                ||'FROM         (SELECT l.unit,COUNT(p.id) AS tpu '
                ||'             FROM '||v_schema_name||'.lease l '
                ||'             JOIN '||v_schema_name||'.lease_participant p ON (p.lease = l.id) '
                ||'             JOIN '||v_schema_name||'.apt_unit a ON (l.unit = a.id) '
                ||'             JOIN '||v_schema_name||'.building b ON (a.building = b.id) '
                ||'             WHERE l.status = ''Active'' '
                ||'             AND  b.property_code = '''||v_property_code||''' '
                ||'             GROUP BY l.unit) AS t '
                INTO avg_tpu;
                
                
        EXECUTE   'SELECT COUNT(a.id)::int '
                ||'FROM '||v_schema_name||'.apt_unit a '
                ||'JOIN '||v_schema_name||'.building b ON (b.id = a.building) '
                ||'JOIN         (SELECT DISTINCT unit '
                ||              'FROM '||v_schema_name||'.lease l '
                ||              'JOIN '||v_schema_name||'.lease_participant p ON (p.lease = l.id) '
                ||              'JOIN '||v_schema_name||'.customer c ON (p.customer = c.id) '
                ||              'WHERE  l.status = ''Active'' '
                ||              'AND    c.registered_in_portal ) AS c ON (c.unit = a.id) '
                ||'WHERE b.property_code = '''||v_property_code||''' '
                INTO reg_units;
 
 
        EXECUTE 'SELECT COUNT(p.id) '
                ||'FROM '||v_schema_name||'.lease_participant p '
                ||'JOIN '||v_schema_name||'.lease l ON (l.id = p.lease) '
                ||'JOIN '||v_schema_name||'.apt_unit a ON (a.id = l.unit) '
                ||'JOIN '||v_schema_name||'.building b ON (a.building = b.id) '
                ||'WHERE b.property_code = '''||v_property_code||''' '
                ||'AND  l.status = ''Active'' '
                INTO total_tenants;
                
        EXECUTE 'SELECT COUNT(p.id) '
                ||'FROM '||v_schema_name||'.lease_participant p '
                ||'JOIN '||v_schema_name||'.lease l ON (l.id = p.lease) '
                ||'JOIN '||v_schema_name||'.apt_unit a ON (a.id = l.unit) '
                ||'JOIN '||v_schema_name||'.building b ON (a.building = b.id) '
                ||'JOIN '||v_schema_name||'.customer c ON (p.customer = c.id) '
                ||'WHERE b.property_code = '''||v_property_code||''' '
                ||'AND  l.status = ''Active'' '
                ||'AND  c.registered_in_portal '
                INTO reg_tenants;
                
        pct_reg_units := ROUND(reg_units*100/total_units,1);
        pct_reg_tenants := ROUND(reg_tenants*100/total_tenants,1);
END;
$$
LANGUAGE plpgsql VOLATILE;
 
                
                  
        
