-- @version $Revision$ ($Author$) $Date$

CREATE USER tst_entity BY tst_entity;
GRANT CONNECT TO tst_entity;
GRANT RESOURCE TO tst_entity;


ALTER PROFILE DEFAULT LIMIT PASSWORD_LIFE_TIME UNLIMITED;
ALTER PROFILE DEFAULT LIMIT FAILED_LOGIN_ATTEMPTS  UNLIMITED;