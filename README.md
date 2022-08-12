# Pet-project to show n + 1 Hibernate problem

### General information
* Project contains several branches. Each branch contains implementation of a way to avoid n + 1 problem
* All tests could be provided within NPlusOneProblemApplicationTests test class
* Each part of this description refer to an appropriate branch name

### How to use the project
* Start database servers. Just run **docker-compose up -d** and 2 database instances the MySql and the MS-SQL will start
* To switch between databases uncomment and comment appropriate parts of *application.yml* config file
* After databases start create ***n_plus_one_demo_db*** for the MS-SQL instance. MySql engine will create it from the JDBC connection string
* Run a test method in debug mode. The schema will be created within Liquibase unless it switched off in config or by override value above the test class

> Important!!!
> * The MS-SQL schema is differ from the MySql one. The MS-MS schema uses Long value as an id when the MySql uses UUID for this purpose.
> * The only branch which is fully implemented for the MySql usage is the ***persistent-failure-fixed-mysql*** one!!!

---

## What is n + 1 problem itself?

See the description [here](https://medium.com/geekculture/resolve-hibernate-n-1-problem-f0e049e689ab)
> The N+1 query problem is said to occur when an ORM, like hibernate, executes 1 query to retrieve the parent entity and N queries to retrieve the child entities. As the number of entities in the database increases, the queries being executed separately can easily affect the performance of the application.

### How to see this in practice
Switch to the *master* branch of the schema and the shouldFetchAllTopics() method of the NPlusOneProblemApplicationTests class in debug mode and you will see the output like this:
```
2022-08-12 16:53:12.414 DEBUG 29188 --- [           main] org.hibernate.SQL                        : select topic0_.id as id1_2_, topic0_.topic_name as topic_na2_2_ from topic topic0_
Hibernate: select topic0_.id as id1_2_, topic0_.topic_name as topic_na2_2_ from topic topic0_
2022-08-12 16:53:12.429 DEBUG 29188 --- [           main] org.hibernate.SQL                        : select comments0_.topic_id as topic_id3_0_0_, comments0_.id as id1_0_0_, comments0_.id as id1_0_1_, comments0_.comment_text as comment_2_0_1_, comments0_.topic_id as topic_id3_0_1_ from comment comments0_ where comments0_.topic_id=?
Hibernate: select comments0_.topic_id as topic_id3_0_0_, comments0_.id as id1_0_0_, comments0_.id as id1_0_1_, comments0_.comment_text as comment_2_0_1_, comments0_.topic_id as topic_id3_0_1_ from comment comments0_ where comments0_.topic_id=?
2022-08-12 16:53:12.430 TRACE 29188 --- [           main] o.h.type.descriptor.sql.BasicBinder      : binding parameter [1] as [BIGINT] - [1]
2022-08-12 16:53:12.439 DEBUG 29188 --- [           main] org.hibernate.SQL                        : select advertisem0_.topic_id as topic_id3_1_0_, advertisem0_.id as id1_1_0_, advertisem0_.id as id1_1_1_, advertisem0_.ad_banner_url as ad_banne2_1_1_, advertisem0_.topic_id as topic_id3_1_1_ from targeted_ads advertisem0_ where advertisem0_.topic_id=?
Hibernate: select advertisem0_.topic_id as topic_id3_1_0_, advertisem0_.id as id1_1_0_, advertisem0_.id as id1_1_1_, advertisem0_.ad_banner_url as ad_banne2_1_1_, advertisem0_.topic_id as topic_id3_1_1_ from targeted_ads advertisem0_ where advertisem0_.topic_id=?
2022-08-12 16:53:12.439 TRACE 29188 --- [           main] o.h.type.descriptor.sql.BasicBinder      : binding parameter [1] as [BIGINT] - [1]

Comments size: 10 ads size: 2

2022-08-12 16:53:12.444 DEBUG 29188 --- [           main] org.hibernate.SQL                        : select comments0_.topic_id as topic_id3_0_0_, comments0_.id as id1_0_0_, comments0_.id as id1_0_1_, comments0_.comment_text as comment_2_0_1_, comments0_.topic_id as topic_id3_0_1_ from comment comments0_ where comments0_.topic_id=?
Hibernate: select comments0_.topic_id as topic_id3_0_0_, comments0_.id as id1_0_0_, comments0_.id as id1_0_1_, comments0_.comment_text as comment_2_0_1_, comments0_.topic_id as topic_id3_0_1_ from comment comments0_ where comments0_.topic_id=?
2022-08-12 16:53:12.444 TRACE 29188 --- [           main] o.h.type.descriptor.sql.BasicBinder      : binding parameter [1] as [BIGINT] - [2]
2022-08-12 16:53:12.446 DEBUG 29188 --- [           main] org.hibernate.SQL                        : select advertisem0_.topic_id as topic_id3_1_0_, advertisem0_.id as id1_1_0_, advertisem0_.id as id1_1_1_, advertisem0_.ad_banner_url as ad_banne2_1_1_, advertisem0_.topic_id as topic_id3_1_1_ from targeted_ads advertisem0_ where advertisem0_.topic_id=?
Hibernate: select advertisem0_.topic_id as topic_id3_1_0_, advertisem0_.id as id1_1_0_, advertisem0_.id as id1_1_1_, advertisem0_.ad_banner_url as ad_banne2_1_1_, advertisem0_.topic_id as topic_id3_1_1_ from targeted_ads advertisem0_ where advertisem0_.topic_id=?
2022-08-12 16:53:12.446 TRACE 29188 --- [           main] o.h.type.descriptor.sql.BasicBinder      : binding parameter [1] as [BIGINT] - [2]

Comments size: 5 ads size: 3
```
So, each call of getComments() and getAdvertisements() methods are causes and external call to the database.


**The Hibernate introduces several ways how to fix that.**

---

## The first way is using EAGER FetchType parameter

Switch to the ***fetch-type-eager*** git branch.

Diff between this branch and master branch will show that the Topic class @OneToMany annotation was modified and the parameter fetch has been added.
```java
@OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
private List<Comment> comments;

@OneToMany(mappedBy = "topic", cascade = CascadeType.ALL/*, fetch = FetchType.EAGER*/)
private List<TargetedAdvertisement> advertisements;
```

After the shouldFetchAllTopics() method execution you will see the output which is a bit changed.
The getComment() method now doesn't trigger the Hibernate to fetch this collection from the MS-SQL database when the getAdvertisements() method invocation it does. 

Limitations of this way are:
* You can't use EAGER fetch for a several collections declared in an Entity (Topic)
* This still isn't the solution due to Hibernate makes a set of calls in order to fetch a nested EAGER fetched collection

---

## The second way is using @LazyCollection(LazyCollectionOption.FALSE) above nested collection

Switch to the ***lazy-collection-annotation*** git branch.

Now the Topic class looks like:
```java
@OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
@LazyCollection(LazyCollectionOption.FALSE)
private List<Comment> comments;

@OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
@LazyCollection(LazyCollectionOption.FALSE)
private List<TargetedAdvertisement> advertisements;
```

Actually it acts like the EAGER FetchType parameter but for multiple nested collections (see code snippet above)
This way allows to bypass the EAGER limitation but still performs too slow.

---

## The third way is using @BatchSize annotation above the nested collection

One of the best ways to handle nested collections, in my opinion.
Switch to the ***batch-size*** git branch. The code snippet is looks like:
```java
@OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
@BatchSize(size = 1000)
private List<Comment> comments;

@OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
@BatchSize(size = 1000)
private List<TargetedAdvertisement> advertisements;
```

How it works. When you invoke a nested collection getter Hibernate fetches the collection entities (Comment) for the N parent entities (1000 for the Topic).
The output looks like:
```
2022-08-12 17:20:39.702 DEBUG 30797 --- [           main] org.hibernate.SQL                        : select topic0_.id as id1_2_, topic0_.topic_name as topic_na2_2_ from topic topic0_
Hibernate: select topic0_.id as id1_2_, topic0_.topic_name as topic_na2_2_ from topic topic0_
2022-08-12 17:20:46.493 DEBUG 30797 --- [           main] org.hibernate.SQL                        : select comments0_.topic_id as topic_id3_0_1_, comments0_.id as id1_0_1_, comments0_.id as id1_0_0_, comments0_.comment_text as comment_2_0_0_, comments0_.topic_id as topic_id3_0_0_ from comment comments0_ where comments0_.topic_id in (?, ?, ?, ?, ?)
Hibernate: select comments0_.topic_id as topic_id3_0_1_, comments0_.id as id1_0_1_, comments0_.id as id1_0_0_, comments0_.comment_text as comment_2_0_0_, comments0_.topic_id as topic_id3_0_0_ from comment comments0_ where comments0_.topic_id in (?, ?, ?, ?, ?)
2022-08-12 17:20:46.502 TRACE 30797 --- [           main] o.h.type.descriptor.sql.BasicBinder      : binding parameter [1] as [BIGINT] - [1]
2022-08-12 17:20:46.502 TRACE 30797 --- [           main] o.h.type.descriptor.sql.BasicBinder      : binding parameter [2] as [BIGINT] - [2]
2022-08-12 17:20:46.503 TRACE 30797 --- [           main] o.h.type.descriptor.sql.BasicBinder      : binding parameter [3] as [BIGINT] - [3]
2022-08-12 17:20:46.503 TRACE 30797 --- [           main] o.h.type.descriptor.sql.BasicBinder      : binding parameter [4] as [BIGINT] - [4]
2022-08-12 17:20:46.504 TRACE 30797 --- [           main] o.h.type.descriptor.sql.BasicBinder      : binding parameter [5] as [BIGINT] - [5]
2022-08-12 17:20:48.799 DEBUG 30797 --- [           main] org.hibernate.SQL                        : select advertisem0_.topic_id as topic_id3_1_1_, advertisem0_.id as id1_1_1_, advertisem0_.id as id1_1_0_, advertisem0_.ad_banner_url as ad_banne2_1_0_, advertisem0_.topic_id as topic_id3_1_0_ from targeted_ads advertisem0_ where advertisem0_.topic_id in (?, ?, ?, ?, ?)
Hibernate: select advertisem0_.topic_id as topic_id3_1_1_, advertisem0_.id as id1_1_1_, advertisem0_.id as id1_1_0_, advertisem0_.ad_banner_url as ad_banne2_1_0_, advertisem0_.topic_id as topic_id3_1_0_ from targeted_ads advertisem0_ where advertisem0_.topic_id in (?, ?, ?, ?, ?)
2022-08-12 17:20:48.801 TRACE 30797 --- [           main] o.h.type.descriptor.sql.BasicBinder      : binding parameter [1] as [BIGINT] - [1]
2022-08-12 17:20:48.801 TRACE 30797 --- [           main] o.h.type.descriptor.sql.BasicBinder      : binding parameter [2] as [BIGINT] - [2]
2022-08-12 17:20:48.802 TRACE 30797 --- [           main] o.h.type.descriptor.sql.BasicBinder      : binding parameter [3] as [BIGINT] - [3]
2022-08-12 17:20:48.802 TRACE 30797 --- [           main] o.h.type.descriptor.sql.BasicBinder      : binding parameter [4] as [BIGINT] - [4]
2022-08-12 17:20:48.802 TRACE 30797 --- [           main] o.h.type.descriptor.sql.BasicBinder      : binding parameter [5] as [BIGINT] - [5]

Comments size: 10 ads size: 2
Comments size: 5 ads size: 3
Comments size: 3 ads size: 3
Comments size: 7 ads size: 5
Comments size: 4 ads size: 1
```

---

## The fourth way is using JOIN FETCH clause in a JPQL query

Switch to the ***join-fetch*** git branch.

Now we don't modify entities but instead of that TopicRepository was enriched with the extra method and looks like:

```java
public interface TopicRepository extends JpaRepository<Topic, Long> {
    String ALL_TOPICS_EAGER_QUERY = """
        select distinct t from Topic t
          join fetch t.comments
          join fetch t.advertisements
    """;

    @Query(ALL_TOPICS_EAGER_QUERY)
    List<Topic> findAllFetched();
}
```

The output now looks like:
```
2022-08-12 17:25:54.505 DEBUG 31088 --- [           main] org.hibernate.SQL                        : select distinct topic0_.id as id1_2_0_, comments1_.id as id1_0_1_, advertisem2_.id as id1_1_2_, topic0_.topic_name as topic_na2_2_0_, comments1_.comment_text as comment_2_0_1_, comments1_.topic_id as topic_id3_0_1_, comments1_.topic_id as topic_id3_0_0__, comments1_.id as id1_0_0__, advertisem2_.ad_banner_url as ad_banne2_1_2_, advertisem2_.topic_id as topic_id3_1_2_, advertisem2_.topic_id as topic_id3_1_1__, advertisem2_.id as id1_1_1__ from topic topic0_ inner join comment comments1_ on topic0_.id=comments1_.topic_id inner join targeted_ads advertisem2_ on topic0_.id=advertisem2_.topic_id
Hibernate: select distinct topic0_.id as id1_2_0_, comments1_.id as id1_0_1_, advertisem2_.id as id1_1_2_, topic0_.topic_name as topic_na2_2_0_, comments1_.comment_text as comment_2_0_1_, comments1_.topic_id as topic_id3_0_1_, comments1_.topic_id as topic_id3_0_0__, comments1_.id as id1_0_0__, advertisem2_.ad_banner_url as ad_banne2_1_2_, advertisem2_.topic_id as topic_id3_1_2_, advertisem2_.topic_id as topic_id3_1_1__, advertisem2_.id as id1_1_1__ from topic topic0_ inner join comment comments1_ on topic0_.id=comments1_.topic_id inner join targeted_ads advertisem2_ on topic0_.id=advertisem2_.topic_id

Comments size: 10 ads size: 2
Comments size: 5 ads size: 3
Comments size: 3 ads size: 3
Comments size: 7 ads size: 5
Comments size: 4 ads size: 1
```

As you see the Hibernate makes just a single call to fetch all the records chained logically.<br><br>

> **The tremendous disadvantage of this way is that the query generated for a several collections will produce cartesian product
> for multiple nested collections.**<br>
 
What does it mean? See the JQPL query below and the appropriate SQL query generated by Hibernate.

JPQL query
```hql
select t from Topic t
  join fetch t.comments
  join fetch t.advertisements
where t.id = 1
```
Vanilla SQL query:
```sql
select * from topic t
  join comment c on t.id = c.topic_id
  join targeted_ads ta on t.id = ta.topic_id
where t.id = 1;
```
Based on data uploaded by liquibase in this project execution of vanilla SQL query will return 20 rows for the topic with id=1
instead of 1 row for the Topic entity, 2 rows for the TargetAdvertisement entity, and 10 rows for the Comment entity.
<br>
But what about Hibernate? When you try to use topicRepository.findAllFetched() it will fail with exception like:
```
Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'topicRepository' defined in com.solbeg.nplusoneproblem.dao.TopicRepository defined in @EnableJpaRepositories declared on JpaRepositoriesRegistrar.EnableJpaRepositoriesConfiguration: Invocation of init method failed; nested exception is org.springframework.data.repository.query.QueryCreationException: Could not create query for public abstract java.util.List com.solbeg.nplusoneproblem.dao.TopicRepository.findAllFetched(); Reason: Validation failed for query for method public abstract java.util.List com.solbeg.nplusoneproblem.dao.TopicRepository.findAllFetched()!; nested exception is java.lang.IllegalArgumentException: Validation failed for query for method public abstract java.util.List com.solbeg.nplusoneproblem.dao.TopicRepository.findAllFetched()!
...
Caused by: org.hibernate.loader.MultipleBagFetchException: cannot simultaneously fetch multiple bags: [com.solbeg.nplusoneproblem.entity.Topic.comments, com.solbeg.nplusoneproblem.entity.Topic.advertisements]
```

How to fix this? Use Set for the nested collection and distinct keyword for the JPQL query.
Actually, Hibernate will passthrough the "distinct" keyword to the DB engine, what doesn't make sense.<br>
If you want to see how to exclude distinct keyword from the Hibernate query just check the shouldFetchAllTopicsUsingEntityManager() test method.

You may want to read more info [here](https://stackoverflow.com/questions/4334970/hibernate-throws-multiplebagfetchexception-cannot-simultaneously-fetch-multipl/51055523?stw=2#51055523)

Summarizing. Avoid using join fetch method for joining multiple collection in the parent entity unless you know what are you doing. 
It affects performance or may cause unpredictable behaviour of your application.

---

## Bonus part hidden n + 1 problem while persisting huge amount of entities. Persistent performance improvement.

There are 3 branches related to this problem:
* persistence-failure
* persistence-failure-fixed-ms-sql
* persistence-failure-fixed-my-sql

The persistence-failure branch is almost the same as the master one, but with extra tests added. Run all the tests, and you will see that some of them fail with an exception.
Rest of branches contain fixes for appropriate DB engine and improvements persistence performance.<br>
Both fixed branches have extra config with external proxy which allows to view how is the Hibernate deals with batch insert.
<br><br>
To read more about ideas and implementation please visit
* [Batch Insert/Update with Hibernate/JPA](https://www.baeldung.com/jpa-hibernate-batch-insert-update)
* [An Overview of Identifiers in Hibernate/JPA](https://www.baeldung.com/hibernate-identifiers)

What to check while playing with these branches
* Exceptions that Hibernate produces and how to fix them
* Performance of the shouldPersistHugeAmountOfCommentsInTopic() with GenerationType.SEQUENCE and GenerationType.AUTO strategies
* @SequenceGenerator declaration for the Long type of primary key with default value = (NEXT VALUE FOR seq_topic_id) and UUID based primary key without default value (MySql 5.x doesn't allow using functions as default value)
* Log changes and extra log data produced by DatasourceProxyBeanPostProcessor proxy
* application.yml changes (spring.jpa.properties). Play with this param and check the log file.
* Comment @GeneratedValue(strategy = GenerationType.SEQUENCE) and uncomment @GeneratedValue(strategy = GenerationType.AUTO) lines and inspect the log file
* Check the Liquibase modify_sequences.xml changelog file. Modify @Parameter(name = "increment_size", value = "1000") set value = "100" and see the result of test run (exception) 
