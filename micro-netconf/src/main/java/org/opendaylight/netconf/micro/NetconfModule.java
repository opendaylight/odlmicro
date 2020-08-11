/*
 * Copyright (c) 2019 Lumina Networks, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.netconf.micro;

import com.google.inject.Provides;
import io.netty.channel.EventLoopGroup;
import io.netty.util.Timer;
import io.netty.util.concurrent.EventExecutor;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;
import org.opendaylight.aaa.api.CredentialAuth;
import org.opendaylight.aaa.api.PasswordCredentialAuth;
import org.opendaylight.aaa.api.PasswordCredentials;
import org.opendaylight.aaa.encrypt.AAAEncryptionService;
import org.opendaylight.aaa.micro.AAAModule;
import org.opendaylight.controller.config.threadpool.ScheduledThreadPool;
import org.opendaylight.controller.config.threadpool.ThreadPool;
import org.opendaylight.controller.config.threadpool.util.FlexibleThreadPoolWrapper;
import org.opendaylight.controller.config.threadpool.util.NamingThreadPoolFactory;
import org.opendaylight.controller.config.threadpool.util.ScheduledThreadPoolWrapper;
import org.opendaylight.controller.micro.GlobalBossGroup;
import org.opendaylight.controller.micro.GlobalEventExecutor;
import org.opendaylight.controller.micro.GlobalTimer;
import org.opendaylight.controller.micro.GlobalWorkerGroup;
import org.opendaylight.controller.micro.InMemoryControllerModule;
import org.opendaylight.infrautils.micro.InfraUtilsModule;
import org.opendaylight.mdsal.binding.api.DataBroker;
import org.opendaylight.mdsal.dom.api.DOMDataBroker;
import org.opendaylight.mdsal.dom.api.DOMMountPointService;
import org.opendaylight.mdsal.dom.api.DOMRpcService;
import org.opendaylight.mdsal.dom.api.DOMSchemaService;
import org.opendaylight.netconf.api.NetconfServerDispatcher;
import org.opendaylight.netconf.api.monitoring.NetconfMonitoringService;
import org.opendaylight.netconf.authprovider.CredentialServiceAuthProvider;
import org.opendaylight.netconf.client.NetconfClientDispatcher;
import org.opendaylight.netconf.client.NetconfClientDispatcherImpl;
import org.opendaylight.netconf.impl.NetconfServerDispatcherImpl;
import org.opendaylight.netconf.impl.NetconfServerSessionNegotiatorFactory;
import org.opendaylight.netconf.impl.ServerChannelInitializer;
import org.opendaylight.netconf.impl.SessionIdProvider;
import org.opendaylight.netconf.impl.osgi.AggregatedNetconfOperationServiceFactory;
import org.opendaylight.netconf.impl.osgi.NetconfMonitoringServiceImpl;
import org.opendaylight.netconf.mapping.api.NetconfOperationServiceFactory;
import org.opendaylight.netconf.mapping.api.NetconfOperationServiceFactoryListener;
import org.opendaylight.netconf.mdsal.connector.MdsalNetconfOperationServiceFactory;
import org.opendaylight.netconf.micro.annotations.AggregatedNetconfOperationServiceFactoryMappers;
import org.opendaylight.netconf.micro.annotations.GlobalNetconfProcessingExecutor;
import org.opendaylight.netconf.micro.annotations.GlobalNetconfSshScheduledExecutor;
import org.opendaylight.netconf.micro.annotations.MdsalNetconfConnector;
import org.opendaylight.netconf.micro.annotations.NetconfAuthProvider;
import org.opendaylight.netconf.sal.connect.impl.DefaultSchemaResourceManager;
import org.opendaylight.netconf.sal.connect.netconf.DeviceActionFactoryImpl;
import org.opendaylight.netconf.sal.connect.netconf.schema.mapping.DefaultBaseNetconfSchemas;
import org.opendaylight.netconf.topology.api.NetconfTopology;
import org.opendaylight.netconf.topology.impl.NetconfTopologyImpl;
import org.opendaylight.odlguice.inject.guice.AutoWiringModule;
import org.opendaylight.odlguice.inject.guice.GuiceClassPathBinder;
import org.opendaylight.odlguice.inject.guice.testutils.AnnotationsModule;
import org.opendaylight.restconf.micro.RestConfModule;
import org.opendaylight.serviceutils.micro.ServiceUtilsModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Guice module for NetConf.
 *
 */
public class NetconfModule extends AutoWiringModule {

    private static final Logger LOG = LoggerFactory.getLogger(NetconfModule.class);

    // FIXME Move the config parameters to its own class and cfg files
    private static final Long CONNECTION_TIMEOUT_MILLIS = 20000L;
    private static final Integer MONITORING_UPDATE_INTERVAL = 6;
    private static final String NAME_PREFIX = "remote-connector-processing-executor";
    private static final Integer MIN_THREAD_COUNT_FLEXIBLE_THREAD_POOL = 1;
    private static final Integer MAX_THREAD_COUNT_FLEXIBLE_THREAD_POOL = 4;
    private static final Long KEEP_ALIVE_MILLIS_FLEXIBLE_THREAD_POOL = 600000L;
    private static final Integer MAX_THREAD_COUNT_SCHEDULED_THREAD_POOL = 8;
    private static final Integer WRITE_TRANSACTION_IDLE_TIMEOUT_MILLIS = 20;

    public NetconfModule(GuiceClassPathBinder classPathBinder) {
        super(classPathBinder, "org.opendaylight.netconf");
    }

    @Override
    protected void configureMore() {
        LOG.info("Loading netconf");
        // Guice
        install(new AnnotationsModule());
        // Controller/MD-SAL
        install(new InMemoryControllerModule());
        install(new AAAModule());
        install(new InfraUtilsModule());

        // ServiceUtils
        install(new ServiceUtilsModule());

        // RESTCONF
        install(new RestConfModule());
    }

    // Converted here from netconf-config/src/main/resources/OSGI-INF/blueprint/netconf-config.xml
    // START
    @Provides
    @Singleton
    NamingThreadPoolFactory getNamingThreadPoolFactory() {
        return new NamingThreadPoolFactory(NAME_PREFIX);
    }

    @Provides
    @Singleton
    @GlobalNetconfProcessingExecutor
    ThreadPool getFlexibleThreadPool(NamingThreadPoolFactory namingThreadPoolFactory) {
        return new FlexibleThreadPoolWrapper(MIN_THREAD_COUNT_FLEXIBLE_THREAD_POOL,
                MAX_THREAD_COUNT_FLEXIBLE_THREAD_POOL,
                KEEP_ALIVE_MILLIS_FLEXIBLE_THREAD_POOL,
                TimeUnit.valueOf("MILLISECONDS"),
                namingThreadPoolFactory);
    }

    @Provides
    @Singleton
    @GlobalNetconfSshScheduledExecutor
    ScheduledThreadPool getScheduledThreadPool(NamingThreadPoolFactory namingThreadPoolFactory) {
        return new ScheduledThreadPoolWrapper(MAX_THREAD_COUNT_SCHEDULED_THREAD_POOL, namingThreadPoolFactory);
    }

    // Converted here from netconf-config/src/main/resources/OSGI-INF/blueprint/netconf-config.xml
    // END

    // Converted here from netconf-client/src/main/resources/OSGI-INF/blueprint/netconf-client.xml
    // START
    @Provides
    @Singleton
    @org.opendaylight.netconf.micro.annotations.NetconfClientDispatcher
    NetconfClientDispatcher getNetconfClientDispatcher(
            @GlobalBossGroup EventLoopGroup globalBossGroup,
            @GlobalWorkerGroup EventLoopGroup globalWorkerGroup,
            @GlobalTimer Timer globalTimer) {
        return new NetconfClientDispatcherImpl(globalBossGroup, globalWorkerGroup, globalTimer);
    }
    // Converted here from netconf-client/src/main/resources/OSGI-INF/blueprint/netconf-client.xml
    // END

    // Converted here from mdsal-netconf-impl/src/main/resources/OSGI-INF/blueprint/mdsal-netconf-impl.xml
    // START
    @Provides
    @Singleton
    @org.opendaylight.netconf.micro.annotations.MapperAggregatorRegistry
    NetconfOperationServiceFactoryListener getMapperAggregatorRegistry() {
        return new AggregatedNetconfOperationServiceFactory();
    }

    @Provides
    @Singleton
    @org.opendaylight.netconf.micro.annotations.AggregatedNetconfOperationServiceFactory
    NetconfOperationServiceFactory getAggregatedNetconfOperationServiceFactoryListener() {
        return new AggregatedNetconfOperationServiceFactory();
    }

    @Provides
    @Singleton
    @AggregatedNetconfOperationServiceFactoryMappers
    AggregatedNetconfOperationServiceFactory getAggregatedNetconfOperationServiceFactoryMappers(
            @org.opendaylight.netconf.micro.annotations.AggregatedNetconfOperationServiceFactory
                NetconfOperationServiceFactory aggregatedNetconfOperationServiceFactory) {
        return new AggregatedNetconfOperationServiceFactory(Arrays.asList(aggregatedNetconfOperationServiceFactory));
    }

    @Provides
    @Singleton
    NetconfMonitoringService getNetconfMonitoringService(
            @org.opendaylight.netconf.micro.annotations.AggregatedNetconfOperationServiceFactory
                NetconfOperationServiceFactory aggregatedNetconfOperationServiceFactory,
            @GlobalNetconfSshScheduledExecutor ScheduledThreadPool scheduledThreadPool) {
        return new NetconfMonitoringServiceImpl(
            aggregatedNetconfOperationServiceFactory, scheduledThreadPool, MONITORING_UPDATE_INTERVAL);
    }

    @Provides
    @Singleton
    NetconfServerSessionNegotiatorFactory getNetconfServerSessionNegotiatorFactory(
            @GlobalTimer Timer globalTimer,
            @AggregatedNetconfOperationServiceFactoryMappers
                AggregatedNetconfOperationServiceFactory aggregatedNetconfOperationServiceFactoryMappers,
            SessionIdProvider sessionIdProvider,
            NetconfMonitoringService netconfMonitoringService) {
        return new NetconfServerSessionNegotiatorFactory(globalTimer,
                aggregatedNetconfOperationServiceFactoryMappers,
                sessionIdProvider,
                CONNECTION_TIMEOUT_MILLIS,
                netconfMonitoringService);
    }

    @Provides
    @Singleton
    ServerChannelInitializer getServerChannelInitializer(
            NetconfServerSessionNegotiatorFactory netconfServerSessionNegotiatorFactory) {
        return new ServerChannelInitializer(netconfServerSessionNegotiatorFactory);
    }

    @Provides
    @Singleton
    NetconfServerDispatcher getNetconfServerDispatcher(
            ServerChannelInitializer serverChannelInitializer,
            @GlobalBossGroup EventLoopGroup globalBossGroup,
            @GlobalWorkerGroup EventLoopGroup globalWorkerGroup) {
        return new NetconfServerDispatcherImpl(serverChannelInitializer, globalBossGroup, globalWorkerGroup);
    }
    // Converted here from mdsal-netconf-impl/src/main/resources/OSGI-INF/blueprint/mdsal-netconf-impl.xml
    // END

    // Converted here from netconf-topology/src/main/resources/OSGI-INF/blueprint/netconf-topology.xml
    // START
    /*
     * @Provides
     *
     * @Singleton SchemaRepositoryProviderImpl getSchemaRepositoryProviderImpl(
     * ServerChannelInitializer serverChannelInitializer,
     *
     * @GlobalBossGroup EventLoopGroup globalBossGroup,
     *
     * @GlobalWorkerGroup EventLoopGroup globalWorkerGroup) { return new
     * SchemaRepositoryProviderImpl("shared-schema-repository-impl"); }
     */
    @Provides
    @Singleton
    NetconfTopology getNetconfTopology(@org.opendaylight.netconf.micro.annotations.NetconfClientDispatcher
            NetconfClientDispatcher clientDispatcherDependency,
            @GlobalNetconfSshScheduledExecutor ScheduledThreadPool keepAliveExecutor,
            @GlobalNetconfProcessingExecutor ThreadPool processingExecutor,
            DefaultSchemaResourceManager defaultSchemaResourceManager,
            @GlobalEventExecutor EventExecutor eventExecutor,
            DataBroker dataBroker,
            DOMMountPointService mountPointService,
            AAAEncryptionService encryptionService,
            DefaultBaseNetconfSchemas defaultBaseNetconfSchemas,
            DeviceActionFactoryImpl deviceActionFactoryImpl) {
        NetconfTopologyImpl impl = new NetconfTopologyImpl("topology-netconf",
                clientDispatcherDependency,
                eventExecutor,
                keepAliveExecutor,
                processingExecutor,
                defaultSchemaResourceManager,
                dataBroker,
                mountPointService,
                encryptionService,
                defaultBaseNetconfSchemas,
                deviceActionFactoryImpl);
        impl.init();
        return impl;
    }
//
//  @Provides
//  @Singleton
//  NetconfTopologySingletonService getNetconfTopologyManager(DataBroker dataBroker,
//          DOMRpcProviderService rpcRegistry,
//          ClusterSingletonServiceProvider clusterSingletonService,
//          @GlobalNetconfSshScheduledExecutor ScheduledThreadPool keepAliveExecutor,
//          @GlobalNetconfProcessingExecutor ThreadPool processingExecutor,
//          ActorSystemProvider actorSystemProvider,
//          @GlobalEventExecutor EventExecutor eventExecutor,
//          @org.opendaylight.netconf.micro.NetconfClientDispatcher NetconfClientDispatcher clientDispatcherDependency,
//          DOMMountPointService mountPointService,
//          AAAEncryptionService encryptionService) {
//      Config config = new ConfigBuilder().setWriteTransactionIdleTimeout(WRITE_TRANSACTION_IDLE_TIMEOUT_MILLIS)
//              .build();
//      return new NetconfTopologyManager(dataBroker,
//              rpcRegistry,
//              clusterSingletonService,
//              keepAliveExecutor,
//              processingExecutor,
//              actorSystemProvider,
//              eventExecutor,
//              clientDispatcherDependency,
//              "topology-netconf",
//              config,
//              mountPointService,
//              encryptionService);
//  }

    // Converted here from netconf-topology/src/main/resources/OSGI-INF/blueprint/netconf-topology.xml
    // END

    // Converted here from mdsal-netconf-connector/src/main/resources/OSGI-INF/blueprint/mdsal-netconf-connector.xml
    // START

    @Provides
    @Singleton
    @MdsalNetconfConnector
    NetconfOperationServiceFactory getMdsalNetconfOperationServiceFactory(DOMSchemaService schemaService,
            @org.opendaylight.netconf.micro.annotations.MapperAggregatorRegistry
                NetconfOperationServiceFactoryListener mapperAggregatorRegistry,
            DOMDataBroker domDataBroker,
            DOMRpcService domRpcService) {
        return new MdsalNetconfOperationServiceFactory(
            schemaService, mapperAggregatorRegistry, domDataBroker, domRpcService);
    }

    // Converted here from mdsal-netconf-connector/src/main/resources/OSGI-INF/blueprint/mdsal-netconf-connector.xml
    // END

    // Converted here from netconf/aaa-authn-odl-plugin/src/main/resources/OSGI-INF/blueprint/aaa-authn-netconf.xml
    // START

    @Provides
    @Singleton
    @NetconfAuthProvider
    CredentialServiceAuthProvider getNetconfAuthProvider(CredentialAuth<PasswordCredentials> credService) {
        return new CredentialServiceAuthProvider((PasswordCredentialAuth) credService);
    }
    // Converted here from netconf/aaa-authn-odl-plugin/src/main/resources/OSGI-INF/blueprint/aaa-authn-netconf.xml
    // END

}
