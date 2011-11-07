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
