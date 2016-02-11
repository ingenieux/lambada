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

## TODO

  * Support Aliases
  * Augment with Reproduceable Jars
  * shade the lambada-maven-plugin
  * Implement ApiGateway support properly

