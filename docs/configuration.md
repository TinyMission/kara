---
layout: default
title: Kara Configuration
isDoc: true
docPage: configuration
displayName: Configuration
icon: check
---

## Kara Configuration

Each Kara application is configured with a set of JSON configuration files.
Kara encourages convention-over-configuration, so these files are generated for you when you create the project and don't need to be edited at all to get going.

The primary config file is located at config/appconfig.json. By default, it looks like this:

    {
         "kara": {
             "appPackage": "<package>",
             "publicDir": "public",
             "stylesheetDir": "stylesheets",
             "sessionDir": "tmp/sessions"
         }
    }

Even though the file itself contains nested objects, Kara flattens all of the keys by joining them with periods.
So, the file above is interpreted as:

    kara.appRoot: <path to project>
    kara.port: 3000
    kara.publicDir: public
    kara.appPackage: <package>
    kara.sessionDir: tmp/sessions
    kara.stylesheetDir: stylesheets

You can view the flattened configuration at any time by running

    kara config

The default config file contains just the values that Kara uses directly.
However, you can specify your own custom values:

    {
        "kara": {
            "appPackage": "<package>",
            "publicDir": "public",
            "stylesheetDir": "stylesheets",
            "sessionDir": "tmp/sessions"
        }
        "mylib": {
            "value1": "foo"
            "value2": "bar"
        }
    }

This can be useful for configuring third party libraries without needing to define your own file format and reading mechanism.
The config values can be retrieved from an appConfig instance (available in the controllers and ActionContexts):

    val value1 = appConfig["mylib.value1"]

### Environments

Besides the default appconfig.json file, you can have config files for each runtime environment that are named like *appconfig.environment.json*.
Values in the environment config file will override those in the default file.
By default, Kara creates an appconfig.development.json file, but you can create others for as many environments as you want.