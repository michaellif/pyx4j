/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Function to obfuscate prod data
***		
***		Running any of these on production will result in a MAJOR DISASTER!
***
***     ======================================================================================================================
**/


CREATE OR REPLACE FUNCTION _dba_.random_string(v_source TEXT) RETURNS TEXT
AS
$$
DECLARE
	v_chars TEXT[] := '{a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z}';
	v_result TEXT := substring(v_source,1,1);
	
BEGIN
	IF (v_source IS NULL OR v_source = '')
	THEN
		v_result := NULL;
	ELSE	
				
		FOR v_count IN 1..(length(v_source) - 1)
		LOOP
			v_result := v_result||v_chars[1+round(random()*25)];
		END LOOP;
	END IF;

	RETURN(v_result);
END;
$$
LANGUAGE plpgsql VOLATILE;

/**
***
***	Returns pseudo-random date - date supplied minus 30 to 90 days
***
**/
CREATE OR REPLACE FUNCTION _dba_.random_date(DATE) RETURNS DATE
AS
$$
	SELECT CASE WHEN $1 IS NULL THEN NULL
	ELSE ($1 - (30 + round(random()*60)::integer))::date END AS random_date;
$$
LANGUAGE SQL VOLATILE;

/**
***	Returns random number as string
**/

CREATE OR REPLACE FUNCTION _dba_.random_number(v_source TEXT) RETURNS TEXT
AS
$$
DECLARE
	v_result	TEXT := '';
	v_count		INT;
	v_curr_char	CHAR(1);
BEGIN
	IF v_source IS NULL 
	THEN	
		RETURN(NULL);
	END IF;
		
	FOR v_count IN 1..LENGTH(v_source)
	LOOP
		v_curr_char = substring(v_source,v_count,1);	
		
		IF (v_curr_char ~ '[0-9]') 
		THEN		
			v_result := v_result || round(v_curr_char::integer*random());
		ELSE
			-- If || us used spaces are supressed, which is REALLY weird			
			v_result := concat(v_result,v_curr_char);
		END IF;
		
	END LOOP;

	RETURN(v_result);
END;	
$$
LANGUAGE plpgsql VOLATILE;


CREATE OR REPLACE FUNCTION _dba_.obfuscate_prod_data() RETURNS VOID AS
$$
DECLARE
        v_schema_name   VARCHAR(64);
BEGIN
        
        UPDATE  _admin_.admin_pmc_merchant_account_index
        SET     merchant_terminal_id = LPAD(id::text,8,'X')
        WHERE   merchant_terminal_id IS NOT NULL; 
        
        UPDATE  _admin_.pad_batch
        SET     account_number = LPAD(id::text,12,'0');
        
        UPDATE  _admin_.pad_debit_record
        SET     account_number = LPAD(id::text,12,'0');
        
        FOR v_schema_name IN 
        SELECT  namespace 
        FROM    _admin_.admin_pmc
        WHERE   status != 'Created'
        LOOP
        
                EXECUTE 'UPDATE '||v_schema_name||'.payment_payment_details '
                        ||'SET account_no_number = regexp_replace(account_no_obfuscated_number,''X'',''0'',''g'') ';
                        
                EXECUTE 'UPDATE '||v_schema_name||'.merchant_account m '
                        ||'SET  account_number = LPAD(m.id::text,12,''0''),'
                        ||'     merchant_terminal_id = a.merchant_terminal_id '
                        ||'FROM _admin_.admin_pmc_merchant_account_index a '
                        ||'WHERE m.id = a.merchant_account_key ';
                          
                        
        END LOOP;
                
                        
END;
$$
LANGUAGE plpgsql VOLATILE;

BEGIN TRANSACTION;

        -- disable scheduled triggers
        
        UPDATE  _admin_.scheduler_trigger
        SET     schedule_suspended = true;
        
        -- remove email notification for Yuriy
        
        DELETE  FROM _admin_.scheduler_trigger_notification
        WHERE   usr = 6;
        
        -- set yardi to test env
        
        UPDATE  _admin_.admin_pmc_yardi_credential
        SET     service_urlbase = 'http://yardi.birchwoodsoftwaregroup.com/Voyager60/',
                resident_transactions_service_url = NULL,
                sys_batch_service_url = NULL,
                maintenance_requests_service_url = NULL;
                
COMMIT;

SELECT * FROM _dba_.obfuscate_prod_data();

