# @version $Revision$ ($Author$) $Date$

1.  Each pom have magic string

    <version>1.1.0-SNAPSHOT</version><!--vista-version-->

    The spaces are important!
    If some pace is added the replacement during branch creation will fail and old version would be used.

2.  Each new vista project need to have .settings folder added!
    It is ignored by default, you need to explicitly add it to svn.

    best paractive to copy the .settings from another project