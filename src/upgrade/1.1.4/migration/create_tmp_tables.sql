CREATE TABLE _dba_.tmp_roles AS 
(   SELECT  r.name,b.value,
            r.require_security_question_for_password_reset
    FROM    vista.crm_role r 
    JOIN    vista.crm_role$behaviors b ON (r.id = b.owner)
    WHERE   name !~ 'Test' 
);

CREATE TABLE _dba_.tmp_categories AS
(   SELECT  category,
            category_type,
            ticket_type,
            deleted
    FROM    vista.communication_message_category
);


-- pg_dump -U psql_dba -h localhost -O -t _dba_.tmp_roles vista_trunk > insert_tmp_roles.sql
-- pg_dump -U psql_dba -h localhost -O -t _dba_.tmp_categories vista_trunk > insert_tmp_categories.sql


