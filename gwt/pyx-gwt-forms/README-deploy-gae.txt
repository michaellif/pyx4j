
1.
  Compile GWT in Eclipse

2.
  mvn -P deploy-test-gae  gae:deploy

It will be uploaded to http://30.latest.pyx4j-tester.appspot.com/

For this to work add appengine.google.com-pyx to ./m2/settings.xml
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


Set CORP_TOOLS=C:  (it asumes that you installed GAE SDK to C:\3p-libs\gae\)