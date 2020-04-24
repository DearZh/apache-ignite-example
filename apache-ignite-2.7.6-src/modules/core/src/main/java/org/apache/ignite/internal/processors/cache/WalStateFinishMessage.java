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

import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.lang.IgniteUuid;


import java.util.UUID;

/**
 * WAL state finish message.
 */
public class WalStateFinishMessage extends WalStateAbstractMessage {
    /** */
    private static final long serialVersionUID = 0L;

    /** Whether WAL state was changed as a result of this call. */
    private final boolean changed;

    /** Error message. */
    private final String errMsg;

    /**
     * Constructor.
     *
     * @param opId Unique operation ID.
     * @param grpId Group ID.
     * @param grpDepId Group deployment ID.
     * @param changed Result.
     * @param errMsg Error message.
     */
    public WalStateFinishMessage(UUID opId, int grpId, IgniteUuid grpDepId, boolean changed,  String errMsg) {
        super(opId, grpId, grpDepId);

        this.changed = changed;
        this.errMsg = errMsg;
    }

    /**
     * @return Result.
     */
    public boolean changed() {
        return changed;
    }

    /**
     * @return Error message.
     */
     public String errorMessage() {
        return errMsg;
    }

    /** {@inheritDoc} */
    @Override public boolean stopProcess() {
        return false;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(WalStateFinishMessage.class, this, "super", super.toString());
    }
}
