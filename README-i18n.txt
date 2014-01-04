# @version $Revision$ ($Author$) $Date$
========= Build commands =========

    * mvn -P i18n
        Extract text catalogs

    * mvn -P i18n,i18n-merge
        Extract text catalogs and create translations (ru and fr, ...) using Translation Catalog in vista-assembly\vista-assembly-i18n\src\main\resources\translations

        use:
          mvn package -P i18n,i18n-merge  -Dmaven.test.skip=true

    * mvn -P i18n,i18n-auto
        Extract text catalogs and create automatic translations (ru and fr) using Google translate and vista-assembly\vista-assembly-i18n\src\main\resources\translations

    * mvn -P i18n,i18n-auto,i18n-ru
        Only auto translate "ru"

    * mvn -P i18n,i18n-auto-all
        Using Google translate update Vista calog vista-assembly\vista-assembly-i18n\src\main\resources\translations

    * mvn -P i18n,i18n-auto,i18n-auto-all
	    Generate .po for "ru", "fr" and other while updating Translation Catalog using Google translate

    * mvn -P i18n,i18n-auto,i18n-auto-all,i18n-ru
	    Generate .po for "ru" while updating Translation Catalog using Google translate

