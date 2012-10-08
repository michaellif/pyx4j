/**
***	=====================================================================
***	@version $Revision$ ($Author$) $Date$
***
***		Update production data
***
***	=====================================================================
**/

BEGIN TRANSACTION;

/**	_admin_ schema **/

SET search_path = '_admin_';


/** The following insert to be disabled in development environment **/
INSERT INTO admin_pmc_payment_type_info
(id,pmc,cc_visa_fee,cc_visa_payment_available,interac_visa_fee,interac_visa_payment_available,
cc_master_card_fee,cc_master_card_payment_available,interac_caledon_fee,interac_caledon_payment_available,
eft_fee,eft_payment_available,e_cheque_fee,e_check_payment_available) VALUES
(nextval('public.admin_pmc_payment_type_info_seq'),2,1.3,TRUE,0.6,TRUE,2.02,TRUE,1.3,TRUE,0.75,TRUE,0.5,TRUE),
(nextval('public.admin_pmc_payment_type_info_seq'),3,1.5,TRUE,0.75,TRUE,2.22,TRUE,NULL,FALSE,1.5,TRUE,1.5,TRUE),
(nextval('public.admin_pmc_payment_type_info_seq'),6,1.5,TRUE,0.75,TRUE,2.22,TRUE,NULL,FALSE,1.5,TRUE,1.5,TRUE),
(nextval('public.admin_pmc_payment_type_info_seq'),9,1.5,TRUE,0.75,TRUE,2.22,TRUE,NULL,FALSE,1.5,TRUE,1.5,TRUE),
(nextval('public.admin_pmc_payment_type_info_seq'),13,1.5,TRUE,0.75,TRUE,2.22,TRUE,NULL,FALSE,1.5,TRUE,1.5,TRUE);

-- manual commit ?
-- COMMIT;

