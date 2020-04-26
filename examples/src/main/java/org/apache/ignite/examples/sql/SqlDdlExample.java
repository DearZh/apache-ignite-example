/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.examples.sql;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.CacheConfiguration;

import java.util.List;

/**
 * 展示Ignite SQL引擎的DDL功能的示例
 */
public class SqlDdlExample {
    /**
     * Dummy cache name. 虚拟缓存名称
     */
    private static final String DUMMY_CACHE_NAME = "DD_DD_dummy_cache";


    @SuppressWarnings({"unused", "ThrowFromFinallyBlock"})
    public static void main(String[] args) throws Exception {

        try (Ignite ignite = Ignition.start("examples/config/example-ignite.xml")) {
            print("Cache query DDL example started.");


            /**
             * 创建虚拟缓存以用作SQL查询的入口点（不需要此功能的新SQL API将在以后的版本中出现，JDBC和ODBC驱动程序已不需要它）
             * 此处定义sqlSchema为PUBLIC（对于Schema的概念在各个场景下都不尽相同，此处可理解为DB中的DB_NAME）；
             */
            CacheConfiguration<?, ?> cacheCfg = new CacheConfiguration<>(DUMMY_CACHE_NAME).setSqlSchema("PUBLIC");

            /**
             * 定义了当前的表名，以及当前表所对应的Schema名称以后，则创建该缓存块；
             */
            try (IgniteCache<?, ?> cache = ignite.getOrCreateCache(cacheCfg)) {

                /**
                 * 关于Ignite中DDL的语法可参考：
                 * https://www.ignite-service.cn/doc/sql/SQLReference.html#_2-3-create-table
                 */


                //根据REPLICATED模板创建参考城市表。（template=replicated）
                cache.query(new SqlFieldsQuery(
                        "CREATE TABLE city (id LONG PRIMARY KEY, name VARCHAR) WITH \"template=replicated\"")).getAll();

                //基于具有一个备份的PARTITIONED模板创建表。（不指定with的情况下，默认模板为PARTITIONED）
                cache.query(new SqlFieldsQuery(
                        "CREATE TABLE person (id LONG, name VARCHAR, city_id LONG, PRIMARY KEY (id, city_id)) " +
                                "WITH \"backups=1, affinity_key=city_id\"")).getAll();

                // 创建索引
                cache.query(new SqlFieldsQuery("CREATE INDEX on Person (city_id)")).getAll();

                //数据插入
                print("Created database objects.");

                SqlFieldsQuery qry = new SqlFieldsQuery("INSERT INTO city (id, name) VALUES (?, ?)");

                cache.query(qry.setArgs(1L, "Forest Hill")).getAll();
                cache.query(qry.setArgs(2L, "Denver")).getAll();
                cache.query(qry.setArgs(3L, "St. Petersburg")).getAll();

                qry = new SqlFieldsQuery("INSERT INTO person (id, name, city_id) values (?, ?, ?)");

                cache.query(qry.setArgs(1L, "John Doe", 3L)).getAll();
                cache.query(qry.setArgs(2L, "Jane Roe", 2L)).getAll();
                cache.query(qry.setArgs(3L, "Mary Major", 1L)).getAll();
                cache.query(qry.setArgs(4L, "Richard Miles", 2L)).getAll();

                print("Populated data.");
                //数据插入完毕


                //数据查询
                List<List<?>> res = cache.query(new SqlFieldsQuery(
                        "SELECT p.name, c.name FROM Person p INNER JOIN City c on c.id = p.city_id")).getAll();

                print("Query results:");

                for (Object next : res)
                    System.out.println(">>>    " + next);

/*
                System.out.println("ARNOLD______：ignite：" + ignite);

                String sql = "select * from city";
                List<List<?>> listA = ignite.getOrCreateCache(DUMMY_CACHE_NAME).query(new SqlFieldsQuery(sql)).getAll();
                System.out.println("##----------------------：" + listA);
*/

                cache.query(new SqlFieldsQuery("drop table Person")).getAll();
                cache.query(new SqlFieldsQuery("drop table City")).getAll();

                print("Dropped database objects.");
            } finally {
                // Distributed cache can be removed from cluster only by #destroyCache() call.
                //只能通过#destroyCache（）调用才能从群集中删除分布式缓存。
                ignite.destroyCache(DUMMY_CACHE_NAME);
            }

            print("Cache query DDL example finished.");
        }
    }

    /**
     * Prints message.
     *
     * @param msg Message to print before all objects are printed.
     */
    private static void print(String msg) {
        System.out.println();
        System.out.println(">>> " + msg);
    }
}
