# @version $Revision$ ($Author$) $Date$

========= Install Eclipse ==========1

1. Get: Latest Eclipse SDK
      eclipse-standard-kepler-R-win32-x86_64.zip or eclipse-standard-kepler-R-win32.zip

2. Unzip it to directory: eclipse-4.3.0   (Optionally)

3. Create Shortcut, provide path to JDK (for example -vm "C:\My\Programs\Java\jdk1.7.0\bin\javaw.exe")
   Or full Target on Windows 32:
        D:\prog\dev\eclipse\eclipse-4.2.2\eclipse.exe -vm D:/jdk1.7.0/bin/javaw.exe -vmargs -Xmx456M -XX:MaxPermSize=256m

   On Windows 64 When using 64-bit JDK set:
        -Xmx1024M -XX:MaxPermSize=512m -XX:ReservedCodeCacheSize=128m

4. Install Eclipse Plugins.
    - Subclipse     (latest)               (From Eclipse Marketplace)
    - Google plugins for Eclipse v3.2.0    (From Eclipse Marketplace)
    - Google Web Toolkin SDK v2.5.0        (From Eclipse Marketplace)
    - Google App Engine SDK                (From Eclipse Marketplace)
    - M2-Eclipse ** (latest)

========= Install Subclipse =========

     Subclipse for SVN 1.7  From Eclipse Marketplace
     Official Update Site:  http://subclipse.tigris.org/update_1.8.x

========= Install M2-Eclipse =========

    Is part of Eclipse distribution, Select  Indigo repository and type "maven"

========= maven build =========

for cmd line maven build add OS env variable (this values are for 32bit os)
MAVEN_OPTS=-Xmx256M -XX:MaxPermSize=256m -Xss1024k -XX:ReservedCodeCacheSize=64m

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

1. Install "Google plugins for Eclipse 4.2 ..."
    site url: http://dl.google.com/eclipse/plugin/4.2

    You may install SDKs bundle for GAE and GWT from google site for faster download.
    In client projects we don't use GAE SDK installed inside Eclipse!

    For project that store Eclipse **-server.launch in SVN.
    Star this bugs: http://code.google.com/p/googleappengine/issues/detail?id=3401
                    http://code.google.com/p/googleappengine/issues/detail?id=2282

2. Download and unzip appengine-java-sdk-1.8.8.zip  http://googleappengine.googlecode.com/files/appengine-java-sdk-1.8.8.zip
		from http://code.google.com/appengine/downloads.html
3. Unzip GAE SDK to C:\3p-libs\gae\appengine-java-sdk-1.8.8  (or make NTFS link)
4. Configure Eclipse -> Preferences -> Google -> App Engine to use SDK above!

4. For heavy server side development use jrebel
   Star this bug: http://code.google.com/p/googleappengine/issues/detail?id=4122
   Install it to C:\3p-libs\jrebel

========= Eclipse Configuration for a new Workspace =========

1. Change Eclipse config
      Window->Preferences  Java\Compiler  Set: 1.6

      Set proper Java Code Style see http://code.pyx4j.com/dev-env.html


2.  To Server web applications start add "Program argumets:"
   --disable_update_check
 or run
   pyx\src\make.appcfg_no_nag.cmd

   Set CORP_TOOLS=C:  (it asumes that you installed GAE SDK to C:\3p-libs\gae\)

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

    Please ensure that your passwords are encrypted! Follow this guide http://maven.apache.org/guides/mini/guide-encryption.html
    On linux don't worget ro run history -c or export HISTSIZE=0; mvn --encrypt-password <password>


Cleaning up Indexes in Google App Engine/Java
  Use Python  SDK
    D:\etc\3p-libs\gae\appengine-python-1.3.6/appcfg.py vacuum_indexes D:\devGwt\pyx4j\incubator\tester\tester-gae-server
    D:\etc\3p-libs\gae\appengine-python-1.3.6/appcfg.py vacuum_indexes D:\devGwt\pyx4j\examples\examples-gae-server

rollback from pyx2 server

    cd /data/build/work/pyx/incubator/tester/tester-gae-server/
    mvn gae:rollback
  or
    cd /data/build/work/pyx/examples/examples-gae-server/war
    mvn gae:rollback
