/*
 * Copyright (c) 2017 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.odlmicro.controller.test;

import java.util.concurrent.ExecutionException;
import javax.inject.Inject;
import org.junit.Rule;
import org.junit.Test;
import org.opendaylight.mdsal.binding.api.DataBroker;
import org.opendaylight.mdsal.binding.api.NotificationService;
import org.opendaylight.odlguice.inject.guice.testutils.AnnotationsModule;
import org.opendaylight.odlguice.inject.guice.testutils.GuiceRule;
import org.opendaylight.odlmicro.controller.InMemoryControllerModule;
import org.opendaylight.odlmicro.infrautils.testutils.AbstractSimpleDistributionTest;

public class InMemoryControllerModuleTest extends AbstractSimpleDistributionTest {

    public @Rule GuiceRule guice = new GuiceRule(InMemoryControllerModule.class, AnnotationsModule.class);

    @Inject DataBroker dataBroker;
    @Inject DataBroker pingPongDataBroker;
    @Inject NotificationService notificationService;

    @Test public void testDataBroker() throws InterruptedException, ExecutionException {
        dataBroker.newReadWriteTransaction().commit().get();
    }
}
