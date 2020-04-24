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

package org.apache.ignite.spi.discovery.zk.internal;

import java.util.UUID;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.spi.discovery.DiscoverySpiCustomMessage;


/**
 * Zk Communication Error Resolve Start Message.
 */
public class ZkCommunicationErrorResolveStartMessage implements DiscoverySpiCustomMessage, ZkInternalMessage {
    /** */
    private static final long serialVersionUID = 0L;

    /** */
    final UUID id;

    /**
     * @param id Unique ID.
     */
    ZkCommunicationErrorResolveStartMessage(UUID id) {
        this.id = id;
    }

    /** {@inheritDoc} */
     @Override public DiscoverySpiCustomMessage ackMessage() {
        return null;
    }

    /** {@inheritDoc} */
    @Override public boolean isMutable() {
        return false;
    }

    /** {@inheritDoc} */
    @Override public boolean stopProcess() {
        return false;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(ZkCommunicationErrorResolveStartMessage.class, this);
    }
}
