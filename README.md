# Jenkins telegram chatops
[![codecov](https://codecov.io/gh/MikeSafonov/jenkins-telegram-chatops/branch/master/graph/badge.svg)](https://codecov.io/gh/MikeSafonov/jenkins-telegram-chatops)
[![Build Status](https://travis-ci.com/MikeSafonov/jenkins-telegram-chatops.svg?branch=master)](https://travis-ci.com/MikeSafonov/jenkins-telegram-chatops)
[![Conventional Commits](https://img.shields.io/badge/Conventional%20Commits-1.0.0-yellow.svg)](https://conventionalcommits.org)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_jenkins-telegram-chatops&metric=alert_status)](https://sonarcloud.io/dashboard?id=MikeSafonov_jenkins-telegram-chatops)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_jenkins-telegram-chatops&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=MikeSafonov_jenkins-telegram-chatops)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_jenkins-telegram-chatops&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=MikeSafonov_jenkins-telegram-chatops)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_jenkins-telegram-chatops&metric=security_rating)](https://sonarcloud.io/dashboard?id=MikeSafonov_jenkins-telegram-chatops)

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_jenkins-telegram-chatops&metric=bugs)](https://sonarcloud.io/dashboard?id=MikeSafonov_jenkins-telegram-chatops)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_jenkins-telegram-chatops&metric=code_smells)](https://sonarcloud.io/dashboard?id=MikeSafonov_jenkins-telegram-chatops)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_jenkins-telegram-chatops&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=MikeSafonov_jenkins-telegram-chatops)

[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_jenkins-telegram-chatops&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=MikeSafonov_jenkins-telegram-chatops)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_jenkins-telegram-chatops&metric=ncloc)](https://sonarcloud.io/dashboard?id=MikeSafonov_jenkins-telegram-chatops)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=MikeSafonov_jenkins-telegram-chatops&metric=sqale_index)](https://sonarcloud.io/dashboard?id=MikeSafonov_jenkins-telegram-chatops)

jenkins-telegram-chatops is a [Spring Boot](https://github.com/spring-projects/spring-boot) application which handing 
[Jira](https://www.atlassian.com/software/jira) webhook events and sends notifications via 
[Telegram](https://telegram.org) bot.

## Key features

TBA


## Build

### Build from source

You can build application using following command:

    ./gradlew clean build
    
#### Requirements:

JDK >= 11

### Unit tests

You can run unit tests using following command:

    ./grdlew test

### Mutation tests

You can run mutation tests using following command:

    ./grdlew pitest

You will be able to find pitest report in `build/reports/pitest/` folder.

### Running jenkins-telegram-chatops

After the build you will get [fully executable jar archive](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/html/#packaging-executable-configuring-launch-script)
 
You can run application using following commands:

    java -jar jenkins-telegram-chatops.jar
or

    ./jenkins-telegram-chatops.jar

 
## Contributing

Feel free to contribute. 
New feature proposals and bug fixes should be submitted as GitHub pull requests. 
Fork the repository on GitHub, prepare your change on your forked copy, and submit a pull request.

**IMPORTANT!**
>Before contributing please read about [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0-beta.2/) / [Conventional Commits RU](https://www.conventionalcommits.org/ru/v1.0.0-beta.2/)
