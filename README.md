# QueryDSL Crud 
[![Build Status](https://travis-ci.org/baptistelebail/querysql-crud.svg?branch=master)](https://travis-ci.org/baptistelebail/querysql-crud)

Lightweight library provising common CRUD repositories on top of [QueryDSL](http://www.querydsl.com/).

## Supported operations
### For unidentifiable resources
- `findAll()` (finds all resources)
- `find(Predicate)` (finds resources matching a [Predicate](http://www.querydsl.com/static/querydsl/4.4.0/apidocs/com/querydsl/core/types/Predicate.html))
- `find(PageRequest)` (finds a resources page according to the [PageRequest]() )
- `find(Predicate, PageRequest)` (finds a resources page matching a [Predicate](http://www.querydsl.com/static/querydsl/4.4.0/apidocs/com/querydsl/core/types/Predicate.html) and according to the [PageRequest]())
- `findOne(Predicate)` (finds one resource matching a [Predicate](http://www.querydsl.com/static/querydsl/4.4.0/apidocs/com/querydsl/core/types/Predicate.html))
- `count(Predicate)` (counts resources matching a [Predicate](http://www.querydsl.com/static/querydsl/4.4.0/apidocs/com/querydsl/core/types/Predicate.html))
- `count()` (counts all resources)
- `delete(Predicate)` (deletes resources matching a [Predicate](http://www.querydsl.com/static/querydsl/4.4.0/apidocs/com/querydsl/core/types/Predicate.html))
- `deleteAll()` (deletes all resources)

### For identifiable resources 
All of the above operations, plus the following (R being the resource type and ID the type of it's id):
- `save(R)` (creates or updates a resource)
- `save(Collection<R>)` (creates or updates several resources)
- `find(Collection<ID>)` (find resources by ids)
- `exists(ID)` (check if a resouce exists for an id)
- `findOne(ID)` (finds a resource by id)
- `delete(ID)` (deletes a resource by id)
- `delete(Collection<ID>)` (deletes resources by ids)

## How to use
### Add the repository
```xml
<repositories>
    <repository>
        <id>blebail-repository</id>
        <url>http://blebail.com/repository/</url>
    </repository>   
</repositories>
```
### Synchronous API
```xml
    <dependency>
        <groupId>com.blebail.querydsl</groupId>
        <artifactId>querydsl-crud-sync</artifactId>
        <version>0.1</version>
    </dependency>
```

### Asynchronous API with CompletableFuture
```xml
   <dependency>
       <groupId>com.blebail.querydsl</groupId>
       <artifactId>querydsl-crud-async</artifactId>
       <version>0.1</version>
   </dependency>
```

## Contribution

### Technical Stack
* [Java 11](https://jdk.java.net/11/)
* [Maven](https://maven.apache.org/)
* [JUnit 5](https://junit.org/junit5/)
* [Querydsl 4](http://www.querydsl.com/)
* [Reactor 3](https://projectreactor.io/)