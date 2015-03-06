# @version $Revision$ ($Author$) $Date$

* When creating Branch
   update all pom.xml
    <version>x+1</version><!--pyx-version-->
  and
    <version>x+1</version><!--vista-version-->

   e.g.  mvn versions:set -DgenerateBackupPoms=false

* In created Branch change
    * change VistaTODO.codeBaseIsProdBranch=true in Branch to support dual development environments
    * rename the file in root production-branch.profile.off to production-branch.profile
    * set patch.number=0 in  vista-server\src\main\resources-generated\generated\build.version.properties
      change product.build to have patch.number e.g.  ".....${parsedVersion.incrementalVersion}.0.${bamboo.buildNumber}"
      **  The "patch.number" to be changed (set+1) for every next production deployment build in Branch

 merge branch back to master. but do not merge any files modified in created Branch so the next merge will not copy them.


