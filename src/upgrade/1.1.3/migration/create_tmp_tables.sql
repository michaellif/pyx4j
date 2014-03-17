CREATE TABLE _dba_.email_template AS
(SELECT     order_in_policy,subject,
            use_header,use_footer,
            content,template_type 
 FROM       vista.email_template);

CREATE TABLE _dba_.legal_terms_policy_item AS 
(SELECT     caption,enabled,content 
 FROM       vista.legal_terms_policy_item);
 
CREATE TABLE _dba_.lease_agreement_legal_term AS 
(SELECT     title,body,signature_format,order_id 
 FROM       vista.lease_agreement_legal_term);
 
CREATE TABLE _dba_.lease_application_legal_term AS 
(SELECT     title,body,signature_format,apply_to_role,order_id 
 FROM vista.lease_application_legal_term);
 
#pg_dump -U psql_dba -h localhost -O -t _dba_.email_template -t _dba_.legal_terms_policy_item -t _dba_.lease_agreement_legal_term -t _dba_.lease_application_legal_term vista_trunk > insert_tmp_policies.sql
