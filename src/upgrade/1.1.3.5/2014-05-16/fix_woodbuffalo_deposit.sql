/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Fix woodbuffaloproperties invoice_line_item
***
***     ======================================================================================================================
**/

BEGIN TRANSACTION;

    UPDATE  woodbuffaloproperties.billing_invoice_line_item 
    SET     ar_code = 7197 
    WHERE   id = 10988911;
    
COMMIT;
