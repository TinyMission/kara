---
layout: default
title: Kara Overview
isDoc: true
docPage: overview
displayName: Overview
icon: home
---

## Kara Overview

Kara is still atan alpha stage, but it contains the following feature set:

* A Rails-like MVC architecture, where controllers contain business logic and views render the output.
* A statically-typed HTML view engine written in Kotlin
* Support for generating JSON responses directly from objects
* A statically-typed CSS rendering engine to compose stylesheets
* A Jetty-based embedded web server that automatically reloads the application when it's rebuilt
* A flexible JSON-based configuration system
* A project and file generator system to aid in setting up your code
* An environment system for different runtime environments

As a project in heavy development, there are still a number of **planned** features that **have yet to be implemented**:

* Much more documentation, clearly
* Integration with Kotlin's Javascript compiler to generate Javascript from Kotlin code (regular Javascript is currently supported)
* A deployment generator that creates servlet-ready WAR files
* Asset compilation and minification
* A plugin architecture that allows plugins for integrating third-party libraries

There are several features that are **not planned** for Kara, since we feel they fall outside of the scope of the framework:

* Support for different HTML template engines (JSP, FreeMarker, Velocity, Jade, etc.)
* Support for different Javascript wrappers (Coffeescript, TypeScript, Dart, etc.)
* A database integration library. There are plenty of good ORM's for the JVM, and while there's a chance that Kotlin could be used to do some interesting things with mapping databases, this can be developed independently of Kara

### Philosophy

Kara is not intended to be everything to ever one. There are slews of excellent web frameworks out there that meet a wide range of needs for developers.
If you're looking for something that lets you use a dynamic language with really simple syntax, or use proper HTML markup that a designer can edit, you'd be better served by something like Rails or Django.

Kara's specific goal is to make a web framework that focuses on developer productivity while maintaining a safe yet powerful syntax for defining markup.
The HTML view DSL is more concise than most markup languages used today, yet maintains strong type safety to help avoid common pitfalls that you'd experince in dynamic languages.
Plus, Kotlin is an excellent language to develop with - letting you easily express complex ideas through simple code that leverages the power of the JVM.

## Installation

#### From Source
To install from source, simply checkout the [repository](https://github.com/TinyMission/kara), build the project and add the location to your system path.

#### From Binary

You can obtain the latest binaries from the [Build Server](http://teamcity.jetbrains.net/viewType.html?buildTypeId=bt432). Click on the Artifacts to download them. Add the location to your system path.

#### Running Kara


Kara is multi-platform. On OSX and Unix based system, a **kara** bash script is provided. On Windows, a **kara.bat** batch file is available. Once you have the location added to your path, you can simply run kara by typing **kara** on the command line.

### Platforms and Prerequisites

To run Kara, the only concrete prerequisite is a working JDK. Optionally, [Ant](http://ant.apache.org/) can be used to enable automatic server restart during development.

To develop a Kara app, it is highly recommended to use [IntelliJ IDEA](http://www.jetbrains.com/idea/) with the [Kotlin Plugin](http://confluence.jetbrains.net/display/Kotlin/Getting+Started).

## Project Structure

Here's the general structure of a Kara project:

    /bin                            Binary output (.class files)
    /config                         Application config files
    /lib                            Third party libraries
    /public/images                  Image resources
    /public/javascripts             Javascript source files
    /public/stylesheets             CSS stylesheets (third party or generated)
    /public/system                  Files that need to persist between deployments
    /src/<package>/routes           Application routes and controllers
    /src/<package>/models           Database integration
    /src/<package>/styles           Stylesheet sources
    /src/<package>/views            HTML view sources
    /tmp                            Temporary files, like sessions
    /ivy.xml                        Application dependencies


