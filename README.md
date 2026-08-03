# Damien project template

This is a project template for a greenfield Java project. It's named _Damien_. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, reload the Gradle project in IntelliJ. The GUI can be started with the Gradle `run` task, or by running `duke.Launcher` from IntelliJ.

## Running Damien

The JavaFX GUI is now the main interface. From the project root, run:

```bash
sdk use java 25.0.3.fx-zulu
./gradlew run
```

Type any command in the text box and press `Enter` or click `Send`. Entering
`bye` displays Damien's goodbye message and closes the window. The old text UI
is deprecated but remains available for regression testing:

```bash
./gradlew runCli
```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
