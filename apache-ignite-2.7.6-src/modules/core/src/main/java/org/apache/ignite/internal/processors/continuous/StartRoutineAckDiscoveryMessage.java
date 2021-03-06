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

package org.apache.ignite.internal.processors.continuous;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.internal.managers.discovery.DiscoveryCustomMessage;
import org.apache.ignite.internal.util.tostring.GridToStringExclude;
import org.apache.ignite.internal.util.typedef.T2;
import org.apache.ignite.internal.util.typedef.internal.S;


/**
 *
 */
public class StartRoutineAckDiscoveryMessage extends AbstractContinuousMessage {
    /** */
    private static final long serialVersionUID = 0L;

    /** */
    private final Map<UUID, IgniteCheckedException> errs;

    /** */
    @GridToStringExclude
    private final Map<Integer, T2<Long, Long>> updateCntrs;

    /** */
    @GridToStringExclude
    private final Map<UUID, Map<Integer, T2<Long, Long>>> updateCntrsPerNode;

    /**
     * @param routineId Routine id.
     * @param errs Errs.
     * @param cntrs Partition counters.
     * @param cntrsPerNode Partition counters per node.
     */
    public StartRoutineAckDiscoveryMessage(UUID routineId,
        Map<UUID, IgniteCheckedException> errs,
        Map<Integer, T2<Long, Long>> cntrs,
        Map<UUID, Map<Integer, T2<Long, Long>>> cntrsPerNode) {
        super(routineId);

        this.errs = new HashMap<>(errs);
        this.updateCntrs = cntrs;
        this.updateCntrsPerNode = cntrsPerNode;
    }

    /** {@inheritDoc} */
     @Override public DiscoveryCustomMessage ackMessage() {
        return null;
    }

    /**
     * @return Update counters for partitions.
     */
    public Map<Integer, T2<Long, Long>> updateCounters() {
        return updateCntrs;
    }

    /**
     * @return Update counters for partitions per each node.
     */
    public Map<UUID, Map<Integer, T2<Long, Long>>> updateCountersPerNode() {
        return updateCntrsPerNode;
    }

    /**
     * @return Errs.
     */
    public Map<UUID, IgniteCheckedException> errs() {
        return errs;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(StartRoutineAckDiscoveryMessage.class, this, "routineId", routineId());
    }
}
