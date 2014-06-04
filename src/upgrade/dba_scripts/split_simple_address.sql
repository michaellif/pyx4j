/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***
***     Function to split simpale address to semi-parsed 
***
***     ===========================================================================================================
**/                                                     

CREATE OR REPLACE FUNCTION _dba_.split_simple_address(  v_schema_name   TEXT,
                                                        v_table_name    TEXT,
                                                        v_addr_col1     TEXT,
                                                        v_addr_col2     TEXT)
RETURNS TABLE   (   address1            VARCHAR(500),
                    address2            VARCHAR(500),
                    suite_num           VARCHAR(500),
                    street_num          TEXT,
                    street_name         TEXT)
AS
$$
BEGIN
    
    FOR address1, address2 IN 
    EXECUTE 'SELECT '||v_addr_col1||','||v_addr_col2||' '
            ||'FROM '||v_schema_name||'.'||v_table_name||' '
            ||'WHERE    ('||v_addr_col1||' IS NOT NULL '
            ||'         OR '||v_addr_col2||' IS NOT NULL) '
    LOOP
        IF (address2 IS NOT NULL)
        THEN
            -- simple case, just unit number
            IF (address2 ~ '^[\d]+$')
            THEN
                suite_num := address2;
            -- unit number plus 'apt', 'unit', 'suite' etc
            ELSIF (address2 ~*  '^(AP(AR)?T(MENT)?|S(UI)?TE|UNIT)\.?\s?(#|NO)?\s?[\d]+\s?[A-Za-z]?$')
            THEN
                suite_num := UNNEST(regexp_matches(address2, '[\d]+\s?[A-Za-z]?$'));
            ELSE
                suite_num := address2;
            END IF;
        ELSE
            suite_num := NULL;
        END IF;
        
        IF (address1 IS NOT NULL)
        THEN
            -- match stuff like '39 Westgate Avenue'
            IF (address1 ~ '^[\d]+\s[A-Za-z\s\.]+$')
            THEN 
                street_num := UNNEST(regexp_matches(address1,'^[\d]+'));
                street_name := INITCAP(TRIM(regexp_replace(address1,'^[\d]+\s','')));
            -- '205-2400 Carling Ave' or so 
            ELSIF (address1 ~ '^[\d]+\s?-\s?[\d]+\s[A-Za-z\s\.]+$')
            THEN
                suite_num := UNNEST(regexp_matches(address1,'^[\d]+'));
                street_num := regexp_replace(regexp_replace(address1,'^[\d]+\s?-\s?',''),'\s?[A-Za-z\s\.]+$','');
                street_name := INITCAP(TRIM(regexp_replace(address1,'^[\d]+\s?-\s?[\d]+\s?','')));
            -- '178  Jarvis street Unit 1001'
            ELSIF (address1 ~* '^[\d]+\s[A-Za-z\s\.]+(\s)?(,\s)?(AP(AR)?T(MENT)?|S(UI)?TE|UNIT)\.?\s?(#|NO)?\s?[\d]+\s?[A-Za-z]?$')
            THEN
                suite_num := UNNEST(regexp_matches(address1,'[\d]+\s?[A-Za-z]?$'));
                street_num := UNNEST(regexp_matches(address1,'^[\d]+'));
                street_name := INITCAP(TRIM(regexp_replace(regexp_replace(address1,'^[\d]+\s?',''),
                        '(,\s)?(AP(AR)?T(MENT)?|S(UI)?TE|UNIT)\.?\s?(#|NO)?\s?[\d]+\s?[A-Za-z]?$','','i')));
            -- '430 Regional Road 24' or such
            ELSIF (address1 ~* '^[\d]+\s(RR|Regional Road)(\s+|-|#)\s?[\d]+$')
            THEN
                street_num := UNNEST(regexp_matches(address1,'^[\d]+'));
                street_name := INITCAP(TRIM(regexp_replace(address1,'^[\d]+','')));
            ELSE
                street_num := NULL;
                street_name := address1;
            END IF;
        ELSE
            street_num := NULL;
            street_name := NULL;
        END IF;
        
        RETURN NEXT;
        
    
    END LOOP;

END;
$$
LANGUAGE plpgsql VOLATILE;
