# QueryDSL Crud 
[![Build Status](https://travis-ci.org/baptistelebail/querysql-crud.svg?branch=master)](https://travis-ci.org/baptistelebail/querysql-crud)

Lightweight library allowing to quickly create repositories providing common CRUD operations on top of [QueryDSL](http://www.querydsl.com/).

## Supported operations

QueryDSL Crud has two repository interfaces.

### [BaseRepository](https://github.com/baptistelebail/querydsl-crud/blob/master/querydsl-crud-sync/src/main/java/com/blebail/querydsl/crud/sync/repository/BaseRepository.java), for unidentifiable resources
- `findAll()` (finds all resources)
- `find(Predicate)` (finds resources matching a [Predicate](http://www.querydsl.com/static/querydsl/4.4.0/apidocs/com/querydsl/core/types/Predicate.html))
- `find(PageRequest)` (finds a resources page according to the [PageRequest]() )
- `find(Predicate, PageRequest)` (finds a resources page matching a [Predicate](http://www.querydsl.com/static/querydsl/4.4.0/apidocs/com/querydsl/core/types/Predicate.html) and according to the [PageRequest]())
- `findOne(Predicate)` (finds one resource matching a [Predicate](http://www.querydsl.com/static/querydsl/4.4.0/apidocs/com/querydsl/core/types/Predicate.html))
- `count(Predicate)` (counts resources matching a [Predicate](http://www.querydsl.com/static/querydsl/4.4.0/apidocs/com/querydsl/core/types/Predicate.html))
- `count()` (counts all resources)
- `delete(Predicate)` (deletes resources matching a [Predicate](http://www.querydsl.com/static/querydsl/4.4.0/apidocs/com/querydsl/core/types/Predicate.html))
- `deleteAll()` (deletes all resources)

### [CrudRepository](https://github.com/baptistelebail/querydsl-crud/blob/feature/sync/querydsl-crud-sync/src/main/java/com/blebail/querydsl/crud/sync/repository/CrudRepository.java), for identifiable resources
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
#### Add the dependency
```xml
    <dependency>
        <groupId>com.blebail.querydsl</groupId>
        <artifactId>querydsl-crud-sync</artifactId>
        <version>0.1</version>
    </dependency>
```

#### BaseRepository Example

The repository needs to extend [QDSLBaseRepository](https://github.com/baptistelebail/querydsl-crud/blob/master/querydsl-crud-sync/src/main/java/com/blebail/querydsl/crud/sync/repository/QDSLBaseRepository.java), pass a [QDSLResource](https://github.com/baptistelebail/querydsl-crud/blob/master/querydsl-crud-commons/src/main/java/com/blebail/querydsl/crud/commons/resource/QDSLResource.java) and the [SQLQueryFactory](http://www.querydsl.com/static/querydsl/4.4.0/apidocs/com/querydsl/sql/SQLQueryFactory.html)

```java
public final class AccountRepository extends QDSLBaseRepository<QAccount, BAccount> {

    public AccountBaseRepository(SQLQueryFactory queryFactory) {
        super(new QDSLResource<>(QAccount.account), queryFactory);
    }
}
```

`AccountRepository` now supports all methods of [BaseRepository](https://github.com/baptistelebail/querydsl-crud/blob/master/querydsl-crud-sync/src/main/java/com/blebail/querydsl/crud/sync/repository/BaseRepository.java).

#### CrudRepository example

The repository needs to extend [QDSLCrudRepository](https://github.com/baptistelebail/querydsl-crud/blob/master/querydsl-crud-sync/src/main/java/com/blebail/querydsl/crud/sync/repository/QDSLCrudRepository.java), pass an [IdentifiableQDSLResource](https://github.com/baptistelebail/querydsl-crud/blob/master/querydsl-crud-commons/src/main/java/com/blebail/querydsl/crud/commons/resource/IdentifiableQDSLResource.java) and the [SQLQueryFactory](http://www.querydsl.com/static/querydsl/4.4.0/apidocs/com/querydsl/sql/SQLQueryFactory.html)

```java
public final class AccountRepository extends QDSLCrudRepository<QAccount, BAccount, String> {

    public AccountRepository(SQLQueryFactory queryFactory) {
        super(new IdentifiableQDSLResource<>(QAccount.account, QAccount.account.id, BAccount::getId), queryFactory);
    }
}
```

`AccountRepository` now supports all methods of [CrudRepository](https://github.com/baptistelebail/querydsl-crud/blob/master/querydsl-crud-sync/src/main/java/com/blebail/querydsl/crud/sync/repository/CrudRepository.java).

### Asynchronous API with CompletableFuture
#### Add the dependency
```xml
   <dependency>
       <groupId>com.blebail.querydsl</groupId>
       <artifactId>querydsl-crud-async</artifactId>
       <version>0.1</version>
   </dependency>
```

#### AsyncBaseRepository Example

The repository needs to extend [QDSLAsyncBaseRepository](hhttps://github.com/baptistelebail/querydsl-crud/blob/master/querydsl-crud-async/src/main/java/com/blebail/querydsl/crud/async/repository/QDSLAsyncBaseRepository.java), pass a [QDSLResource](https://github.com/baptistelebail/querydsl-crud/blob/master/querydsl-crud-commons/src/main/java/com/blebail/querydsl/crud/commons/resource/QDSLResource.java) and the [SQLQueryFactory](http://www.querydsl.com/static/querydsl/4.4.0/apidocs/com/querydsl/sql/SQLQueryFactory.html)

```java
public final class AccountRepository extends QDSLAsyncBaseRepository<QAccount, BAccount> {

    public AccountBaseRepository(SQLQueryFactory queryFactory) {
        super(new QDSLResource<>(QAccount.account), queryFactory);
    }
}
```

`AccountRepository` now supports all methods of [AsyncBaseRepository](https://github.com/baptistelebail/querydsl-crud/blob/master/querydsl-crud-async/src/main/java/com/blebail/querydsl/crud/async/repository/AsyncBaseRepository.java).

#### AsyncCrudRepository example

The repository needs to extend [QDSLAsyncCrudRepository](hhttps://github.com/baptistelebail/querydsl-crud/blob/master/querydsl-crud-async/src/main/java/com/blebail/querydsl/crud/async/repository/QDSLAsyncCrudRepository.java), pass an [IdentifiableQDSLResource](https://github.com/baptistelebail/querydsl-crud/blob/master/querydsl-crud-commons/src/main/java/com/blebail/querydsl/crud/commons/resource/IdentifiableQDSLResource.java) and the [SQLQueryFactory](http://www.querydsl.com/static/querydsl/4.4.0/apidocs/com/querydsl/sql/SQLQueryFactory.html)

```java
public final class AccountRepository extends QDSLAsyncCrudRepository<QAccount, BAccount, String> {

    public AccountRepository(SQLQueryFactory queryFactory) {
        super(new IdentifiableQDSLResource<>(QAccount.account, QAccount.account.id, BAccount::getId), queryFactory);
    }
}
```

`AccountRepository` now supports all methods of [AsyncCrudRepository](https://github.com/baptistelebail/querydsl-crud/blob/master/querydsl-crud-async/src/main/java/com/blebail/querydsl/crud/async/repository/AsyncCrudRepository.java).

#### Custom Executor

By default, [QDSLAsyncBaseRepository](hhttps://github.com/baptistelebail/querydsl-crud/blob/master/querydsl-crud-async/src/main/java/com/blebail/querydsl/crud/async/repository/QDSLAsyncBaseRepository.java) and [QDSLAsyncCrudRepository](hhttps://github.com/baptistelebail/querydsl-crud/blob/master/querydsl-crud-async/src/main/java/com/blebail/querydsl/crud/async/repository/QDSLAsyncCrudRepository.java) use a thread pool of fixed sized 2 (`Executors.newFixedThreadPool(2)`), but a custom [Executor](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executor.html) can be passed through their second constructor, such as:
```java
public final class AccountRepository extends QDSLAsyncBaseRepository<QAccount, BAccount> {

    public AccountRepository(SQLQueryFactory queryFactory, Executor executor) {
        super(new QDSLResource<>(QAccount.account), queryFactory, executor);
    }
}
```
or
```java
public final class AccountRepository extends QDSLAsyncCrudRepository<QAccount, BAccount, String> {

    public AccountRepository(SQLQueryFactory queryFactory, Executor executor) {
        super(new IdentifiableQDSLResource<>(QAccount.account, QAccount.account.id, BAccount::getId), queryFactory, executor);
    }
}
```

## Contribution

### Technical Stack
* [Java 11](https://jdk.java.net/11/)
* [Maven](https://maven.apache.org/)
* [JUnit 5](https://junit.org/junit5/)
* [Querydsl 4](http://www.querydsl.com/)
* [Reactor 3](https://projectreactor.io/)