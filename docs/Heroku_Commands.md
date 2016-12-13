# Commands

    $ heroku run "sh target/bin/odk2publisher"
    $ heroku run "sh target/bin/testpost"


https://devcenter.heroku.com/articles/heroku-command
https://devcenter.heroku.com/categories/command-line
https://devcenter.heroku.com/articles/using-the-cli

----------------
# Maven
https://devcenter.heroku.com/articles/run-non-web-java-processes-on-heroku

    $ heroku login
    $ git clone https://github.com/heroku/devcenter-java-worker   // Fetch example repo
    $ mvn package   //builds. Will download dependencies
    $ target\bin\worker.bat   //Run the worker
    $ target\bin\oneoff      //Run the one-off process

Ready to deploy to Heroku:
----------------------------
You declare how you want your application executed in a Procfile in the project root. Create this file as below:

`worker: sh target/bin/worker`

There is no need to add commands that you want executed in a one-off dyno to a Procfile - the one-off dyno mechanism lets you specify the command when you launch the one-off dyno

Deploy to Heroku:
-----------------
Commit your changes to Git:

    $ git init
    $ git add .
    $ git commit -m "Ready to deploy"

Create the app:

    $ heroku create

Rename the app:  (https://devcenter.heroku.com/articles/renaming-apps)

    $ heroku apps:rename odk2-publisher
 If you are using the CLI to rename an app from inside the Git checkout directory, your remote will be updated automatically

Deploy your code:

    $ git push heroku master

Scaling worker processes  (not for my one-off task)
------------------------
You can now start and scale your worker dynos using a command like this:

    $ heroku ps:scale worker=1
    $ heroku logs --tail         //Ctrl-C to stop viewing log

One-off dynos:
--------------
If your process is a command you wish to run manually on an as needed basis, you can do so with a one-off dyno. Use the heroku run command to start a one-off dyno and execute the command:

    $ heroku run "sh target/bin/oneoff"


### Scheduler:

    $ heroku addons:create scheduler:standard
    $ heroku addons:open scheduler


### Adding new classe:
- Edit pom.xml and add <program> entries
- Rebuild with `mvn package`

### Using Libraries
Add libraries to C:\Users\Andrew\.m2\repository
You can also add a dependency in pom.xml, something like this:
```xml
    <dependencies>
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpclient</artifactId>
            <version>4.5.2</version>
        </dependency>
    </dependencies>
```




### Adding ODK jar files for wink client
copy files into a lib directory in project
```
mvn install:install-file -DlocalRepositoryPath=repo -DcreateChecksum=true -Dpackaging=jar -Dfile=lib\aggregate-rest-interface-2014-11-24.jar -DgroupId=org.opendatakit.aggregate.odktables -DartifactId=rest -Dversion=2014-11-24

mvn install:install-file -DlocalRepositoryPath=repo -DcreateChecksum=true -Dpackaging=jar -Dfile=lib\wink-1.0-SNAPSHOT.jar -DgroupId=org.opendatakit.wink -DartifactId=client -Dversion=1.0-SNAPSHOT
```
### Add this to pom:
```xml
  <repositories>
      <repository>
          <id>project</id>
          <url>file://${project.basedir}/repo</url>
      </repository>
  </repositories>

  <dependencies>
    <dependency>
        <groupId>org.apache.httpcomponents</groupId>
        <artifactId>httpclient</artifactId>
        <version>4.5.2</version>
    </dependency>
    <dependency>
        <groupId>org.json</groupId>
        <artifactId>json</artifactId>
        <version>20151123</version>
    </dependency>
    <dependency>
      <groupId>org.opendatakit.aggregate.odktables</groupId>
      <artifactId>rest</artifactId>
      <version>2014-11-24</version>
    </dependency>
    <dependency>
      <groupId>org.opendatakit.wink</groupId>
      <artifactId>client</artifactId>
      <version>1.0-SNAPSHOT</version>
    </dependency>
  </dependencies>
```
Taken from:
http://stackoverflow.com/questions/364114/can-i-add-jars-to-maven-2-build-classpath-without-installing-them
https://github.com/nikita-volkov/install-to-project-repo

Groupid = where to find it
Artifact = jar name without ".jar"
version = 0.0.1-SNAPSHOT
(https://maven.apache.org/guides/mini/guide-naming-conventions.html)


--------------
# Gradle

Open the app:

    $ heroku open

check how many dynos are running using the ps command:

    $ heroku ps

Determining your Free dyno hours:

    $ heroku ps -a <app name>
    $ heroku ps -a salty-earth-29847

------------------
Install dependencies in local folder (will download gradle and others)

    $ gradlew.bat stage

Run the app locally:

    $ heroku local web -f Procfile.windows

Your app will now be running at http://localhost:5000. (Procfile sets the PORT environment variable.)

--------
