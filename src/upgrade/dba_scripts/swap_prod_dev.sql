/**
***	=======================================================================
***	@version $Revision$ ($Author$) $Date$
***
***		Renames vista_prod <> vista
***
***	=======================================================================
**/

ALTER DATABASE vista_prod RENAME TO vista_prod1;
ALTER DATABASE vista RENAME TO vista_prod;
ALTER DATABASE vista_prod1 RENAME TO vista;


