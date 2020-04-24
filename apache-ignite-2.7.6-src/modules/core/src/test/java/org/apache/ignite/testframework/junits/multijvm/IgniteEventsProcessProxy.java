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

package org.apache.ignite.testframework.junits.multijvm;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteEvents;
import org.apache.ignite.IgniteException;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.events.Event;
import org.apache.ignite.lang.IgniteBiPredicate;
import org.apache.ignite.lang.IgniteFuture;
import org.apache.ignite.lang.IgnitePredicate;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.resources.IgniteInstanceResource;


/**
 * Ignite events proxy for ignite instance at another JVM.
 */
@SuppressWarnings("TransientFieldInNonSerializableClass")
public class IgniteEventsProcessProxy implements IgniteEvents {
    /** Ignite proxy. */
    private final transient IgniteProcessProxy igniteProxy;

    /**
     * @param igniteProxy Ignite proxy.
     */
    public IgniteEventsProcessProxy(IgniteProcessProxy igniteProxy) {
        this.igniteProxy = igniteProxy;
    }

    /** {@inheritDoc} */
    @Override public ClusterGroup clusterGroup() {
        throw new UnsupportedOperationException("Operation isn't supported yet.");
    }

    /** {@inheritDoc} */
    @Override public <T extends Event> List<T> remoteQuery(IgnitePredicate<T> p, long timeout,
         int... types) throws IgniteException {
        throw new UnsupportedOperationException("Operation isn't supported yet.");
    }

    /** {@inheritDoc} */
    @Override public <T extends Event> IgniteFuture<List<T>> remoteQueryAsync(IgnitePredicate<T> p, long timeout,
         int... types) throws IgniteException {
        throw new UnsupportedOperationException("Operation isn't supported yet.");
    }

    /** {@inheritDoc} */
    @Override public <T extends Event> UUID remoteListen( IgniteBiPredicate<UUID, T> locLsnr,
         IgnitePredicate<T> rmtFilter,  int... types) throws IgniteException {
        throw new UnsupportedOperationException("Operation isn't supported yet.");
    }

    /** {@inheritDoc} */
    @Override public <T extends Event> IgniteFuture<UUID> remoteListenAsync(
         IgniteBiPredicate<UUID, T> locLsnr,  IgnitePredicate<T> rmtFilter,
         int... types) throws IgniteException {
        throw new UnsupportedOperationException("Operation isn't supported yet.");
    }

    /** {@inheritDoc} */
    @Override public <T extends Event> UUID remoteListen(int bufSize, long interval, boolean autoUnsubscribe,
         IgniteBiPredicate<UUID, T> locLsnr,  IgnitePredicate<T> rmtFilter,
         int... types) throws IgniteException {
        throw new UnsupportedOperationException("Operation isn't supported yet.");
    }

    /** {@inheritDoc} */
    @Override public <T extends Event> IgniteFuture<UUID> remoteListenAsync(int bufSize, long interval,
        boolean autoUnsubscribe,  IgniteBiPredicate<UUID, T> locLsnr,  IgnitePredicate<T> rmtFilter,
         int... types) throws IgniteException {
        throw new UnsupportedOperationException("Operation isn't supported yet.");
    }

    /** {@inheritDoc} */
    @Override public void stopRemoteListen(UUID opId) throws IgniteException {
        throw new UnsupportedOperationException("Operation isn't supported yet.");
    }

    /** {@inheritDoc} */
    @Override public IgniteFuture<Void> stopRemoteListenAsync(UUID opId) throws IgniteException {
        throw new UnsupportedOperationException("Operation isn't supported yet.");
    }

    /** {@inheritDoc} */
    @Override public <T extends Event> T waitForLocal( IgnitePredicate<T> filter,
         int... types) throws IgniteException {
        throw new UnsupportedOperationException("Operation isn't supported yet.");
    }

    /** {@inheritDoc} */
    @Override public <T extends Event> IgniteFuture<T> waitForLocalAsync( IgnitePredicate<T> filter,
         int... types) throws IgniteException {
        throw new UnsupportedOperationException("Operation isn't supported yet.");
    }

    /** {@inheritDoc} */
    @Override public <T extends Event> Collection<T> localQuery(IgnitePredicate<T> p,  int... types) {
        throw new UnsupportedOperationException("Operation isn't supported yet.");
    }

    /** {@inheritDoc} */
    @Override public void recordLocal(Event evt) {
        throw new UnsupportedOperationException("Operation isn't supported yet.");
    }

    /** {@inheritDoc} */
    @Override public void localListen(final IgnitePredicate<? extends Event> lsnr, final int... types) {
        igniteProxy.remoteCompute().run(new LocalListenTask(lsnr, types));
    }

    /** {@inheritDoc} */
    @Override public boolean stopLocalListen(IgnitePredicate<? extends Event> lsnr,  int... types) {
        throw new UnsupportedOperationException("Operation isn't supported yet.");
    }

    /** {@inheritDoc} */
    @Override public void enableLocal(int... types) {
        throw new UnsupportedOperationException("Operation isn't supported yet.");
    }

    /** {@inheritDoc} */
    @Override public void disableLocal(int... types) {
        throw new UnsupportedOperationException("Operation isn't supported yet.");
    }

    /** {@inheritDoc} */
    @Override public int[] enabledEvents() {
        throw new UnsupportedOperationException("Operation isn't supported yet.");
    }

    /** {@inheritDoc} */
    @Override public boolean isEnabled(int type) {
        throw new UnsupportedOperationException("Operation isn't supported yet.");
    }

    /** {@inheritDoc} */
    @Override public IgniteEvents withAsync() {
        throw new UnsupportedOperationException("Operation isn't supported yet.");
    }

    /** {@inheritDoc} */
    @Override public boolean isAsync() {
        throw new UnsupportedOperationException("Operation isn't supported yet.");
    }

    /** {@inheritDoc} */
    @Override public <R> IgniteFuture<R> future() {
        throw new UnsupportedOperationException("Operation isn't supported yet.");
    }

    /**
     *
     */
    private static class LocalListenTask implements IgniteRunnable {
        /** Ignite. */
        @IgniteInstanceResource
        private Ignite ignite;

        /** Listener. */
        private final IgnitePredicate<? extends Event> lsnr;

        /** Types. */
        private final int[] types;

        /**
         * @param lsnr Listener.
         * @param types Types.
         */
        public LocalListenTask(IgnitePredicate<? extends Event> lsnr, int[] types) {
            this.lsnr = lsnr;
            this.types = types;
        }

        /** {@inheritDoc} */
        @Override public void run() {
            ignite.events().localListen(lsnr, types);
        }
    }
}