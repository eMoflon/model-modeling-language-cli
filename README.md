# Model Modeling Language CLI

The [Model Modeling Language](https://github.com/eMoflon/model-modeling-language) (MML) is a domain-specific modeling language implemented with the open-source
language engineering tool [Langium](https://langium.org/). This repository contains a CLI that serves as a link between the Javascript-based MML and the Java-based Eclipse Modeling Framework as well as the HiPE Pattern Matcher.

The CLI can be integrated directly in the VSCode plugin of MML in order to access individual functions directly from the user interface, but it can also be executed in the command line, in which case inputs must be available as a file.

---

ℹ️ **The CLI requires at least Java 17!**

---

* [How to build](#how-to-build)
* [Usage](#usage)


## How to build

1. Make sure that a Java JDK is installed that fulfills the requirements
2. Build the CLI using Maven
   ```shell
   mvn package
   ```

## Usage
### Usage with the MML VSCode plugin
Install the MML VSCode plugin according to the corresponding instructions. 
Also build the CLI as a JAR file. Open the VSCode settings and navigate to the "Model Modeling Language" section under "Extensions". 
Enter the path to the Jar file in the field provided.

🎉 You can now use the Modeling Language to its full extent directly in VSCode!

### Stand-alone use
