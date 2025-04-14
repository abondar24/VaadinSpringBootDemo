# ArticleManager

Small article management system based on SpringBoot, Vaadin and Neo4j

## Build and run



### Run the app

- Just an app without infrastructure
```
gradle clean build

java -jar build/libs/artileManager-1.0-SNAPSHOT.jar
```

- App with all required infrastructure running

```
gradle clean build

gradlewbootRun
```
### Manually start/stop infrastructre
```
gradle composeUp

gradle composeDown
```


### Integration tests
```
gradle clean integrationTest
```

## Access
App available under localhost:9080