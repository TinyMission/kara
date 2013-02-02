---
layout: default
title: Kara Command Line
isDoc: true
docPage: commandline
displayName: Command Line
icon: angle-right
---

## Command Line Usage

Here's an overview of the Kara command line usage:


    Usage:
        kara [-options] command args

    Commands:
        c, config           Show the application's configuration for the current environment
        g, generate         Generates a new project or file (see below)
        d, dependencies     Generates dependencies for application's ivy.xml file to application's lib folder.
        h, help             Show this help message
        s, server           Run the Kara server on the current directory

    Options:
        -d, --debug         Show debug log messages
        -e, --env           Specify the environment (default is --env=development)
        -i, --info          Show info log messages (default)
        -w, --warn          Show only warning log messages

    Generators:
        project <name>      Generates a new Kara project with the given name.

                            Use the --package=<package> option to specify a package
                            that's different than the project name.


                            Use the --ide=ide_name option to specify a package
                            that can be opened directly in the IDE of choice.

                            Currently supports: 'idea' for IntelliJ IDEA

        update              Updates the application's Kara dependency to the latest version
        
        controller <name>   Generates a new controller with the given name.
                            "Controller" will be automatically appended to the name)
    
        view <controller> <view>  Generate a new view for the given controller.

### Creating a Project

To create a Kara project, navigate to the directory you'd like the new project in and run something like:

    kara generate project MyKaraApp --package=com.example

This will create a new project in MyKaraApp with the package com.example. At this point, the project is just a set of directories and some boilerplate code.

To import the project into the IDE, it is recommended to specify the --ide option with your IDE of choice. To import into IntelliJ IDEA, use --ide=idea and then you can just open up the project and build it in IDEA without further steps.


**Recommended:** you can add the build.xml file that Kara generates to IDEA's Ant tasks and set it to be executed after compilation.
This will force the Kara server to reload the application code whenever it's rebuilt.


### Running the Development Server

Once the project is built, it can be run in the development server by simply running:

    kara server

The server will reload the application code whenever the tmp/restart.txt file is touched:

    touch tmp/restart.txt


### Other Generators

To create a new controller in your existing project, simply run something like:

    kara generate controller Blog

which will create a controller called BlogController.

To create a new view in your existing project, run something like:

    kara generate view Blog List

which will generate a view called List that belongs to the BlogController.

Since the Kara library is shipped with the Kara installation and changes frequently, it's often necessary to update the KaraLib.jar file in your application's lib folder.
Running the *update* generator will do just that:

    kara generate update
