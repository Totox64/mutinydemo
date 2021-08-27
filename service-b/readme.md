# MicroProfile generated Application

## Introduction

MicroProfile Starter has generated this MicroProfile application for you containing some endpoints which are called from the main application (see the `service-a` directory)

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Packaging and running the application

If you want to build an _??ber-jar_, execute the following command:

    mvn package -Dquarkus.package.type=uber-jar

To run the application:

    java -Dquarkus.http.port=8180 -jar target/mutinydemo-runner.jar

The application can be also packaged using simple:

    mvn package

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it is not an _??ber-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

## Creating a native executable

Mind having GRAALVM_HOME set to your Mandrel or GraalVM installation.

You can create a native executable using:

    mvn package -Dquarkus.package.type=native

Or, if you don't have [Mandrel](https://github.com/graalvm/mandrel/releases/) or
[GraalVM](https://github.com/graalvm/graalvm-ce-builds/releases) installed, you can run the native executable
build in a container using:

    mvn package -Dquarkus.package.type=native -Dquarkus.native.container-build=true

Or to use Mandrel distribution:

    mvn package -Dquarkus.package.type=native -Dquarkus.native.container-build=true -Dquarkus.native.builder-image=quay.io/quarkus/ubi-quarkus-mandrel:20.3-java11

You can then execute your native executable with:

    ./target/mutinydemo-runner -Dquarkus.http.port=8180

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image.

## Specification examples


### Rest Client
A type safe invocation of HTTP rest endpoints. Specification [here](https://microprofile.io/project/eclipse/microprofile-rest-client)
The example calls one endpoint from another JAX-RS resource where generated Rest Client is injected as CDI bean.
