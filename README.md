# Kara Web Framework

Kara is a web framework for the JVM using the [Kotlin programming language](http://confluence.jetbrains.net/display/Kotlin/Welcome).  It uses Kotlin's unique syntax to allow developers to write succinct, statically-typed HTML and CSS all in one language.

## Overview

Kara is still atan alpha stage, but it contains the following feature set:

* A Rails-like MVC architecture, where controllers contain business logic and views render the output.
* A statically-typed HTML view engine written in Kotlin
* Support for generating JSON responses directly from objects
* A statically-typed CSS rendering engine to compose stylesheets
* A Jetty-based embedded web server that automatically reloads the application when it's rebuilt
* A flexible JSON-based configuration system
* A project and file generator system to aid in setting up your code

As a project in heavy development, there are still a number of **planned** features that **have yet to be implemented**:

* Much more documentation, clearly
* Integration with Kotlin's Javascript compiler to generate Javascript from Kotlin code (regular Javascript is currently supported)
* A deployment generator that creates servlet-ready WAR files
* Asset compilation and minification
* A plugin architecture that allows plugins for integrating third-party libraries

There are several features that are **not planned** for Kara, since we feel they fall outside of the scope of the framework:

* Support for different HTML template engines (JSP, FreeMaker, Velocity, Jade, etc.)
* Support for different Javascript wrappers (Coffeescript, TypeScript, Dart, etc.)
* A database integration library. There are plenty of good ORM's for the JVM, and while there's a chance that Kotlin could be used to do some interesting things with mapping databases, this can be developed independently of Kara


## Installation

To install Kara, simply checkout the repository and add the location to your system path.

### Platforms and Prerequisites

To run Kara, the only concrete prerequisite is a working JDK. Optionally, [Ant](http://ant.apache.org/) can be used to enable automatic server restart during development.

To develop a Kara app, it is highly recommended to use [IntelliJ IDEA](http://www.jetbrains.com/idea/) with the [Kotlin Plugin](http://confluence.jetbrains.net/display/Kotlin/Getting+Started).

So far, Kara has only been tested on Mac OS X. However, the runtime is obviously platform independent and the bash script should work fine on any other *nix. In order to get it to work on Windows, we'll need to develop a batch file to run it.


## Command Line Usage

Here's an overview of the Kara command line usage:

    Usage:
        kara [-options] command args

    Commands:
        c, config    Show the application's configuration for the current environment
        g, generate  Generates a new project or file (see below)
        h, help      Show this help message
        s, server    Run the Kara server on the current directory

    Options:
        -d, --debug  Show debug log messages
        -e, --env    Specify the environment (default is --env=development)
        -i, --info   Show info log messages (default)
        -w, --warn   Shwo only warning log messages

    Generators:
        project <name>      Generates a new Kara project with the given name.
                            Use the --package=<package> option to specify a package
                            that's different than the project name.
        controller <name>   Generates a new controller with the given name.
                            "Controller" will be automatically appended to the name)
        view <controller> <view>  Generate a new view for the given controller.

### Creating a Project

To create a Kara project, navigate to the directory you'd like the new project in and run something like:

    kara generate project MyKaraApp --package=com.example

This will create a new project in MyKaraApp with the package com.example. At this point, the project is just a set of directories and some boilerplate code.

To import the project into IDEA, follow the steps before. **Note, this is quite crude and we would benefit from some guidance from JetBrains as to how to smooth this out.**

* Open IDEA and select *File -> Import Project* and select your project's directory
* Select *File -> Project Structure* and choose a JDK from the dropdown. Either 6 or 7 should work
* After the project has been imported, select *File -> Import Module* and select the <package>.iml file that Kara created
* Open a .kt file in the project (like src/<package>/Application.kt) and IDEA will prompt you to set up the module with Kotlin, select "Set up module as JVM Kotlin module"
* Right click on lib/KaraLib.jar in the project navigator and select *Add as Library*

That should be it. You should now be able to build the project.

Once the project is built, it can be run in the development server by simply running:

    kara server


## Project Structure

Here's the general structure of a Kara project:

    /bin                            Binary output (.class files)
    /config                         Application config files
    /lib                            Third party libraries
    /public/images                  Image resources
    /public/javascripts             Javascript source files
    /public/stylesheets             CSS stylesheets (third party or generated)
    /public/system                  App-generated files that need to persist between deployments
    /src/<package>/controllers      Application controllers
    /src/<package>/models           Database integration
    /src/<package>/styles           Stylesheet sources
    /src/<package>/views            HTML view sources
    /tmp                            Temporary files, like sessions



## Authors

Kara is developed by [Tiny Mission](http://tinymission.com). We're a small web and mobile development company and hope to use Kara to help us work faster and write better code.


## Contributing

There's plenty of work left to do to make Kara a first class framework, and we'd welcome contributions. Contact andy at tinymission.com to get involved.


## License

Kara is Open Source and licensed under the Apache Licenses, version 2.0. It can be freely used in commercial projects.
