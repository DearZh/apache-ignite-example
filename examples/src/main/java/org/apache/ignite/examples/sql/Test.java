package org.apache.ignite.examples.sql;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.CacheConfiguration;

public class Test {

    public static void main(String[] args) throws Exception {
        try (Ignite ignite = Ignition.start("examples/config/example-ignite.xml")) {
            System.out.println("Cache query DDL example started.");
            CacheConfiguration<?, ?> cacheCfg = new CacheConfiguration<>("city").setSqlSchema("PUBLIC");
            try (IgniteCache<?, ?> cache = ignite.getOrCreateCache(cacheCfg)) {
                SqlFieldsQuery qry = new SqlFieldsQuery("INSERT INTO city (id, name) VALUES (?, ?)");
                for (int i = 60001; i < 120000; i++) {
                    cache.query(qry.setArgs(i, "Forest Hill")).getAll();
                }
                System.out.println("Insert End");
            }
        }
    }

}
