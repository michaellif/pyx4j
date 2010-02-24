========= Install Eclipse =========

1. Get: Eclipse SDK
       eclipse-SDK-3.5.1-win32.zip

2. Unzip it to directory: eclipse-3.5.1

========= Install Subclipse =========

    http://subclipse.tigris.org/update_1.6.x for SVN 1.6

========= Install M2-Eclipse =========

    http://m2eclipse.sonatype.org/sites/m2e

========= maven build =========

for cmd line maven build add OS env variable (this values are for 32bit os)
MAVEN_OPTS=-Xmx256M -XX:MaxPermSize=256m -Xss1024k

* Maven build with "GWT" modules compilations to JavaScript

  ** tester-client:
        mvn -P gwtct

  ** examples-*:
        mvn -P gwtce

* Before deployment to appengine run:

        mvn -P prod

========= Install Google App Engine SDK for Java =========

1. Install default plugin - http://dl.google.com/eclipse/plugin/3.5
2. Download and unzip appengine-java-sdk-1.3.0.zip
		from http://code.google.com/appengine/downloads.html#Google_App_Engine_SDK_for_Java
3. Get from SVN https://pyx4j.com/sec/svn_internal/apps/incubator/gae-dev-classpath-hack and build it in Eclipse.
4. Copy results of the build (in target\classes) to appengine-local-runtime.jar located in App Engine SDK.

========= Eclipse Configuration for a new Workspace =========

1. Change Eclipse config
      Window->Preferences  Java\Compiler  Set: 1.6


Cleaning up Indexes in Google App Engine/Java
  Use Python  SDK
    D:\etc\3p-libs\gae\appengine-python-1.3.1/appcfg.py vacuum_indexes D:\devGwt\pyx4j\incubator\tester\tester-gae-server
    D:\etc\3p-libs\gae\appengine-python-1.3.1/appcfg.py vacuum_indexes D:\devGwt\pyx4j\examples\examples-gae-server

rollback from pyx2 server (outside the firwall!):

    /data/tools/3p-libs/gae/appengine-java-sdk-1.3.1/bin/appcfg.sh --email=vlads@myeasyforce.com rollback /data/build/work/pyx/incubator/tester/tester-gae-server/war
    /data/tools/3p-libs/gae/appengine-java-sdk-1.3.1/bin/appcfg.sh --email=vlads@myeasyforce.com rollback /data/build/work/pyx/examples/examples-gae-server/war