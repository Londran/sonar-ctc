# CTC++ plugin for Sonarqube
The plug-in allows you to import the code coverage information from [Testwell CTC++](http://www.verifysoft.com/en_ctcpp.html) into the [SonarQube](https://www.sonarqube.org) database and its visualization within the SonarQube web interface.



## Prerequisites

For using this Plugin, you need to have at least *sonarqube-version 5.6*.

For building this plugin, you will need *maven 3.X* and *Java 8* or higher.

## Quickstart

Provide a configuration using the file "sonar-project.properties". Set following property, besides the standard ones:
**sonar.ctc.report.path=profile.txt**

## Download

| **AppVeyor CI** (Continuous Delivery service for Windows) | Download |
| --- | --- |
| [![Build status](https://ci.appveyor.com/api/projects/status/j12lfbjketpccxvr?svg=true)](https://ci.appveyor.com/project/rufinio/sonar-ctc)|[sonar-ctc-plugin](https://ci.appveyor.com/project/rufinio/sonar-ctc/build/artifacts) |
