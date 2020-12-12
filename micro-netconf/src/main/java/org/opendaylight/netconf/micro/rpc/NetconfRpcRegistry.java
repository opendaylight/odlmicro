/*
 * Copyright (c) 2020 Control2113.io. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.netconf.micro.rpc;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.opendaylight.aaa.encrypt.AAAEncryptionService;
import org.opendaylight.mdsal.binding.api.DataBroker;
import org.opendaylight.mdsal.binding.api.RpcProviderService;
import org.opendaylight.netconf.sal.connect.util.NetconfTopologyRPCProvider;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.NetconfNodeTopologyService;
import org.opendaylight.yangtools.concepts.ObjectRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class NetconfRpcRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(NetconfRpcRegistry.class);

    private final DataBroker dataBroker;
    private ObjectRegistration<NetconfNodeTopologyService> netconfNodeTopologyService;
    private RpcProviderService rpcProviderService;
    private AAAEncryptionService encryptionService;

    @Inject
    public NetconfRpcRegistry(final DataBroker dataBroker, final RpcProviderService rpcProviderService,
            final AAAEncryptionService encryptionService) {
        this.dataBroker = dataBroker;
        this.rpcProviderService = rpcProviderService;
        this.encryptionService = encryptionService;
    }

    @PostConstruct
    public void init() {
        LOG.info("NetconfRpcRegistry Session Initiated");
        netconfNodeTopologyService = rpcProviderService.registerRpcImplementation(NetconfNodeTopologyService.class,
                new NetconfTopologyRPCProvider(this.dataBroker, this.encryptionService, "topology-netconf"));
    }

    @PreDestroy
    public void close() {
        LOG.info("NetconfRpcRegistry Closed");
        if (netconfNodeTopologyService != null) {
            netconfNodeTopologyService.close();
        }
    }
}
