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

package org.apache.ignite.examples.binary.computegrid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.ignite.compute.ComputeJob;
import org.apache.ignite.compute.ComputeJobAdapter;
import org.apache.ignite.compute.ComputeJobResult;
import org.apache.ignite.compute.ComputeTaskSplitAdapter;
import org.apache.ignite.lang.IgniteBiTuple;
import org.apache.ignite.binary.BinaryObject;


/**
 * Task that is used for {@link ComputeClientBinaryTaskExecutionExample} and
 * similar examples in .NET and C++.
 * <p>
 * This task calculates average salary for provided collection of employees.
 * It splits the collection into batches of size {@code 3} and creates a job
 * for each batch. After all jobs are executed, there results are reduced to
 * get the average salary.
 */

/**
 * 用于{@link ComputeClientBinaryTaskExecutionExample}的任务，以及.NET和C ++中的类似示例。 * <p>
 * *此任务将计算所提供员工集合的平均工资。 *它将集合分成大小为{@code 3}的批次，并为每个批次创建一个作业*。
 * 执行完所有作业后，结果减少为*获得平均薪水。
 */
public class ComputeClientTask extends ComputeTaskSplitAdapter<Collection<BinaryObject>, Long> {
    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection<? extends ComputeJob> split(
            int gridSize,
            Collection<BinaryObject> arg
    ) {
        Collection<ComputeClientJob> jobs = new ArrayList<>();

        Collection<BinaryObject> employees = new ArrayList<>();

        // Split provided collection into batches and
        // create a job for each batch.
        for (BinaryObject employee : arg) {
            employees.add(employee);

            if (employees.size() == 3) {
                jobs.add(new ComputeClientJob(employees));

                employees = new ArrayList<>(3);
            }
        }

        if (!employees.isEmpty())
            jobs.add(new ComputeClientJob(employees));

        return jobs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long reduce(List<ComputeJobResult> results) {
        long sum = 0;
        int cnt = 0;

        for (ComputeJobResult res : results) {
            IgniteBiTuple<Long, Integer> t = res.getData();

            sum += t.get1();
            cnt += t.get2();
        }

        return sum / cnt;
    }

    /**
     * Remote job for {@link ComputeClientTask}.
     */
    private static class ComputeClientJob extends ComputeJobAdapter {
        /**
         * Collection of employees.
         */
        private final Collection<BinaryObject> employees;

        /**
         * @param employees Collection of employees.
         */
        private ComputeClientJob(Collection<BinaryObject> employees) {
            this.employees = employees;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object execute() {
            long sum = 0;
            int cnt = 0;

            for (BinaryObject employee : employees) {
                System.out.println(">>> Processing employee: " + employee.field("name"));

                // Get salary from binary object. Note that object
                // doesn't need to be fully deserialized.
                long salary = employee.field("salary");

                sum += salary;
                cnt++;
            }

            return new IgniteBiTuple<>(sum, cnt);
        }
    }
}
