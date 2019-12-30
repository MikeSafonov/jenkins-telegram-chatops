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

`Jenkins-telegram-chatops` is a Telegram bot written using [Spring Boot](https://github.com/spring-projects/spring-boot) which handing 
able to show list of Jenkins jobs and run specific job.

## Key features

- `/jobs` command for listing Jenkins jobs
- `/run` command for running specific Jenkins job
- monitoring using [Prometheus](https://prometheus.io)
- holding secrets with [HashiCorp Vault](https://www.vaultproject.io/)


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

## Configuration

According to [Spring Docs](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-application-property-files)
you can override default application properties by put custom **application.properties** file in one of the following
locations:

- a `/config` subdirectory of the current directory
- the current directory

### Custom properties

<dl> 
  <dt>jenkins.url</dt>
  <dd>URL to Jenkins instance</dd>
    
  <dt>jenkins.username</dt>
  <dd>Jenkins username for REST API</dd>
  
  <dt>jenkins.token</dt>
  <dd>Jenkins user`s password or token for REST API</dd>
  
  <dt>jenkins.poolSize</dt>
  <dd>Size of FixedThreadPool</dd>
  
  <dt>telegram.bot.name</dt>
  <dd>telegram bot name</dd>
  
  <dt>telegram.bot.token</dt>
  <dd>telegram bot token</dd>
  
  <dt>telegram.bot.users</dt>
  <dd>coma separated list of bot users telegram IDs</dd>
  
  <dt>telegram.bot.proxyHost</dt>
  <dd>http proxy host</dd>

  <dt>telegram.bot.proxyPort</dt>
  <dd>http proxy port</dd>
  
  <dt>telegram.bot.connectionTimeout</dt>
  <dd>timeout in milliseconds until a connection is established</dd>
  
  <dt>telegram.bot.connectionRequestTimeout</dt>
  <dd>timeout in milliseconds used when requesting a connection</dd>
    
  <dt>telegram.bot.socketTimeout</dt>
  <dd>the socket timeout in milliseconds, which is the timeout for waiting for data  or, put differently, a maximum period inactivity between two consecutive data packets)</dd>
</dl>

### Retryable configuration for checking Jenkins jobs status

When Jenkins job run via REST API `jenkins-telegram-chatops` waits until it finished.
There are three steps :

- wait until job in Jenkins queue
- wait until job not started yet
- wait until job building

Each steps performs REST API calls in `maxAttempts` attempts with some `delay`.

<dl> 
  <dt>jenkins.retry.inqueue.maxAttempts</dt>
  <dd>count max calls for `wait until job in Jenkins queue` </dd>
    
  <dt>jenkins.retry.inqueue.backoff.delay</dt>
  <dd>delay in ms for `wait until job in Jenkins queue`</dd>
  
  <dt>jenkins.retry.notstarted.maxAttempts</dt>
    <dd>count max calls for `wait until job not started yet` </dd>
      
  <dt>jenkins.retry.notstarted.backoff.delay</dt>
  <dd>delay in ms for `wait until job not started yet`</dd>
    
  <dt>jenkins.retry.building.maxAttempts</dt>
  <dd>count max calls for `wait until job building` </dd>
    
  <dt>jenkins.retry.building.backoff.delay</dt>
  <dd>delay in ms for `wait until job building`</dd>
 </dl>

## Telegram bot commands

Telegram bot supports following text commands:

- **_/jobs_** - listing Jenkins jobs
- **_/run_** <<*job name*>> - run <<*job name*>> Jenkins job
- **_/help_** - prints help message

### Running Jenkins jobs

![sequence diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.github.com/MikeSafonov/jenkins-telegram-chatops/master/diagrams/sequence.txt)

## INTEGRATIONS 

### Monitoring using [Prometheus](https://prometheus.io)

You can access prometheus metrics by url: 

    {host:port}/actuator/prometheus

### Holding secrets with [HashiCorp Vault](https://www.vaultproject.io/)

Integration with `Vault` was made using [spring-cloud-vault](https://cloud.spring.io/spring-cloud-vault/reference/html/).

By default `jenkins-telegram-chatops` integration with `Vault` disabled. 

To enable integration with `Vault` pass following arguments to `jenkins-telegram-chatops` run command:

    java -jar jenkins-telegram-chatops.jar --spring.cloud.vault.enabled=true --spring.cloud.vault.uri=<your vault uri> 
    --spring.cloud.vault.token=<your vault token> --spring.cloud.vault.kv.application-name=<vault application name>
 
## Contributing

Feel free to contribute. 
New feature proposals and bug fixes should be submitted as GitHub pull requests. 
Fork the repository on GitHub, prepare your change on your forked copy, and submit a pull request.

**IMPORTANT!**
>Before contributing please read about [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0-beta.2/) / [Conventional Commits RU](https://www.conventionalcommits.org/ru/v1.0.0-beta.2/)
