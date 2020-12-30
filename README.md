[![Build Status](https://travis-ci.com/kavros/Api.Groceries.svg?branch=master)](https://travis-ci.com/kavros/Api.Groceries)
# Overview
Automated Invoice Processing workflow.

## Development
* Use Intellij IDEA for development.
* SQL Server Management Studio

## Deployment 
* Add the required databases and tables.
* Open terminal and run `nvm package`
* Go to target folder and run `java -jar rest-service-0.0.1-SNAPSHOT.jar`.

## Databases & Tables
* See /src/main/resources/hibernate.cgf.xml
* `Api.Groceries` with the following tables:
    * Rules
    * Mappings
    * Records
* Cmp00x with the following tables:
    * Smast
