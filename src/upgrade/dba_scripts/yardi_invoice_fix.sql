
CREATE OR REPLACE FUNCTION _dba_.yardi_invoice_fix (v_schema_name TEXT) RETURNS void AS
$$
BEGIN
        EXECUTE 'SET search_path = '||v_schema_name;
        
        EXECUTE 'ALTER TABLE  '||v_schema_name||'.billing_invoice_line_item DROP CONSTRAINT billing_invoice_line_item_adjustment_fk';
        EXECUTE 'ALTER TABLE  '||v_schema_name||'.billing_invoice_line_item DROP CONSTRAINT billing_invoice_line_item_ar_code_fk';
        EXECUTE 'ALTER TABLE  '||v_schema_name||'.billing_invoice_line_item DROP CONSTRAINT billing_invoice_line_item_billing_account_fk';
        EXECUTE 'ALTER TABLE  '||v_schema_name||'.billing_invoice_line_item DROP CONSTRAINT billing_invoice_line_item_billing_cycle_fk';
        EXECUTE 'ALTER TABLE  '||v_schema_name||'.billing_invoice_line_item DROP CONSTRAINT billing_invoice_line_item_deposit_fk';
        EXECUTE 'ALTER TABLE  '||v_schema_name||'.billing_invoice_line_item DROP CONSTRAINT billing_invoice_line_item_payment_record_fk';
        EXECUTE 'ALTER TABLE  '||v_schema_name||'.billing_invoice_line_item DROP CONSTRAINT billing_invoice_line_item_product_charge_fk';
        
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_invoice_line_item DROP CONSTRAINT billing_invoice_line_item_id_discriminator_ck';
        
        EXECUTE 'UPDATE '||v_schema_name||'.billing_invoice_line_item '
                ||'SET id_discriminator = ''YardiDebit'' '
                ||'WHERE id_discriminator = ''YardiCharge'' ';
                
       EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_invoice_line_item ADD CONSTRAINT billing_invoice_line_item_id_discriminator_ck CHECK (id_discriminator IN (''AccountCharge'',
        ''AccountCredit'',''CarryforwardCharge'',''CarryforwardCredit'',''Deposit'',''DepositRefund'',''LatePaymentFee'',''NSF'',''Payment'',''PaymentBackOut'',
        ''ProductCharge'',''ProductCredit'',''Withdrawal'',''YardiCredit'',''YardiDebit'',''YardiPayment'',''YardiReceipt'',''YardiReversal''))' ;
        
        
       -- DROP TABLE maintenance_request_status;
        DROP TABLE maintenance_request;
        
        
        EXECUTE 'ALTER TABLE  '||v_schema_name||'.billing_invoice_line_item ADD CONSTRAINT billing_invoice_line_item_adjustment_fk FOREIGN KEY (adjustment) REFERENCES '||v_schema_name||'.lease_adjustment(id) DEFERRABLE INITIALLY DEFERRED';
        EXECUTE 'ALTER TABLE  '||v_schema_name||'.billing_invoice_line_item ADD CONSTRAINT billing_invoice_line_item_ar_code_fk  FOREIGN KEY (ar_code) REFERENCES '||v_schema_name||'.arcode(id) DEFERRABLE INITIALLY DEFERRED';
        EXECUTE 'ALTER TABLE  '||v_schema_name||'.billing_invoice_line_item ADD CONSTRAINT billing_invoice_line_item_billing_account_fk FOREIGN KEY (billing_account) REFERENCES '||v_schema_name||'.billing_account(id) DEFERRABLE INITIALLY DEFERRED';
        EXECUTE 'ALTER TABLE  '||v_schema_name||'.billing_invoice_line_item ADD CONSTRAINT billing_invoice_line_item_billing_cycle_fk 
                FOREIGN KEY (billing_cycle) REFERENCES '||v_schema_name||'.billing_billing_cycle(id) DEFERRABLE INITIALLY DEFERRED';
        EXECUTE 'ALTER TABLE  '||v_schema_name||'.billing_invoice_line_item ADD CONSTRAINT billing_invoice_line_item_deposit_fk FOREIGN KEY (deposit) REFERENCES '||v_schema_name||'.deposit(id) DEFERRABLE INITIALLY DEFERRED';
        EXECUTE 'ALTER TABLE  '||v_schema_name||'.billing_invoice_line_item ADD CONSTRAINT billing_invoice_line_item_payment_record_fk FOREIGN KEY (payment_record) REFERENCES '||v_schema_name||'.payment_record(id) DEFERRABLE INITIALLY DEFERRED';
        EXECUTE 'ALTER TABLE  '||v_schema_name||'.billing_invoice_line_item ADD CONSTRAINT billing_invoice_line_item_product_charge_fk 
                FOREIGN KEY (product_charge) REFERENCES '||v_schema_name||'.billing_invoice_line_item(id) DEFERRABLE INITIALLY DEFERRED';
                

        
END;
$$
LANGUAGE plpgsql VOLATILE;

BEGIN TRANSACTION;

SELECT namespace,_dba_.yardi_invoice_fix(namespace)
FROM _admin_.admin_pmc 
WHERE status != 'Created'
ORDER BY 1;

COMMIT;


