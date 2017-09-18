# Cuttlefish
Cuttlefish aims to be a highly extensible visualisation and analysis platform for all kinds of network data

# Download and Execution

To run the most recent version of Cuttlefish download `cuttlefish-bundle.zip` from the [release page](https://github.com/dev-cuttlefish/cuttlefish/releases/tag/v2.5-beta) of version 2.5, extract the contents of the zip file and run `cuttlefish.jar` within the extracted cuttlefish-bundle folder using the command:
```
java -jar cuttlefish.jar
```
You may download older versions of Cuttlefish from the [releases](https://github.com/dev-cuttlefish/cuttlefish/releases) page of this repository. However, Cuttlefish version 2.5 is the only one which runs with the current JRE. All the older versions produce errors when run with Java 7 or higher, since they use the package `com.sun.image.codec.jpeg` which is deprecated.
