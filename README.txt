# @version $Revision$ ($Author$) $Date$
========= Environment setup =========

1. Folow setup instruction http://jira.birchwoodsoftwaregroup.com/wiki/display/VISTA/Vista+Development+Environment+Setup


maven profiles used in build:

    * pyx
        Build together with pyx framework

    * full
        PreCompile jasperreports reports
        Generate IEnity implemenations on server

    * gwtc
        Compile Main GWT modules to Javascript.

        Additional profile to use with 'gwtc'

        * draft  (default)
            Do GWT compilation in 'draft' mode for "safari,gecko1_8,ie9" only and single locale (runs faster)

            N.B. to compile for all locale use

               mvn -P gwtc -P !draft -P !developer-env
             or
               bg-prod.cmd

        * soyc
            Generate soyc reports for created GWT modules

    * gwtct
        Compile Test GWT modules to Javascript.

    * i18n
        Extract text catalogs

    * i18n,i18n-merge
        Extract text catalogs and create translations (ru and fr) using Translation Catalog in vista-i18n-catalog\src\main\resources\translations

        use:
          mvn package -P i18n,i18n-merge  -Dmaven.test.skip=true

    * i18n,i18n-auto
        Extract text catalogs and create automatic translations (ru and fr) using Google translate

    * i18n,i18n-auto,i18n-auto-all
	    Generate .po for ru and fr and update Translation Catalog using Google translate

    * caledon-tests
        Enable caledon tests

    * selenium
        Enable selenium tests execution

maven profiles for build server

    * build-full
    * build-ci (for cruisecontrol)
    * build-selenium
    * deploy (deploy to tomcat)
    * deploy-target-www11
    * deploy-target-www22
    * deploy-target-www33
    * deploy-target-www44
