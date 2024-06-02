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

   **Note:** The first build may fail because local dependencies are not found. 
   In this case, use:
   ```shell
   mvn clean validate package -U
   ```

## Usage
### Usage with the MML VSCode plugin
Install the MML VSCode plugin according to the corresponding instructions. 
Also build the CLI as a JAR file. Open the VSCode settings and navigate to the "Model Modeling Language" section under "Extensions". 
Enter the path to the Jar file in the field provided.

🎉 You can now use the Modeling Language to its full extent directly in VSCode!

### Stand-alone use

The CLI can also be used independently of the VSCode plugin. In this case, input and output are usually via files, 
the path of which is also specified in the command.

In general, the CLI can be called by executing the JAR file.

```shell
java -jar ./target/model-modeling-language-cli-1.0-SNAPSHOT.jar
```

In the following we use the alias `mmlcli`.

```text
Usage: mmlcli [-hV] [COMMAND]
            -h, --help Show this help message and exit.
            -V, --version Print version information and exit.
```

#### Generate
The `generate` command is used to generate Ecore and XMI files from the serialized MML format. By default, the input 
is via STDIN, the output is in the form of the generated files in a specified directory. Optionally, a file whose 
content is in serialized MML format can be specified instead of the STDIN input.

```text
Usage: mmlcli generate [-f[=SERIALIZED]] <projectName> <outputDirectory>
            <projectName>           Name of the entire project/workspace
            <outputDirectory>       Path to the output directory
            -f, --file[=SERIALIZED] Path to the serialized workspace as json file
```

#### Serialize
The `serialize` command is used to serialize Ecore files into the MML format. The entry is made by specifying a path 
to an Ecore file. The output is via STDOUT by default, but optionally a path to an output file can also be specified 
in which the serialized model is to be saved.

```text
Usage: mmlcli serialize [-o[=SERIALIZED]] <ecoreFile>
            <ecoreFile>             Path to an Ecore file
            -o, --out[=SERIALIZED]  Path to the output directory
```

#### Extender
The `extender` command is used to extend all metamodel classes in a metamodel by a common superclass. 
This superclass adds a `nodeId` attribute to all classes, which is used to assign unique identifiers. 
This attribute is initialized for all elements in the transferred model.

```text
Usage: mmlcli extender [-hV] [-i[=(true|false)]] <ecoreFile> <modelFile>
Extend a metamodel and model with a unique node identifier
      <ecoreFile>   path to the Ecore file containing the metamodel
      <modelFile>   path to the XMI file containing the model
  -h, --help        Show this help message and exit.
  -i, --invert[=(true|false)]
                    remove identifiers if present
  -V, --version     Print version information and exit.
```

#### Modelserver
The `modelserver` command is used to generate a ModelServer for constraint evaluation. 
Both metamodel-specific and constraint-specific code is generated and compiled.

```text
Usage: mmlcli modelserver [-hV] [-e[=(true|false)]] [-f[=path]] [-p[=
                          (true|false)]] [-r[=(true|false)]] [-v[=
                          (true|false)]] <workspacePath> <ecorePath> <modelPath>
Generate a ModelServer
      <workspacePath>   path to the working directory of the ModelServer
      <ecorePath>       path to the Ecore file containing the metamodel
      <modelPath>       path to the XMI file containing the model
  -e, --[no-]run-model-extender[=(true|false)]
                        add unique identifiers to the metamodel and model
                          before generating the ModelServer
  -f, --file[=path]     path to a serialized constraint document
  -h, --help            Show this help message and exit.
  -p, --package-jar[=(true|false)]
                        pack the ModelServer into a Jar file
  -r, --[no-]run-model-server[=(true|false)]
                        start the ModelServer after generation is complete
  -v, --verbose[=(true|false)]
                        prints extended compiler output
  -V, --version         Print version information and exit.
```