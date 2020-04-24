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

package org.apache.ignite.examples.servicegrid;

import java.util.Collection;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteServices;
import org.apache.ignite.Ignition;
import org.apache.ignite.examples.ExampleNodeStartup;
import org.apache.ignite.examples.ExamplesUtils;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.resources.ServiceResource;

/**
 * Example that demonstrates how to deploy distributed services in Ignite.
 * Distributed services are especially useful when deploying singletons on the ignite,
 * be that cluster-singleton, or per-node-singleton, etc...
 * <p>
 * To start remote nodes, you must run {@link ExampleNodeStartup} in another JVM
 * which will start node with {@code examples/config/example-ignite.xml} configuration.
 * <p>
 * NOTE:<br/>
 * Starting {@code ignite.sh} directly will not work, as distributed services
 * cannot be peer-deployed and classes must be on the classpath for every node.
 */

/**
 * 演示如何在Ignite中部署分布式服务的示例。
 * *当在ignite上部署单例时，分布式服务特别有用，*是集群单个节点还是每个节点单个节点，等等... * <p>
 * *要启动远程节点，必须在以下目录中运行{@link ExampleNodeStartup}另一个JVM *，
 * 它将以{@code examples / config / example-ignite.xml}配置开始节点。 * <p> *注意：<br/>
 * *直接启动{@code ignite.sh}无效，因为分布式服务*不能进行对等部署，并且每个节点的类必须位于类路径上。
 */
public class ServicesExample {
    /**
     * Executes example.
     *
     * @param args Command line arguments, none required.
     * @throws Exception If example execution failed.
     */
    public static void main(String[] args) throws Exception {
        // Mark this node as client node. 将该节点标记为客户端节点启动
        Ignition.setClientMode(true);

        /**
         * 由于此处Ignite节点被配置为了客户端方式，所以在Ignite启动的时候要链接到远程的节点上才行，否则将会存在警告提示；<br/>
         * 此处在启动ignite节点时，需要先启动一个ignite 服务端节点，否则当前test无效
         */

        try (Ignite ignite = Ignition.start("examples/config/example-ignite.xml")) {
            //校验当前Ignite是否存在服务端节点；
            if (!ExamplesUtils.hasServerNodes(ignite))
                return;

            // Deploy services only on server nodes. 仅在服务器节点上部署服务
            IgniteServices svcs = ignite.services(ignite.cluster().forServers());

            try {
                // Deploy cluster singleton. 服务网格注册，将当前的SimpleMapServiceImpl() 服务网格应用实现部署在ignite服务端节点中（只会部署到任意一个节点中）
                svcs.deployClusterSingleton("myClusterSingletonService", new SimpleMapServiceImpl());

                // Deploy node singleton. 部署节点单例（将当前的服务网格应用，部署到所有的服务端节点中）
                svcs.deployNodeSingleton("myNodeSingletonService", new SimpleMapServiceImpl());

                // Deploy 2 instances, regardless of number nodes. 无论节点数有多少，只随机部署2个实例
                svcs.deployMultiple("myMultiService",
                        new SimpleMapServiceImpl(),
                        2 /*total number*/,
                        0 /*0 for unlimited*/);

                /**
                 * 下述方法演示了上面被部署到服务节点上的服务应用，如何被调用 ↓↓↓
                 */

                // Example for using a service proxy        使用服务代理
                // to access a remotely deployed service.   访问远程部署的服务的示例。
                serviceProxyExample(ignite);

                // Example for auto-injecting service proxy 自动注入服务代理示例
                // into remote closure execution. 远程关闭执行
                serviceInjectionExample(ignite);
            } finally {
                // Undeploy all services.
                ignite.services().cancelAll();
            }
        }
    }

    /**
     * simple example to demonstrate service proxy invocation of a remotely deployed service.
     * 简单示例演示远程部署服务的服务代理调用
     *
     * @param ignite Ignite instance.
     * @throws Exception If failed.
     */
    private static void serviceProxyExample(Ignite ignite) throws Exception {
        System.out.println(">>>");
        System.out.println(">>> Starting service proxy example.");
        System.out.println(">>>");

        // Get a sticky proxy for node-singleton map service.
        /**
         * 获取当前被代理的单节点服务网格（获取集群节点上其中一个服务网格代理）<br/>
         * 当前myNodeSingletonService的服务网格应用假设是已经被部署到了3个节点上（A,B,C)，那么此处获取指定节点代理时，只会随机代理到其中一个节点上的服务网格应用
         * 即每次通过该代理所发送的请求，都只会到当前其中一个网格上进行执行；<br/>
         * 假设此时获取到的是A节点上的服务网格应用代理，那么后续通过mapSvc的方法执行，则都是在A节点上执行该服务网格应用<br/>
         * 假设当前A节点上负载压力已经较大，或者想切换到其他节点上调用服务网格，那么则直接使用clusterGroup的分组即可；
         * 如此处可以使用：
         * ignite.services(ignite.cluster().forAttribute("ROLE", "worker")).serviceProxy()
         * 当前services() 可以接受一个ClusterGroup作为参数，通过获取我们想使用的集群节点，然后再代理对应的服务网格应用即可；<br/>
         *
         * 关于：ClusterGroup的介绍，可以查看技术服务网的集群节点一节
         * https://www.ignite-service.cn/doc/java/Clustering.html#_2-3-%E9%9B%86%E7%BE%A4%E8%8A%82%E7%82%B9%E5%B1%9E%E6%80%A7
         */

        SimpleMapService<Integer, String> mapSvc = ignite.services().serviceProxy("myNodeSingletonService",
                SimpleMapService.class,
                true);

        int cnt = 10;

        /**
         * 上述所获取的mapSvc代理是来自于服务端的远程节点中，
         * 所以每次的服务调用都会通过该代理到达某个远程节点中的网格应用中进行执行 <br/>
         * 由于服务代理是粘性的，因此我们将始终与同一远程节点联系。
         */
        // Each service invocation will go over a proxy to some remote node. 每次服务调用都将通过代理到达某个远程节点，然后来执行对应节点的服务应用网格
        // Since service proxy is sticky, we will always be contacting the same remote node. 由于服务代理是粘性的，因此我们将始终与同一远程节点联系。
        for (int i = 0; i < cnt; i++)
            mapSvc.put(i, Integer.toString(i));

        // Get size from remotely deployed service instance.
        int mapSize = mapSvc.size();

        System.out.println("Map service size: " + mapSize);

        if (mapSize != cnt)
            throw new Exception("Invalid map size [expected=" + cnt + ", actual=" + mapSize + ']');
    }

    /**
     * Simple example to demonstrate how to inject service proxy into distributed closures.
     * 一个简单的示例，演示如何将服务代理注入分布式闭包中。
     *
     * @param ignite Ignite instance.
     * @throws Exception If failed.
     */
    private static void serviceInjectionExample(Ignite ignite) throws Exception {
        System.out.println(">>>");
        System.out.println(">>> Starting service injection example.");
        System.out.println(">>>");

        // Get a sticky proxy for cluster-singleton map service. 获取一个key为myClusterSingletonService的服务网格代理
        SimpleMapService<Integer, String> mapSvc = ignite.services().serviceProxy("myClusterSingletonService",
                SimpleMapService.class,
                true);

        int cnt = 10;

        // Each service invocation will go over a proxy to the remote cluster-singleton instance. 每次服务调用都将通过代理到达远程集群-单个实例。
        for (int i = 0; i < cnt; i++)
            mapSvc.put(i, Integer.toString(i));

        /**
         * 此处与上述serviceProxyExample()方法单纯的演示服务网格的调用不同<br/>
         * 下述代码主要说明了：通过将服务应用的代理代理自动注入的方式并通过广播闭包的形式，将各个服务节点都执行一次服务网格应用；<br/>
         * 好处是：
         * 可以分散服务的承压，将一个节点需要执行的代码，分散到多个节点上执行，减少了单个节点的压力，
         * 其实这也是IgniteCompute()计算网格的好处，关于计算网格可以直接查看此处DEMO：ComputeTaskSplitExample类
         * 或技术服务站的：https://www.ignite-service.cn/doc/java/ComputeGrid.html#_1-%E8%AE%A1%E7%AE%97%E7%BD%91%E6%A0%BC<br/>
         *
         * ↓↓↓↓↓
         * 尽管部分服务节点是没有部署服务网格应用的，
         * 但是我们仍然可以通过将服务网格应用的代理注入到闭包的形式，通知其他节点进行代码的执行；
         */

        // Broadcast closure to every node. 向每个节点广播闭包，各个服务节点都将会执行SimpleClosure类的call方法，并返回最终结果集
        final Collection<Integer> mapSizes = ignite.compute().broadcast(new SimpleClosure());

        System.out.println("Closure execution result: " + mapSizes);

        // Since we invoked the same cluster-singleton service instance
        // from all the remote closure executions, they should all return
        // the same size equal to 'cnt' value.
        for (int mapSize : mapSizes)
            if (mapSize != cnt)
                throw new Exception("Invalid map size [expected=" + cnt + ", actual=" + mapSize + ']');
    }

    /**
     * Simple closure to demonstrate auto-injection of the service proxy.
     * 简单的闭包以演示服务代理的自动注入
     */
    private static class SimpleClosure implements IgniteCallable<Integer> {
        // Auto-inject service proxy.
        @ServiceResource(serviceName = "myClusterSingletonService", proxyInterface = SimpleMapService.class)
        private transient SimpleMapService mapSvc;

        /**
         * {@inheritDoc}
         */
        @Override
        public Integer call() throws Exception {
            int mapSize = mapSvc.size();

            System.out.println("闭包执行：Executing closure [mapSize=" + mapSize + ']');

            return mapSize;
        }
    }
}