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

package org.apache.ignite.examples.servicegrid;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.services.Service;
import org.apache.ignite.services.ServiceContext;

/**
 * Simple service which utilizes Ignite cache as a mechanism to provide
 * distributed {@link SimpleMapService} functionality.
 * *简单的服务，利用Ignite缓存作为一种机制来提供
 */
public class SimpleMapServiceImpl<K, V> implements Service, SimpleMapService<K, V> {
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 0L;

    /**
     * Ignite instance.
     */
    @IgniteInstanceResource
    private Ignite ignite;

    /**
     * Underlying cache map.
     */
    private IgniteCache<K, V> cache;

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(K key, V val) {
        System.out.println("--------------put了数据------------------");
        cache.put(key, val);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V get(K key) {
        return cache.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        cache.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return cache.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancel(ServiceContext ctx) {
        ignite.destroyCache(ctx.name());

        System.out.println("Service was cancelled: 服务已取消：" + ctx.name());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(ServiceContext ctx) throws Exception {
        // Create a new cache for every service deployment.
        // Note that we use service name as cache name, which allows
        // for each service deployment to use its own isolated cache.
        //为每个服务部署创建一个新的缓存。
        // 请注意，我们使用服务名称作为缓存名称，它允许每个服务部署使用其自己的隔离缓存。
        cache = ignite.getOrCreateCache(new CacheConfiguration<K, V>(ctx.name()));

        System.out.println("Service was initialized: 服务已初始化" + ctx.name());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(ServiceContext ctx) throws Exception {
        System.out.println("Executing distributed service:执行分布式服务： " + ctx.name());
    }
}