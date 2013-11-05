# @version $Revision$ ($Author$) $Date$

* When creating Branch
   update all pom.xml
    <version>x+1</version><!--pyx-version-->
  and
    <version>x+1</version><!--vista-version-->

   e.g.  mvn versions:set -DgenerateBackupPoms=false

* When creating Branch change VistaTODO.codeBaseIsProdBranch=true in Branch to support dual development environments

*
  To "patch.number" be changed (set) for every next production deployment build in Branch
  file vista-server\src\main\resources-generated\generated\build.version.properties contains fixed patch.number


