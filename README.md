# Cuttlefish
Cuttlefish aims to be a highly extensible visualisation and analysis platform for all kinds of network data

# Download and Execution

To run the most recent version of Cuttlefish download `cuttlefish-bundle.zip` from the [release page](https://github.com/dev-cuttlefish/cuttlefish/releases/tag/v2.5-beta) of version 2.5, extract the contents of the zip file and run `cuttlefish.jar` within the extracted cuttlefish-bundle folder using the command:
```
java -jar cuttlefish.jar
```
You may download older versions of Cuttlefish from the [releases](https://github.com/dev-cuttlefish/cuttlefish/releases) page of this repository. However, Cuttlefish version 2.5 is the only one which runs with the current JRE. All the older versions produce errors when run with Java 7 or higher, since they use the package `com.sun.image.codec.jpeg` which is deprecated.



# Build
    
    This readme is intended to help users and developers in building, deploying
    and running Cuttlefish. It also provides detailed information on a series
    of problems encountered when trying to deploy Cuttlefish in a single jar
    file, which for the moment has not been achieved.
    
    
    Ilias Rinis (iliasr -at- gmail.com)
    30 Sept. 2013

    

## Build description

The latest upgrade of Cuttlefish has introduced the usage of the JOGL v2.0.2
library. This library consists of two main modules, the gluegen-rt and the 
jogl-all modules. Both depend on a smaller set of native (platform-dependent)
libraries though. These libraries can be seen in lib/jogl-all-platforms and are
the jar files that contain *natives* in their name. The rest of the libraries
though do not have such dependencies (like Gephi Toolkit).

Due to constraints of the default Java ClassLoader, the creation of a single
jar that contains all dependencies is not possible at the moment, since the
native libraries cannot be loaded from within the jar. Section III provides
insight on how to deal with this issue in the future, and manage to create a
single jar for Cuttlefish.

The package of Cuttlefish now consists of a zip file, named 
cuttlefish-bundle.zip . This file contains cuttlefish.jar and a directory,
cuttlefish_lib, with all the library dependencies. The classpath of the
manifest cuttlefish.jar points to this directory so that all libraries can be
loaded. For this reason, cuttlefish.jar and cuttlefish_lib MUST BE PLACED IN
THE SAME DIRECTORY, NEXT TO EACH OTHER.


## Build Instructions
    
The build.xml ant script is used to build the project and create the bundle
of Cuttlefish. Simply running 'ant' or 'ant dist' will invoke the complete
process: clean, build, create main project jar, copy lib dependencies, and also
finally create the .zip project bundle. The project can be simply run with
'java -jar cuttlefish.jar'.

    
## Building a single jar
    
For the moment, JOGL does not encourage, neither fully support the building of
a single, fat jar of applications that use JOGL. They recommend using Java
WebStart (JNLP) to launch applications. For more information:
-> http://jogamp.org/jogl/doc/deployment/JOGL-DEPLOYMENT.html

However, there have been some tests that demonstrate that it is possible to
build a single fat jar through Eclipse, as seen in:
-> http://jogamp.org/wiki/index.php/JogAmp_JAR_File_Handling


Still, lately there has been a regression of Test-2 (which shows the creation
of a single jar), and until this is fixed, it might not be possible to create
a single jar for Cuttlefish. It is recommended to follow the progress of this
bug. I have posted a related question in the forums of jogamp, which might shed 
some light.
-> http://forum.jogamp.org/Packaging-JOGL-into-single-generated-JAR-in-Eclipse-td4030045.html

There have been several discussions in the freenode IRC channel of jogamp, the
log of which might be also useful to the developers, and is saved in the
repository in
-> lib/jogl-all-platforms/jogamp-irc-jar-bundle-issue.log

This log follows several debug attempts by me and members of the channel, that
generally locate the problem in the internal loading of native libraries within
jogl -- without however discovering the root cause. It has been suggested 
(and actually, this is how JOGL intended it) that a project that uses JOGL must
consist of two jars:

~ cuttlefish.jar
with cuttlefish, all lib dependencies, gluegen-rt.jar, jogl-all.jar

~ cuttlefish-natives-os.and.arch.jar (e.g. cuttlefish-natives-linux-i586.jar)
with jogl-all-natives-os.and.arch.jar, gluegen-rt-natives-os.and.arch.jar


Finally, there are numerous attempts from people trying to achieve this, and 
the jogl forums may have some interesting discussions.
-> http://forum.jogamp.com

