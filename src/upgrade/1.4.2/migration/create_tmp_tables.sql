CREATE TABLE _dba_.tmp_terms AS 
(
    SELECT  t.target,
            MAX(v.version_number) AS max_version, 
            v.holder,d.content,v.caption 
    FROM    _admin_.vista_terms t
    JOIN    _admin_.vista_terms_v v ON (t.id = v.holder)
    JOIN    _admin_.vista_terms_v$document vd ON (v.id = vd.owner)
    JOIN    _admin_.legal_document d ON (vd.value = d.id)
    WHERE   t.target IN ('TenantBillingTerms','TenantPreAuthorizedPaymentECheckTerms',
            'TenantPreAuthorizedPaymentCardTerms')
    AND     d.locale = 'en' 
    AND     v.caption IS NOT NULL
    GROUP BY t.target, v.holder,d.content,v.caption
);

-- pg_dump -U psql_dba -h localhost -O -t _dba_.tmp_terms vista_trunk > insert_tmp_terms.sql
    
