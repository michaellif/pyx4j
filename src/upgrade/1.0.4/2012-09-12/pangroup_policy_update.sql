/**
***	=========================================================================
***
***		Fix for PAN Group policies - older version did not include 
***		guarantor and customer in id_assignment_items
***
***	=========================================================================
**/

BEGIN TRANSACTION;

-- Since it is quite a trivial insert I will not bother with elegant SQL

INSERT INTO pangroup.id_assignment_item(id,policy,order_in_policy,target,tp)
VALUES (nextval('public.id_assignment_item_seq'),1,5,'guarantor','generatedNumber');

INSERT INTO pangroup.id_assignment_item(id,policy,order_in_policy,target,tp)
VALUES (nextval('public.id_assignment_item_seq'),1,6,'customer','generatedNumber');

COMMIT;

