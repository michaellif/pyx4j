========= Install Eclipse =========

1. Get: Eclipse SDK  or  Eclipse IDE for Java EE Developers
       eclipse-SDK-3.6.1-win32.zip or eclipse-java-helios-SR1-win32.zip

2. Unzip it to directory: eclipse-3.6.1   (Optionally)

3. Create Shortcut, provide path to JDK (for example -vm "C:\My\Programs\Java\jdk1.6.0_20\bin\javaw.exe")
   Or full Target on Windows 32:
        D:\prog\dev\eclipse\eclipse-3.6.1\eclipse.exe -vm D:/jdk1.6.0/bin/javaw.exe -vmargs -Xmx456M

4. Install Eclipse Plugins.
    - Subclipse                  (From Eclipse Marketplace)
    - Google plugins for Eclipse (From Eclipse Marketplace)
    - Google Web Toolkin SDK     (From Eclipse Marketplace)
    - Google App Engine SDK      (From Eclipse Marketplace)
    - M2-Eclipse **

========= Install Subclipse =========

     Subclipse for SVN 1.6  From Eclipse Marketplace
     Official Update Site:  http://subclipse.tigris.org/update_1.6.x

========= Install M2-Eclipse =========

    Use the latest plugin http://m2eclipse.sonatype.org/
    Official Update Site: http://m2eclipse.sonatype.org/sites/m2e

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

1. Install "Google plugins for Eclipse 3.6 version 1.3.3"
    site url: http://dl.google.com/eclipse/plugin/3.6

    You may install SDKs bundle for GAE and GWT from google site for faster download.
    In client projects we don't use GAE SDK installed inside Eclipse.

-- (Optionally) Patch the development mode SDK to avoid maven build after each code change in dependencies.
2. Download and unzip appengine-java-sdk-1.4.0.zip
		from http://googleappengine.googlecode.com/files/appengine-java-sdk-1.4.0.zip
3. For project that store Eclipse **-server.launch in SVN.
   Unzip GAE SDK to C:\3p-libs\gae\appengine-java-sdk-1.4.0
4. For heavy server side development use jrebel

========= Eclipse Configuration for a new Workspace =========

1. Change Eclipse config
      Window->Preferences  Java\Compiler  Set: 1.6

2.  To Server web applications start add "Program argumets:"
   --disable_update_check
 or run
   pyx\src\make.appcfg_no_nag.cmd

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

    Plase enure that your passwords are encrypted! Follow this guide http://maven.apache.org/guides/mini/guide-encryption.html
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
