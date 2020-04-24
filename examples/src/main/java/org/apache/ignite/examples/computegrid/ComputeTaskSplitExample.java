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

package org.apache.ignite.examples.computegrid;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.compute.ComputeJob;
import org.apache.ignite.compute.ComputeJobAdapter;
import org.apache.ignite.compute.ComputeJobResult;
import org.apache.ignite.compute.ComputeTaskSplitAdapter;
import org.apache.ignite.examples.ExampleNodeStartup;


/**
 * Demonstrates a simple use of Ignite with {@link ComputeTaskSplitAdapter}.
 * <p>
 * Phrase passed as task argument is split into jobs each taking one word. Then jobs are distributed among
 * cluster nodes. Each node computes word length and returns result to master node where total phrase length
 * is calculated on reduce stage.
 * <p>
 * Remote nodes should always be started with special configuration file which
 * enables P2P class loading: {@code 'ignite.{sh|bat} examples/config/example-ignite.xml'}.
 * <p>
 * Alternatively you can run {@link ExampleNodeStartup} in another JVM which will start node
 * with {@code examples/config/example-ignite.xml} configuration.
 */

/**
 * 演示了将Ignite与{@link ComputeTaskSplitAdapter}一起使用的简单方法。
 * 作为任务参数传递的短语被分成多个作业，每个作业占用一个单词。然后，
 * 作业将在*群集节点之间分配。每个节点计算单词长度，并将结果返回到主节点，
 * 在还原阶段计算总短语长度*。 * <p> *远程节点应始终以特殊的配置文件启动，
 * 该文件可启用P2P类加载：{@code'ignite。{sh | bat} examples / config / example-ignite.xml'}。 * <p> *或者，
 * 您可以在另一个JVM中运行{@link ExampleNodeStartup}，该JVM将使用{@code examples / config / example-ignite.xml}配置启动节点*。
 */
public class ComputeTaskSplitExample {
    /**
     * Executes example.
     *
     * @param args Command line arguments, none required.
     * @throws IgniteException If example execution failed.
     */
    public static void main(String[] args) throws IgniteException {
        try (Ignite ignite = Ignition.start("examples/config/example-ignite.xml")) {
            System.out.println();
            System.out.println("Compute task split example started.");

            // Execute task on the cluster and wait for its completion. 在群集上执行任务，然后等待其完成。
            int cnt = ignite.compute().execute(SplitExampleCharacterCountTask.class, "Hello Ignite Enabled World!");

            System.out.println();
            System.out.println(">>> Total number of characters in the phrase is '" + cnt + "'.");
            System.out.println(">>> Check all nodes for output (this node is also part of the cluster).");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Task to count non-white-space characters in a phrase. 任务是计算短语中的非空格字符
     */
    private static class SplitExampleCharacterCountTask extends ComputeTaskSplitAdapter<String, Integer> {
        /**
         * Splits the received string to words, creates a child job for each word, and sends
         * these jobs to other nodes for processing. Each such job simply prints out the received word.
         *
         * @param clusterSize Number of available cluster nodes. Note that returned number of
         *                    jobs can be less, equal or greater than this cluster size.
         * @param arg         Task execution argument. Can be {@code null}.
         * @return The list of child jobs.
         */
        /**
         * 将接收到的字符串拆分为单词，为每个单词创建一个子作业，
         * 然后将这些作业发送给其他节点进行处理。每个这样的作业仅打印出接收到的单词。
         * * * @param clusterSize可用群集节点的数量。请注意，返回的*作业数量可以小于，等于或大于此群集大小。
         * * @param arg任务执行参数。可以为{@code null}。 * @return子作业列表。
         */
        @Override
        protected Collection<? extends ComputeJob> split(int clusterSize, String arg) {
            System.out.println("split开始执行");
            Collection<ComputeJob> jobs = new LinkedList<>();

            for (final String word : arg.split(" ")) {
                jobs.add(new ComputeJobAdapter() {
                    @Override
                    public Object execute() {
                        System.out.println();
                        System.out.println(">>> Printing '" + word + "' on this node from ignite job.");

                        // Return number of letters in the word.
                        return word.length();
                    }
                });
            }
            System.out.println("split执行完毕");
            return jobs;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Integer reduce(List<ComputeJobResult> results) {
            System.out.println("reduce开始执行");
            int sum = 0;

            for (ComputeJobResult res : results)
                sum += res.<Integer>getData();

            System.out.println("reduce执行完毕");
            return sum;
        }
    }
}