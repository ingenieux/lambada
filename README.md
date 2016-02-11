# Lambada

## AWS Lambda for Silly People

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

## Docs

  * http://lambada.ingenieux.com.br/lambada-maven-plugin/
  * http://lambada.ingenieux.com.br/lambada-runtime/
  * http://lambada.ingenieux.com.br/lambada-invoker/
  * http://lambada.ingenieux.com.br/lambada-testing/

## TODO

  * Support Aliases
  * Augment with Reproduceable Jars
  * shade the lambada-maven-plugin
  * Implement ApiGateway support properly

