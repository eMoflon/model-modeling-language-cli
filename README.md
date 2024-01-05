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
The serialize command is used to serialize Ecore files into the MML format. The entry is made by specifying a path 
to an Ecore file. The output is via STDOUT by default, but optionally a path to an output file can also be specified 
in which the serialized model is to be saved.

```text
Usage: mmlcli serialize [-o[=SERIALIZED]] <ecoreFile>
            <ecoreFile>             Path to an Ecore file
            -o, --out[=SERIALIZED]  Path to the output directory
```

