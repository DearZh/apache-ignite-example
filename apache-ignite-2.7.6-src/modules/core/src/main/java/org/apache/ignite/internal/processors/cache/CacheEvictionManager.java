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

package org.apache.ignite.internal.processors.cache;

import java.util.Collection;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.internal.processors.affinity.AffinityTopologyVersion;
import org.apache.ignite.internal.processors.cache.transactions.IgniteTxEntry;
import org.apache.ignite.internal.processors.cache.version.GridCacheVersion;


/**
 *
 */
public interface CacheEvictionManager extends GridCacheManager {
    /**
     * @param txEntry Transactional entry.
     * @param loc Local transaction flag.
     */
    public void touch(IgniteTxEntry txEntry, boolean loc);

    /**
     * @param e      Entry for eviction policy notification.
     * @param topVer Topology version.
     */
    public void touch(GridCacheEntryEx e, AffinityTopologyVersion topVer);

    /**
     * @param entry Entry to attempt to evict.
     * @param obsoleteVer Obsolete version.
     * @param filter Optional entry filter.
     * @param explicit {@code True} if evict is called explicitly, {@code false} if it's called
     *      from eviction policy.
     * @return {@code True} if entry was marked for eviction.
     * @throws IgniteCheckedException In case of error.
     */
    public boolean evict( GridCacheEntryEx entry,
         GridCacheVersion obsoleteVer,
        boolean explicit,
         CacheEntryPredicate[] filter) throws IgniteCheckedException;

    /**
     * @param keys Keys to evict.
     * @param obsoleteVer Obsolete version.
     * @throws IgniteCheckedException In case of error.
     */
    public void batchEvict(Collection<?> keys,  GridCacheVersion obsoleteVer) throws IgniteCheckedException;
}