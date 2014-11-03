/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***
***     metcap site update
***
***     ===========================================================================================================
**/        


BEGIN TRANSACTION;

    UPDATE  metcap.site_descriptor
    SET     skin = 'skin1',
            enabled = TRUE;
    
    UPDATE  metcap.site_palette
    SET     object1 = 207,
            object2 = 123,
            contrast1 = 123,
            contrast2 = 123,
            contrast3 = 123,
            contrast4 = 123,
            contrast5 = 123,
            contrast6 = 123,
            foreground = 220,
            form_background = 270,
            site_background = 207;
            
COMMIT;
