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

package org.apache.ignite.internal.visor.verify;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;
import org.apache.ignite.internal.processors.cache.verify.PartitionKey;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.internal.visor.VisorDataTransferObject;

/**
 *
 */
public class VisorValidateIndexesJobResult extends VisorDataTransferObject {
    /** */
    private static final long serialVersionUID = 0L;

    /** Results of indexes validation from node. */
    private Map<PartitionKey, ValidateIndexesPartitionResult> partRes;

    /** Results of reverse indexes validation from node. */
    private Map<String, ValidateIndexesPartitionResult> idxRes;

    /**
     * @param partRes Results of indexes validation from node.
     * @param idxRes Results of reverse indexes validation from node.
     */
    public VisorValidateIndexesJobResult(Map<PartitionKey, ValidateIndexesPartitionResult> partRes,
        Map<String, ValidateIndexesPartitionResult> idxRes) {
        this.partRes = partRes;
        this.idxRes = idxRes;
    }

    /**
     * For externalization only.
     */
    public VisorValidateIndexesJobResult() {
    }

    /** {@inheritDoc} */
    @Override public byte getProtocolVersion() {
        return V2;
    }

    /**
     * @return Results of indexes validation from node.
     */
    public Map<PartitionKey, ValidateIndexesPartitionResult> partitionResult() {
        return partRes;
    }

    /**
     * @return Results of reverse indexes validation from node.
     */
    public Map<String, ValidateIndexesPartitionResult> indexResult() {
        return idxRes;
    }

    /** {@inheritDoc} */
    @Override protected void writeExternalData(ObjectOutput out) throws IOException {
        U.writeMap(out, partRes);
        U.writeMap(out, idxRes);
    }

    /** {@inheritDoc} */
    @Override protected void readExternalData(byte protoVer, ObjectInput in) throws IOException, ClassNotFoundException {
        partRes = U.readMap(in);

        if (protoVer >= V2)
            idxRes = U.readMap(in);
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(VisorValidateIndexesJobResult.class, this);
    }

    /**
     * @return {@code true} If any indexes issues found on node, otherwise returns {@code false}.
     */
    public boolean hasIssues() {
        return (partRes != null && partRes.entrySet().stream().anyMatch(e -> !e.getValue().issues().isEmpty())) ||
            (idxRes != null && idxRes.entrySet().stream().anyMatch(e -> !e.getValue().issues().isEmpty()));
    }
}
