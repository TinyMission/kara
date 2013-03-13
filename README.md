# Kara Web Framework

Kara is a web framework for the JVM using the [Kotlin programming language](http://confluence.jetbrains.net/display/Kotlin/Welcome).  It uses Kotlin's unique syntax to allow developers to write succinct, statically-typed HTML and CSS all in one language.

For more information and documentation, check out [karaframework.com](http://karaframework.com/).

## Authors

Kara is developed by [Tiny Mission](http://tinymission.com). We're a small web and mobile development company and hope to use Kara to help us work faster and write better code.


## Contributing

There's plenty of work left to do to make Kara a first class framework, and we'd welcome contributions. Contact andy at tinymission.com to get involved.

## Continous integration and Downloads
CI is set up at [TeamCity](http://teamcity.jetbrains.com/project.html?projectId=project77&tab=projectOverview).
Download latest successful [build](http://teamcity.jetbrains.com/repository/download/bt432/.lastSuccessful/kara-{build.number}.zip)

## Bugs, Suggestions and Feature Requests

All issues for Kara are tracked [here](http://youtrack.codebetter.com/issues/kara)

## License

Kara is Open Source and licensed under the Apache Licenses, version 2.0. It can be freely used in commercial projects.


## Running Sample Project

To start sample project you need:
* either download kara binary package and unpack or compile `kara-dist` artifact in IDEA project
* close IDEA projects
* configure Path Variable `KARA_HOME` in IDEA to point to kara root directory or to the output of `kara-dist` artifacts, i.e. `<project home>/out/artifacts/kara_dist`
* open samples directory
* run `../kara s` command
* open demo pages at embedded web server running on `http://localhost:3000`

## Generating new project

To start sample project you need:
* either download kara binary package and unpack or compile `kara-dist` artifact in IDEA project
* close IDEA projects
* configure Path Variable `KARA_HOME` in IDEA to point to kara root directory or to the output of `kara-dist` artifacts, i.e. `<project home>/out/artifacts/kara_dist`
* open the directory you like to start with
* run `<path to kara home>/kara g project <project name> --package=<package name> --ide=idea` command
* open generated project in IDEA
* use `Server` run configuration to start/debug your application

