CREATE TABLE _dba_.tmp_emails AS 
(
    SELECT  order_in_policy,subject,
            use_header,use_footer,
            content, template_type
    FROM    vista.email_template
    WHERE   template_type IN ('PaymentReturned','DirectDebitAccountChanged')
);

-- pg_dump -U psql_dba -h localhost -O -t _dba_.tmp_emails vista_prod > insert_tmp_emails.sql
