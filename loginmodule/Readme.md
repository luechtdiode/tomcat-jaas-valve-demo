Setup:
======
First, get and unzip tomcat 8.5.

After mvn clean install, the loginmodule can be installed in tomcat/lib folder and the testservlet can be deployed in 
webapps.

libs to copy into tomcat/lib folder:
------------------------------------
```
loginmodule-<version>.jar (from target-folder)
jjwt-0.9.1
jaxb-api-2.3.1
jackson-databind-2.9.6
jackson-core-2.9.6
jackson-annotations-2.9.0
```

Modifications in jaas.config (tomcat/conf):
-------------------------------------------
```
MyAccess {
  ch.seidel.tomcattest.SampleLoginModule required debug=true;
};
```

Modifications in server.xml (tomcat/conf):
-------------------------------------------
```
<Realm className="org.apache.catalina.realm.JAASRealm" appName="MyAccess"
      userClassNames="ch.seidel.tomcattest.SampleUserPrincipal"
      roleClassNames="ch.seidel.tomcattest.SampleRolePrincipal"
      />

<Valve className="ch.seidel.tomcattest.SecurityValve"/>
```
