# ODK 2 Publisher
A Java script to pull new entries from ODK 2 Aggregate, and publish to OpenFn.

Desktop for this app is at Heroku:
https://dashboard.heroku.com/apps/odk2-publisher

### Technical information
- Run using: `heroku run "sh target/bin/testpost"``

### Repo Syncing
This repo is stored in two locations.

1. [Heroku](https://dashboard.heroku.com/apps/odk2-publisher)
2. [Github](https://github.com/AbalobiSA/odk2-publisher)

### Getting Started

Clone the repo from heroku:

    $ heroku git:clone -a odk2-publisher

Add the github repo as origin

    $ git remote add origin https://github.com/AbalobiSA/odk2-publisher.git

You will now have two remotes:
- Heroku
- Origin

### Installing dependencies
This is a maven project, and the majority of the dependencies can be found
in Maven Central repo. However, we import one custom jar file.

You will need to clone and build the DEVELOPMENT branch of this repo:
    $ https://github.com/opendatakit/sync-client/tree/development

Then, read the documentation at the repo's github page. It will guide you
through creating a jar file to include in this project.

You will need to use these commands to add the jar to this project:

```
mvn install:install-file -Dfile=<path-to-file> -DpomFile=<path-to-pomfile>

EXAMPLE

mvn install:install-file -Dfile=C:\cygwin64\home\Carl\Git\sync-client\target\sync-client-1.0-SNAPSHOT-jar-with-dependencies.jar -DpomFile=C:\cygwin64\home\Carl\Git\sync-client\pom.xml
```

The dependency should already be in pom.xml - if not, it should look like this:

```
<dependency>
    <groupId>org.opendatakit</groupId>
    <artifactId>sync-client</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

And lastly, you can import the jar contents as shown here:

```
import org.opendatakit.sync.client.SyncClient;
```
