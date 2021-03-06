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

package org.apache.ignite.internal.visor.tx;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.internal.visor.VisorDataTransferObject;
import org.apache.ignite.transactions.TransactionState;


/**
 * Argument for {@link VisorTxTask}.
 */
public class VisorTxTaskArg extends VisorDataTransferObject {
    /** */
    private static final long serialVersionUID = 0L;

    /** */
    private VisorTxOperation op;

    /** */
    private  Integer limit;

    /** */
    private  Long minDuration;

    /** */
    private  Integer minSize;

    /** */
    private  TransactionState state;

    /** */
    private  VisorTxProjection proj;

    /** */
    private  List<String> consistentIds;

    /** */
    private  String xid;

    /** */
    private  String lbRegex;

    /** */
    private  VisorTxSortOrder sortOrder;

    /**
     * Default constructor.
     */
    public VisorTxTaskArg() {
        // No-op.
    }

    /**
     * @param limit Limit to collect.
     * @param minDuration Min duration.
     * @param minSize Min size.
     * @param state State.
     * @param proj Projection.
     * @param consistentIds Consistent ids for NODES projection.
     * @param xid Xid.
     * @param lbRegex Label regex.
     * @param sortOrder Sort order.
     */
    public VisorTxTaskArg(VisorTxOperation op,  Integer limit,  Long minDuration,  Integer minSize,
         TransactionState state,  VisorTxProjection proj,  List<String> consistentIds,
         String xid,  String lbRegex,  VisorTxSortOrder sortOrder) {
        this.op = op;
        this.limit = limit;
        this.minDuration = minDuration;
        this.minSize = minSize;
        this.state = state;
        this.proj = proj;
        this.consistentIds = consistentIds;
        this.lbRegex = lbRegex;
        this.xid = xid;
        this.sortOrder = sortOrder;
    }

    /** */
    public VisorTxOperation getOperation() {
        return op;
    }

    /** */
     public Integer getLimit() {
        return limit;
    }

    /** */
     public Long getMinDuration() {
        return minDuration;
    }

    /** */
     public Integer getMinSize() {
        return minSize;
    }

    /** */
     public TransactionState getState() {
        return state;
    }

    /** */
    public VisorTxProjection getProjection() {
        return proj;
    }

    /** */
     public List<String> getConsistentIds() {
        return consistentIds;
    }

    /** */
     public String getLabelRegex() {
        return lbRegex;
    }

    /** */
     public String getXid() {
        return xid;
    }

    /** */
     public VisorTxSortOrder getSortOrder() {
        return sortOrder;
    }

    /** {@inheritDoc} */
    @Override protected void writeExternalData(ObjectOutput out) throws IOException {
        U.writeEnum(out, op);
        out.writeInt(limit == null ? -1 : limit);
        out.writeLong(minDuration == null ? -1 : minDuration);
        out.writeInt(minSize == null ? -1 : minSize);
        U.writeEnum(out, state);
        U.writeEnum(out, proj);
        U.writeCollection(out, consistentIds);
        out.writeUTF(lbRegex == null ? "" : lbRegex);
        out.writeUTF(xid == null ? "" : xid);
        U.writeEnum(out, sortOrder);
    }

    /** {@inheritDoc} */
    @Override protected void readExternalData(byte protoVer, ObjectInput in) throws IOException, ClassNotFoundException {
        op = VisorTxOperation.fromOrdinal(in.readByte());
        limit = fixNull(in.readInt());
        minDuration = fixNull(in.readLong());
        minSize = fixNull(in.readInt());
        state = TransactionState.fromOrdinal(in.readByte());
        proj = VisorTxProjection.fromOrdinal(in.readByte());
        consistentIds = U.readList(in);
        lbRegex = fixNull(in.readUTF());
        xid = fixNull(in.readUTF());
        sortOrder = VisorTxSortOrder.fromOrdinal(in.readByte());
    }

    /**
     * @param val Value.
     */
    private Integer fixNull(int val) {
        return val == -1 ? null : val;
    }

    /**
     * @param val Value.
     */
    private Long fixNull(long val) {
        return val == -1 ? null : val;
    }

    /**
     * @param val Value.
     */
    private String fixNull(String val) {
        return "".equals(val) ? null : val;
    }


    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(VisorTxTaskArg.class, this);
    }
}
