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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.compute.ComputeJob;
import org.apache.ignite.compute.ComputeJobAdapter;
import org.apache.ignite.compute.ComputeJobResult;
import org.apache.ignite.compute.ComputeTaskAdapter;
import org.apache.ignite.examples.ExampleNodeStartup;


/**
 * Demonstrates a simple use of Ignite with
 * {@link ComputeTaskAdapter}.
 * <p>
 * Phrase passed as task argument is split into words on map stage and distributed among cluster nodes.
 * Each node computes word length and returns result to master node where total phrase length is
 * calculated on reduce stage.
 * <p>
 * Remote nodes should always be started with special configuration file which
 * enables P2P class loading: {@code 'ignite.{sh|bat} examples/config/example-ignite.xml'}.
 * <p>
 * Alternatively you can run {@link ExampleNodeStartup} in another JVM which will start node
 * with {@code examples/config/example-ignite.xml} configuration.
 */

/**
 * 演示将Ignite与* {@link ComputeTaskAdapter}一起使用。
 * * <p> *作为任务参数传递的短语在map阶段被拆分为单词，并分布在群集节点之间。
 * *每个节点都会计算单词长度，并将结果返回到主节点，该主节点在*减少阶段计算总短语长度。
 * * <p> *远程节点应始终以特殊的配置文件启动，该文件可启用P2P类加载：{@code'ignite。{sh | bat} examples / config / example-ignite.xml'}。 * <p> *或者
 * ，您可以在另一个JVM中运行{@link ExampleNodeStartup}，该JVM将使用{@code examples / config / example-ignite.xml}配置启动节点*。
 */
public class ComputeTaskMapExample {
    /**
     * Executes example.
     *
     * @param args Command line arguments, none required.
     * @throws IgniteException If example execution failed.
     */
    public static void main(String[] args) throws IgniteException {
        try (Ignite ignite = Ignition.start("examples/config/example-ignite.xml")) {
            System.out.println();
            System.out.println("Compute task map example started.");

            // Execute task on the cluster and wait for its completion.在群集上执行任务，然后等待其完成。
            //会出现乱序执行的情况，
            int cnt = ignite.compute().execute(MapExampleCharacterCountTask.class, "Hello Ignite Enabled World!");

            System.out.println();
            System.out.println(">>> Total number of characters in the phrase is '" + cnt + "'.");
            System.out.println(">>> Check all nodes for output (this node is also part of the cluster).");
        }
    }

    /**
     * Task to count non-white-space characters in a phrase.
     */
    private static class MapExampleCharacterCountTask extends ComputeTaskAdapter<String, Integer> {
        /**
         * Splits the received string to words, creates a child job for each word, and sends
         * these jobs to other nodes for processing. Each such job simply prints out the received word.
         *
         * @param nodes Nodes available for this task execution.
         * @param arg   String to split into words for processing.
         * @return Map of jobs to nodes.
         */
        @Override
        public Map<? extends ComputeJob, ClusterNode> map(List<ClusterNode> nodes, String arg) {
            Map<ComputeJob, ClusterNode> map = new HashMap<>();

            Iterator<ClusterNode> it = nodes.iterator();

            for (final String word : arg.split(" ")) {
                // If we used all nodes, restart the iterator. 如果我们使用了所有节点，请重新启动迭代器。
                if (!it.hasNext())
                    it = nodes.iterator();

                ClusterNode node = it.next();

                map.put(new ComputeJobAdapter() {
                    @Override
                    public Object execute() {
                        System.out.println();
                        System.out.println(">>> Printing '" + word + "' on this node from ignite job.");

                        // Return number of letters in the word.
                        return word.length();
                    }
                }, node);
            }

            return map;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Integer reduce(List<ComputeJobResult> results) {
            int sum = 0;

            for (ComputeJobResult res : results)
                sum += res.<Integer>getData();

            return sum;
        }
    }
}