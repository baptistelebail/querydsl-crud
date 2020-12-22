# QueryDSL Crud 
[![Build Status](https://travis-ci.org/baptistelebail/querydsl-crud.svg?branch=master)](https://travis-ci.org/baptistelebail/querydsl-crud)

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

### [CrudRepository](https://github.com/baptistelebail/querydsl-crud/blob/master/querydsl-crud-sync/src/main/java/com/blebail/querydsl/crud/sync/repository/CrudRepository.java), for identifiable resources
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
```java
// Initialize a repository
SQLQueryFactory sqlQueryFactory = ...
QDSLResource<QAccount, BAccount> accountResource = new QDSLResource<>(QAccount.account);
BaseRepository<BAccount> repository = new QDSLBaseRepository<>(accountResource, sqlQueryFactory);

// A few examples
Optional<BAccount> account = repository.findOne();
Collection<BAccount> accounts = repository.findAll();
Page<BAccount> accountPage = repository.find(new PageRequest(0, 10, List.of(new Sort("username", Sort.Direction.ASC))));
```

The [QDSLBaseRepository](https://github.com/baptistelebail/querydsl-crud/blob/master/querydsl-crud-sync/src/main/java/com/blebail/querydsl/crud/sync/repository/QDSLBaseRepository.java) can also be extended, just pass a [QDSLResource](https://github.com/baptistelebail/querydsl-crud/blob/master/querydsl-crud-commons/src/main/java/com/blebail/querydsl/crud/commons/resource/QDSLResource.java) and a [SQLQueryFactory](http://www.querydsl.com/static/querydsl/4.4.0/apidocs/com/querydsl/sql/SQLQueryFactory.html)

```java
public final class AccountRepository extends QDSLBaseRepository<QAccount, BAccount> {

    public AccountBaseRepository(SQLQueryFactory queryFactory) {
        super(new QDSLResource<>(QAccount.account), queryFactory);
    }
}
```

`AccountRepository` now supports all methods of [BaseRepository](https://github.com/baptistelebail/querydsl-crud/blob/master/querydsl-crud-sync/src/main/java/com/blebail/querydsl/crud/sync/repository/BaseRepository.java).

#### CrudRepository example
```java
// Initialize a repository
SQLQueryFactory sqlQueryFactory = ...
IdentifiableQDSLResource<QAccount, BAccount, String> accountResource = new IdentifiableQDSLResource<>(QAccount.account, QAccount.account.id, BAccount::getId);
CrudRepository<BAccount, String> repository = new QDSLCrudRepository<>(accountResource, sqlQueryFactory);

// A few examples
Optional<BAccount> account = repository.findOne("account1");
boolean exists = repository.exists("account1");
boolean deleted = repository.delete("account1");
```

The [QDSLCrudRepository](https://github.com/baptistelebail/querydsl-crud/blob/master/querydsl-crud-sync/src/main/java/com/blebail/querydsl/crud/sync/repository/QDSLCrudRepository.java) can also be extended, just pass an [IdentifiableQDSLResource](https://github.com/baptistelebail/querydsl-crud/blob/master/querydsl-crud-commons/src/main/java/com/blebail/querydsl/crud/commons/resource/IdentifiableQDSLResource.java) and a [SQLQueryFactory](http://www.querydsl.com/static/querydsl/4.4.0/apidocs/com/querydsl/sql/SQLQueryFactory.html)

```java
public final class AccountRepository extends QDSLCrudRepository<QAccount, BAccount, String> {

    public AccountRepository(SQLQueryFactory queryFactory) {
        super(new IdentifiableQDSLResource<>(QAccount.account, QAccount.account.id, BAccount::getId), queryFactory);
    }
}
```

`AccountRepository` now supports all methods of [CrudRepository](https://github.com/baptistelebail/querydsl-crud/blob/master/querydsl-crud-sync/src/main/java/com/blebail/querydsl/crud/sync/repository/CrudRepository.java).

### Asynchronous API with CompletableFuture

QueryDSL Crud Async provides asynchronous APIs for QueryDSL Crud, with return types being [CompletableFuture](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html).

#### Add the dependency
```xml
   <dependency>
       <groupId>com.blebail.querydsl</groupId>
       <artifactId>querydsl-crud-async</artifactId>
       <version>0.1</version>
   </dependency>
```

#### AsyncBaseRepository Example
```java
// Initialize a repository
SQLQueryFactory sqlQueryFactory = ...
QDSLResource<QAccount, BAccount> accountResource = new QDSLResource<>(QAccount.account);
AsyncBaseRepository<BAccount> repository = new QDSLAsyncBaseRepository<>(accountResource, sqlQueryFactory);

// A few examples
CompletableFuture<Optional<BAccount>> account = repository.findOne();
CompletableFuture<Collection<BAccount>> accounts = repository.findAll();
CompletableFuture<Page<BAccount>> accountPage = repository.find(new PageRequest(0, 10, List.of(new Sort("username", Sort.Direction.ASC))));
```

The [QDSLAsyncBaseRepository](https://github.com/baptistelebail/querydsl-crud/blob/master/querydsl-crud-async/src/main/java/com/blebail/querydsl/crud/async/repository/QDSLAsyncBaseRepository.java) can also be extended, just pass a [QDSLResource](https://github.com/baptistelebail/querydsl-crud/blob/master/querydsl-crud-commons/src/main/java/com/blebail/querydsl/crud/commons/resource/QDSLResource.java) and a [SQLQueryFactory](http://www.querydsl.com/static/querydsl/4.4.0/apidocs/com/querydsl/sql/SQLQueryFactory.html)

```java
public final class AccountRepository extends QDSLAsyncBaseRepository<QAccount, BAccount> {

    public AccountBaseRepository(SQLQueryFactory queryFactory) {
        super(new QDSLResource<>(QAccount.account), queryFactory);
    }
}
```

`AccountRepository` now supports all methods of [AsyncBaseRepository](https://github.com/baptistelebail/querydsl-crud/blob/master/querydsl-crud-async/src/main/java/com/blebail/querydsl/crud/async/repository/AsyncBaseRepository.java).

#### AsyncCrudRepository example
```java
// Initialize a repository
SQLQueryFactory sqlQueryFactory = ...
IdentifiableQDSLResource<QAccount, BAccount, String> accountResource = new IdentifiableQDSLResource<>(QAccount.account, QAccount.account.id, BAccount::getId);
AsyncCrudRepository<BAccount, String> repository = new QDSLAsyncCrudRepository<>(accountResource, sqlQueryFactory);

// A few examples
CompletableFuture<Optional<BAccount>> account = repository.findOne("account1");
CompletableFuture<Boolean> exists = repository.exists("account1");
CompletableFuture<Boolean> deleted = repository.delete("account1");
```

The [QDSLAsyncCrudRepository](https://github.com/baptistelebail/querydsl-crud/blob/master/querydsl-crud-async/src/main/java/com/blebail/querydsl/crud/async/repository/QDSLAsyncCrudRepository.java) can also be extended, just pass an [IdentifiableQDSLResource](https://github.com/baptistelebail/querydsl-crud/blob/master/querydsl-crud-commons/src/main/java/com/blebail/querydsl/crud/commons/resource/IdentifiableQDSLResource.java) and a [SQLQueryFactory](http://www.querydsl.com/static/querydsl/4.4.0/apidocs/com/querydsl/sql/SQLQueryFactory.html)

```java
public final class AccountRepository extends QDSLAsyncCrudRepository<QAccount, BAccount, String> {

    public AccountRepository(SQLQueryFactory queryFactory) {
        super(new IdentifiableQDSLResource<>(QAccount.account, QAccount.account.id, BAccount::getId), queryFactory);
    }
}
```

`AccountRepository` now supports all methods of [AsyncCrudRepository](https://github.com/baptistelebail/querydsl-crud/blob/master/querydsl-crud-async/src/main/java/com/blebail/querydsl/crud/async/repository/AsyncCrudRepository.java).

#### Custom Executor

By default, [QDSLAsyncBaseRepository](https://github.com/baptistelebail/querydsl-crud/blob/master/querydsl-crud-async/src/main/java/com/blebail/querydsl/crud/async/repository/QDSLAsyncBaseRepository.java) and [QDSLAsyncCrudRepository](https://github.com/baptistelebail/querydsl-crud/blob/master/querydsl-crud-async/src/main/java/com/blebail/querydsl/crud/async/repository/QDSLAsyncCrudRepository.java) use a thread pool of fixed sized 2 (`Executors.newFixedThreadPool(2)`), but a custom [Executor](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executor.html) can be passed through their constructor, such as:
```java
SQLQueryFactory sqlQueryFactory = ...
Executor executor = ...

AsyncBaseRepository<BAccount> repository = new QDSLAsyncBaseRepository<>(accountResource, sqlQueryFactory, executor);

AsyncCrudRepository<BAccount, String> repository = new QDSLAsyncCrudRepository<>(accountResource, sqlQueryFactory, executor);
```
or via inheritance:
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

## Technical Stack
* [Java 11](https://jdk.java.net/11/)
* [Maven](https://maven.apache.org/)
* [JUnit 5](https://junit.org/junit5/)
* [Querydsl 4](http://www.querydsl.com/)
* [Reactor 3](https://projectreactor.io/)
