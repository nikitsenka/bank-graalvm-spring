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
## Run DB locally

```
docker build --no-cache -t bank-postgres-db ./docker/db/
docker run --name bank-postgres-db -p 5432:5432 -e POSTGRES_PASSWORD=postgres -d bank-postgres-db
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
### Test
Create client
```
 curl -X POST http://localhost:8080/client/new/100 
```
Get balance
```
 curl -X GET http://localhost:8080/client/0/balance 
```
Add Money
```
 curl -H "Content-type: application/json" -X POST http://localhost:8080/transaction  -d '{"amount":1000,"from_client_id":0,"to_client_id":1}'
```
