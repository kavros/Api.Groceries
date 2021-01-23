[![Build](https://github.com/kavros/Api.Groceries/workflows/maven_build/badge.svg)](https://github.com/kavros/Api.Groceries/actions)
# Overview
Automated Invoice Processing workflow.

## Development
* Use Intellij IDEA for development.
* SQL Server Management Studio
* Add the required databases and tables by running the deployment.sql script or use instructions bellow to generate a new one.

## Deployment 
* Open terminal and run `nvm package`
* Copy the fonts folder in the same folder as the jar.
* Go to target folder and run `java -jar rest-service-0.0.1-SNAPSHOT.jar`.

## Databases & Tables
* See /src/main/resources/hibernate.cgf.xml
* `Api.Groceries` with the following tables:
    * Rules
    * Mappings
    * Records
* Cmp00x with the following tables:
    * Smast

## Export Database from SSMS
1. Right click your database in your server, and select `Tasks > Generate Scripts`.
2. At Introduction step, click `Next`.
3. At Choose Objects, you can either select either the entire database (including functions, and etc) or specify what objects you want to generate. In your case, you should select Script entire databases and all database objects then hit Next.
4. Choose your file name and destination then click next
5. Review and then click next