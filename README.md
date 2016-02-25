[![ZenHub](https://raw.githubusercontent.com/ZenHubIO/support/master/zenhub-badge.png)](https://zenhub.io)
[![Join the chat at https://gitter.im/ingenieux/lambada](https://badges.gitter.im/ingenieux/lambada.svg)](https://gitter.im/ingenieux/lambada?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# Lambada: AWS Lambda for Silly People

Lambada is a platform for writing [AWS Lambda](https://aws.amazon.com/lambda/) functions using [Apache Maven](http://maven.apache.org/) and [Java](http://java.com/)

## Points of Interest:

  * We use [gitter](https://gitter.im/ingenieux/lambada/) as our chatroom. Please join us, everyone is welcome :)
  * For updates, join our [Mailing List](https://groups.google.com/d/forum/lambada-users)
  * We also use [ZenHub](https://zenhub.io/), so if you'd like to have deeper insight into our development process, please install its Chrome extension and visit any of our repository boards.

## Docs

  * http://lambada.ingenieux.com.br/lambada-maven-plugin/
  * http://lambada.ingenieux.com.br/lambada-runtime/
  * http://lambada.ingenieux.com.br/lambada-invoker/
  * http://lambada.ingenieux.com.br/lambada-testing/

## Installation

```
$ mvn install
```

## Usage

See lambada-example. Some ideas:

```
$ mvn -Pdeploy deploy
$ mvn lambada:serve (builds a server on localhost using the @ApiGateway)
```

