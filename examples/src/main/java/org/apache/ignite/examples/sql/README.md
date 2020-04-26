# SQL APi
主要演示了Ignite SQL API的用法。
当前文档演示可参考：https://www.ignite-service.cn/doc/sql/JavaDeveloperGuide.html#_1-sql-api
## 介绍
官方介绍：
Ignite 在已有的集群功能上扩展并支持完整的DDL和DML SQL语句，并且还实现了一个与分布式系统有关的DDL
的子集；

解读：也就是说，**Ignite不光单纯的支持DDL SQL语句，它还特殊扩展了一套子集的API子集来实现DDL的效果；
比如，通过Java类注解的方式来实现DDL的效果，以及QueryEntity的方式；**

## Test步骤
当前整体SQL包下所介绍的内容，分别包括：

1、SQL DDL的演示 SqlDdlExample

2、SqlDML的演示 SqlDmlExample（主要说明了通过注解的形式来实现DDL的方式）

3、JDBC纯链接的演示 SqlJdbcExample

4、Queries的演示 SqlQueriesExample

上述几个相关演示类的命名可能会引起相对应的误解：
```
SqlDdlExample并不是完全演示了Sql DDL 的写法
SqlDmlExample也并不是只演示了DML的写法；
相反它们是交互进行的，Ddl类中包含了DML的内容，DML类中包含了ddl的内容。

最大的不同是：
**DDL类中演示了如何基于我们所熟知的SQL DDL的写法(create table)的方式进行表的创建；
而 Dml类中则主要演示了如何通过Java Bean注解的方式是来进行表的创建；**

Test的时候注意一下即可；
```

Ignite中关于DML的写法是全部相同的，并没有特殊的API及写法，只是针对DDL扩展出了不同的API；


## 注意事项
* 使用Create table创建的表，则只能使用Drop table进行删除，使用注解方式创建的表，则只能使用destroyCache() API的方式进行删除；
* 不同的模式Schema下可以创建相同的表名，但是destroyCache("T_N")时也将会把所有schema下的同名表都删除；
* 原生DDL写法建表，则默认表和索引的模式名是“PUBLIC”，非DDL的写法建表则默认Schema是当前CacheConfiguration()中所配置的缓存名；
除此之外，也都可以通过setSchema()的方式来重新配置模式名;
*

**如果当前业务是将Ignite作为DB来使用，则优先建议完全采用原生的DDL写法和DML，更加友好，降低了Coding复杂度，效果更佳；**

**如果是作为业务缓存层而存在，则可以考虑Java Bean的注解写法；**