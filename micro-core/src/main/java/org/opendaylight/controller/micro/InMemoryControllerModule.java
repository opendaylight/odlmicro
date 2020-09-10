/*
 * Copyright (c) 2017 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.controller.micro;

import com.google.inject.Provides;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.netty.channel.EventLoopGroup;
import io.netty.util.Timer;
import io.netty.util.concurrent.EventExecutor;
import javax.inject.Singleton;
import org.opendaylight.controller.config.yang.netty.eventexecutor.AutoCloseableEventExecutor;
import org.opendaylight.controller.config.yang.netty.threadgroup.NioEventLoopGroupCloseable;
import org.opendaylight.controller.config.yang.netty.timer.HashedWheelTimerCloseable;
import org.opendaylight.infrautils.inject.guice.AbstractCloseableModule;
import org.opendaylight.mdsal.binding.api.DataBroker;
import org.opendaylight.mdsal.binding.api.NotificationPublishService;
import org.opendaylight.mdsal.binding.api.NotificationService;
import org.opendaylight.mdsal.binding.api.RpcConsumerRegistry;
import org.opendaylight.mdsal.binding.api.RpcProviderService;
import org.opendaylight.mdsal.binding.dom.adapter.AdapterContext;
import org.opendaylight.mdsal.binding.dom.adapter.BindingDOMNotificationPublishServiceAdapter;
import org.opendaylight.mdsal.binding.dom.adapter.BindingDOMNotificationServiceAdapter;
import org.opendaylight.mdsal.binding.dom.adapter.BindingDOMRpcProviderServiceAdapter;
import org.opendaylight.mdsal.binding.dom.adapter.BindingDOMRpcServiceAdapter;
import org.opendaylight.mdsal.binding.dom.adapter.CurrentAdapterSerializer;
import org.opendaylight.mdsal.binding.dom.codec.api.BindingNormalizedNodeSerializer;
import org.opendaylight.mdsal.dom.api.DOMActionProviderService;
import org.opendaylight.mdsal.dom.api.DOMActionService;
import org.opendaylight.mdsal.dom.api.DOMDataBroker;
import org.opendaylight.mdsal.dom.api.DOMMountPointService;
import org.opendaylight.mdsal.dom.api.DOMNotificationService;
import org.opendaylight.mdsal.dom.api.DOMRpcProviderService;
import org.opendaylight.mdsal.dom.api.DOMRpcService;
import org.opendaylight.mdsal.dom.api.DOMSchemaService;
import org.opendaylight.mdsal.dom.broker.DOMMountPointServiceImpl;
import org.opendaylight.mdsal.dom.broker.DOMNotificationRouter;
import org.opendaylight.mdsal.dom.broker.DOMRpcRouter;
import org.opendaylight.mdsal.eos.binding.api.EntityOwnershipService;
import org.opendaylight.mdsal.eos.binding.dom.adapter.BindingDOMEntityOwnershipServiceAdapter;
import org.opendaylight.mdsal.eos.dom.simple.SimpleDOMEntityOwnershipService;
import org.opendaylight.mdsal.micro.MdsalModule;
import org.opendaylight.mdsal.singleton.common.api.ClusterSingletonServiceProvider;
import org.opendaylight.mdsal.singleton.dom.impl.DOMClusterSingletonServiceProviderImpl;
import org.opendaylight.yangtools.yang.model.parser.api.YangParserFactory;
import org.opendaylight.yangtools.yang.parser.impl.YangParserFactoryImpl;
import org.opendaylight.yangtools.yang.xpath.api.YangXPathParserFactory;
import org.opendaylight.yangtools.yang.xpath.impl.AntlrXPathParserFactory;


@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
public class InMemoryControllerModule extends AbstractCloseableModule {

    // TODO re-use
    // org.opendaylight.controller.md.sal.binding.impl.BindingBrokerWiring

    // TODO propose @Inject and @PreDestroy close() annotations at source to
    // simplify this, a lot...

    private CurrentAdapterSerializer currentAdapterSerializer;
    private AdapterContext adapterContext;
    private BindingDOMNotificationPublishServiceAdapter bindingDOMNotificationPublishServiceAdapter;
    private DOMNotificationRouter domNotificationPublishService;

    @Override
    protected void configureCloseables() {
        install(new MdsalModule());

        // TODO this is just for early stage POC! switch to real CDS wiring here,
        // eventually..
        DataBrokerModule dataBrokerTestModule = new DataBrokerModule(true);

        DataBroker dataBroker = dataBrokerTestModule.getDataBroker();
        bind(DataBroker.class).toInstance(dataBroker);

        DOMSchemaService domSchemaService = dataBrokerTestModule.getSchemaService();
        bind(DOMSchemaService.class).toInstance(domSchemaService);

        DOMDataBroker domDefaultDataBroker = dataBrokerTestModule.getDOMDataBroker();
        bind(DOMDataBroker.class).toInstance(domDefaultDataBroker);

        currentAdapterSerializer = dataBrokerTestModule.getCurrentAdapterSerializer();
        bind(CurrentAdapterSerializer.class).toInstance(currentAdapterSerializer);

        adapterContext = dataBrokerTestModule.getAdapterContext();

        domNotificationPublishService = dataBrokerTestModule.getDOMNotificationRouter();
        bind(DOMNotificationService.class).toInstance(domNotificationPublishService);
        bind(NotificationService.class)
                .toInstance(new BindingDOMNotificationServiceAdapter(adapterContext, domNotificationPublishService));

        bindingDOMNotificationPublishServiceAdapter =
                new BindingDOMNotificationPublishServiceAdapter(adapterContext, domNotificationPublishService);
        bind(NotificationPublishService.class).toInstance(bindingDOMNotificationPublishServiceAdapter);
        bind(BindingNormalizedNodeSerializer.class).toInstance(currentAdapterSerializer);

        bind(DOMMountPointService.class).to(DOMMountPointServiceImpl.class);

        DOMRpcRouter domRpcRouter = DOMRpcRouter.newInstance(domSchemaService);
        bind(DOMRpcRouter.class).toInstance(domRpcRouter);

        DOMRpcService domRpcService = domRpcRouter.getRpcService();
        bind(DOMRpcService.class).toInstance(domRpcService);

        DOMRpcProviderService domRpcProviderService = domRpcRouter.getRpcProviderService();
        bind(DOMRpcProviderService.class).toInstance(domRpcProviderService);

        DOMActionProviderService domActionProviderService = domRpcRouter.getActionProviderService();
        bind(DOMActionProviderService.class).toInstance(domActionProviderService);

        DOMActionService domActionService = domRpcRouter.getActionService();
        bind(DOMActionService.class).toInstance(domActionService);

        BindingDOMRpcServiceAdapter bindingDOMRpcServiceAdapter =
                new BindingDOMRpcServiceAdapter(adapterContext, domRpcService);
        bind(RpcConsumerRegistry.class).toInstance(bindingDOMRpcServiceAdapter);

        BindingDOMRpcProviderServiceAdapter bindingDOMRpcProviderServiceAdapter =
                new BindingDOMRpcProviderServiceAdapter(adapterContext, domRpcProviderService);
        bind(RpcProviderService.class).toInstance(bindingDOMRpcProviderServiceAdapter);

        SimpleDOMEntityOwnershipService eos = new SimpleDOMEntityOwnershipService();
        bind(EntityOwnershipService.class).toInstance(new BindingDOMEntityOwnershipServiceAdapter(eos, adapterContext));
        bind(ClusterSingletonServiceProvider.class).toInstance(new DOMClusterSingletonServiceProviderImpl(eos));

        bind(YangXPathParserFactory.class).to(AntlrXPathParserFactory.class);
        bind(YangParserFactory.class).to(YangParserFactoryImpl.class);

    }

    // NETCONF
    // FIXME Add configuration for thread-count
    @Provides
    @Singleton
    @GlobalBossGroup
    EventLoopGroup getGlobalBossGroup() {
        return NioEventLoopGroupCloseable.newInstance(0);
    }

    // NETCONF
    // FIXME Add configuration for thread-count
    @Provides
    @Singleton
    @GlobalWorkerGroup
    EventLoopGroup getGlobalWorkerGroup() {
        return NioEventLoopGroupCloseable.newInstance(0);
    }

    // NETCONF
    // FIXME Add configuration for thread-count
    @Provides
    @Singleton
    @GlobalTimer
    Timer getGlobalTimer() {
        return HashedWheelTimerCloseable.newInstance(0L, 0);
    }

    // NETCONF
    @Provides
    @Singleton
    @GlobalEventExecutor
    EventExecutor getGlobalEventExecutor() {
        return AutoCloseableEventExecutor.globalEventExecutor();
    }

    @Override
    public void close() throws Exception {
        domNotificationPublishService.close();
    }
}
