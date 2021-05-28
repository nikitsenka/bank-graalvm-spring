# About
Sample bank application witten in reactive Spring Webflux and packaged as GraalVM native executable. 

## Build

This project has been configured to let you generate a lightweight container running a native executable.
Docker should be installed and configured on your machine prior to creating the image, see [the Getting Started section of the reference guide](https://docs.spring.io/spring-native/docs/0.10.0-SNAPSHOT/reference/htmlsingle/#getting-started-buildpacks).
### Install GraalVM
The easiest way to install and manage multiple java machines is using [sdkman](https://sdkman.io/)

```
sdk use java 21.1.0.r11-grl
gu install native-image
 
```
### Create Image
To create the image, run the following goal:

```
./gradlew bootBuildImage
```

## Run
### Dev mode
```
./gradlew bootRun
```
### Prod mode
```
docker run --rm demo:0.0.1-SNAPSHOT
```
