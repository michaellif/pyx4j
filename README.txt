========= Install Eclipse =========

1. Get: Eclipse SDK
       eclipse-SDK-3.5.2-win32.zip

2. Unzip it to directory: eclipse-3.5.2   (Optionally)

3. Create Shortcut, provide path to JDK (for example -vm "C:\My\Programs\Java\jdk1.6.0_15\bin\javaw.exe")
   Or full Target on Windows 32:
        D:\prog\dev\eclipse\eclipse-3.5.2\eclipse.exe -vm D:/jdk1.6.0/bin/javaw.exe -vmargs -Xmx456M

4. Install Eclipse Plugins.
    - Subclipse
    - Google plugins for Eclipse
    - Google Web Toolkin SDK
    - Google App Engine SDK
    - M2-Eclipse **

    All Eclipse Plugins required by our development team are stored in our repository.
    Our Eclipse Update site is: http://pyx4j.com/eclipse/svn/trunk/3.5/
    The stable update site url for this project as of March 2010 is http://pyx4j.com/eclipse/svn/tags/2010-03/

    ** There is a copy of M2-Eclipse as aditional site
      http://pyx4j.com/eclipse/svn/trunk/3.5/m2e  or http://pyx4j.com/eclipse/svn/tags/2010-03/m2e

========= Install Subclipse =========

     Subclipse for SVN 1.6
     Official Update Site:  http://subclipse.tigris.org/update_1.6.x

========= Install M2-Eclipse =========

    Use the latest plugin http://m2eclipse.sonatype.org/
    Official Update Site:  http://m2eclipse.sonatype.org/sites/m2e

========= maven build =========

for cmd line maven build add OS env variable (this values are for 32bit os)
MAVEN_OPTS=-Xmx256M -XX:MaxPermSize=256m -Xss1024k

* Maven build with "GWT" modules compilations to JavaScript

  ** tester-client:
        mvn -P gwtct

  ** examples-*:
        mvn -P gwtce

  all GWT modules if you are in hurry (20% faster):
        mvn -P gwtce,draft

  on Core 2 Duo CPU  you can run build 25% faster
       mvn -P gwtce -DgwtLocalWorkers=2
    Change  maven settings.xml to have <properties><gwtLocalWorkers>2</gwtLocalWorkers></properties> in default profile to make it permanent.


* Before deployment to appengine run:

        mvn -P prod

========= Install Google App Engine SDK for Java =========

1. Install "Google plugins for Eclipse 3.5 version 1.2.0"
    site url: http://pyx4j.com/internal/eclipse/eclipse3.5-google-gpe-1.2.0/
    Do not install default latest one from google site:  http://dl.google.com/eclipse/plugin/3.5

    You may install SDKs bundle for GAE and GWT from google site for faster download.

-- (Optionally) Patch the development mode SDK to avoid maven build after each code change in dependencies.
2. Download and unzip appengine-java-sdk-1.3.1.zip
		from http://code.google.com/p/googleappengine/downloads/detail?name=appengine-java-sdk-1.3.1.zip&can=2&q=
3. Get from SVN https://pyx4j.com/sec/svn_internal/apps/incubator/gae-dev-classpath-hack and build it in Eclipse.
   If you don't have access to Private SVN get the classes from here: http://pyx4j.com/downloads/appengine-local-runtime.jar-1.3.1-patch.zip
4. Copy/Add results of the build (in target\classes) to appengine-local-runtime.jar located in App Engine SDK.

========= Eclipse Configuration for a new Workspace =========

1. Change Eclipse config
      Window->Preferences  Java\Compiler  Set: 1.6

 For mvn -P deploy-gae (mvn gae:deploy) to work add appengine.google.com-pyx to ./m2/settings.xml
    <settings>
        <servers>
            ....
            <server>
                <id>appengine.google.com-pyx</id>
                <username>MyEmail@gmail.com</username>
                <password>MyPassword</password>
            </server>

        </servers>

        <profiles>
            ....
        </profiles>
    </settings>

Cleaning up Indexes in Google App Engine/Java
  Use Python  SDK
    D:\etc\3p-libs\gae\appengine-python-1.3.1/appcfg.py vacuum_indexes D:\devGwt\pyx4j\incubator\tester\tester-gae-server
    D:\etc\3p-libs\gae\appengine-python-1.3.1/appcfg.py vacuum_indexes D:\devGwt\pyx4j\examples\examples-gae-server

rollback from pyx2 server

    cd /data/build/work/pyx/incubator/tester/tester-gae-server/
    mvn gae:rollback
  or
    cd /data/build/work/pyx/examples/examples-gae-server/war
    mvn gae:rollback

    /data/tools/3p-libs/gae/appengine-java-sdk-1.3.1/bin/appcfg.sh --email=vlads@myeasyforce.com rollback /data/build/work/pyx/incubator/tester/tester-gae-server/war
    /data/tools/3p-libs/gae/appengine-java-sdk-1.3.1/bin/appcfg.sh --email=vlads@myeasyforce.com rollback /data/build/work/pyx/examples/examples-gae-server/war