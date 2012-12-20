# Kara Web Framework

Kara is a web framework for the JVM using the [Kotlin programming language](http://confluence.jetbrains.net/display/Kotlin/Welcome).  It uses Kotlin's unique syntax to allow developers to write succinct, statically-typed HTML and CSS all in one language.

## Overview

Kara is still atan alpha stage, but it contains the following feature set:

* A Rails-like MVC architecture, where controllers contain business logic and views render the output.
* A statically-typed HTML view engine written in Kotlin
* Support for generating JSON responses directly from objects
* A statically-typed CSS rendering engine to compose stylesheets
* A Jetty-based embedded web server for developing your application, including automatically reloading the application when it's rebuilt
* A flexible JSON-based configuration system
* A project and file generator system to aid in setting up your code

As a project in heavy development, there are still a number of *planned* features that *have yet to be implemented*:

* Integration with Kotlin's Javascript compiler to generate Javascript from Kotlin code (regular Javascript is currently supported)
* A deployment generator that creates servlet-ready WAR files
* A plugin architecture that allows plugins for integrating third-party libraries

There are several features that are *not planned* for Kara, since we feel they fall outside of the scope of the framework:

* A database integration library. There are plenty of good ORM's for the JVM, and while there's a chance that Kotlin could be used to do some interesting things with mapping databases, this can be developed independently of Kara
* Support for different HTML template engines (JSP, FreeMaker, Velocity, Jade, etc.)
* Support for different Javascript wrappers (Coffeescript, TypeScript, Dart, etc.)


## Authors

Kara is developed by [Tiny Mission](http://tinymission.com). We're a small web and mobile development company and hope to use Kara to help us work faster and write better code.


## Contributing

There's plenty of work left to do to make Kara a first class framework, and we'd welcome contributions. Contact andy at tinymission.com to get involved.


## License

Kara is Open Source and licensed under the Apache Licenses, version 2.0. It can be freely used in commercial projects.
