# @version $Revision$ ($Author$) $Date$
========= Environment setup =========

1. Folow setup instruction http://propertyvista.jira.com/wiki/display/VISTA/Vista+Development+Environment+Setup


maven profiles used in build:

    * pyx
        Build together with pyx framework

    * full
        PreCompile jasperreports reports
        Generate IEnity implemenations on server

    * gwtc
        Compile Main GWT modules to Javascript.

        Additional profile to use with 'gwtc'

        * draft
            Do GWT compilation in 'draft' mode (runs faster)

        * soyc
            Generate soyc reports for created GWT modules

    * gwtct
        Compile Test GWT modules to Javascript.

    * caledon-tests
        Enable caledon tests

    * selenium
        Enable selenium tests execution

maven profiles for build server

    * build-env
    * build-ci (for cruisecontrol)
    * deploy (deploy to tomcat)
    * deploy-target-www33
    * deploy-target-www44
