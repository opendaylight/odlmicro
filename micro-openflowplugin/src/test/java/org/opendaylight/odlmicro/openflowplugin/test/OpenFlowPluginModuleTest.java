/*
 * Copyright (c) 2017 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.odlmicro.openflowplugin.test;

import static com.google.common.truth.Truth.assertThat;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.inject.Inject;
import junit.framework.AssertionFailedError;
import org.junit.Rule;
import org.junit.Test;
import org.opendaylight.aaa.web.testutils.WebTestModule;
import org.opendaylight.infrautils.diagstatus.DiagStatusService;
import org.opendaylight.infrautils.diagstatus.ServiceStatusSummary;
import org.opendaylight.odlguice.inject.guice.GuiceClassPathBinder;
import org.opendaylight.odlguice.inject.guice.testutils.AnnotationsModule;
import org.opendaylight.odlguice.inject.guice.testutils.GuiceRule;
import org.opendaylight.odlmicro.controller.InMemoryControllerModule;
import org.opendaylight.odlmicro.infrautils.DiagStatusModule;
import org.opendaylight.odlmicro.infrautils.inject.guice.ready.GuiceReadyModule;
import org.opendaylight.odlmicro.infrautils.testutils.AbstractSimpleDistributionTest;
import org.opendaylight.odlmicro.openflowplugin.OpenFlowPluginModule;
import org.opendaylight.odlmicro.serviceutils.ServiceUtilsModule;
import org.opendaylight.openflowplugin.api.openflow.OpenFlowPluginProvider;
import org.opendaylight.openflowplugin.impl.OpenFlowPluginProviderImpl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.PacketProcessingService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.openflow.provider.config.rev160510.OpenflowProviderConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.openflowplugin.app.forwardingrules.manager.config.rev160511.ForwardingRulesManagerConfig;

public class OpenFlowPluginModuleTest extends AbstractSimpleDistributionTest {

    private static final GuiceClassPathBinder CLASS_PATH_BINDER = new GuiceClassPathBinder("org.opendaylight");

    public @Rule GuiceRule guice = new GuiceRule(new OpenFlowPluginModule(CLASS_PATH_BINDER),
            new ServiceUtilsModule(), new InMemoryControllerModule(), new DiagStatusModule(), new WebTestModule(),
            new GuiceReadyModule(), new AnnotationsModule());

    @Inject PacketProcessingService packetProcessingService; // OPNFLWPLUG-1059
    @Inject OpenFlowPluginProvider ofpProvider;
    @Inject OpenflowProviderConfig ofpConfig;
    @Inject ForwardingRulesManagerConfig frmConfig;

    @Inject OpenFlowPluginProviderImpl openFlowPluginProviderImpl;
    @Inject DiagStatusService diagStatus;

    @Test public void testConfig() throws InterruptedException {
        assertThat(ofpConfig.getGlobalNotificationQuota().longValue()).isEqualTo(64000L);
        assertThat(frmConfig.getReconciliationRetryCount().intValue()).isEqualTo(5);
    }

    @Test
    public void testDiagStatus() throws InterruptedException, ExecutionException, TimeoutException {
        openFlowPluginProviderImpl.getFullyStarted().get(60, TimeUnit.SECONDS);
        ServiceStatusSummary status = diagStatus.getServiceStatusSummary();
        if (!status.isOperational()) {
            throw new AssertionFailedError(
                    "diagStatus reports non operational, details: " + status);
        }
    }

}
