1.
  MySQL server configuration to execute tests in this module.

  MySQL server at localhost:3306
  DB Name: tst_entity
  User name: tst_entity password: tst_entity

   See scripts\mysql\mysql-init.sql  to create user and DB

2.
  MySQL Tests in this module are disabled by default in maven build.  Maven profile 'pyx4j-mysql-tests' required for them to execute in maven.
  e.g. mvn -P pyx4j-mysql-tests

3. There are 'ZZEnvCleanerTest' that will remove all created data during tests so the test DB will not grow overtime.

4. TODO
  Define how to test Oracle, Idea with maven profile: pyx4j-mysql-tests, pyx4j-oracle-tests, pyx4j-oracle-mysql-tests