/*
 * Copyright (c) 2018 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ovsdb.micro.test;

import javax.inject.Inject;
import org.junit.Rule;
import org.opendaylight.aaa.micro.CertModule;
import org.opendaylight.controller.micro.InMemoryControllerModule;
import org.opendaylight.infrautils.micro.DiagStatusModule;
import org.opendaylight.infrautils.micro.inject.guice.ready.GuiceReadyModule;
import org.opendaylight.infrautils.micro.testutils.AbstractSimpleDistributionTest;
import org.opendaylight.infrautils.web.WebModule;
import org.opendaylight.odlguice.inject.guice.GuiceClassPathBinder;
import org.opendaylight.odlguice.inject.guice.testutils.AnnotationsModule;
import org.opendaylight.odlguice.inject.guice.testutils.GuiceRule;
import org.opendaylight.ovsdb.hwvtepsouthbound.HwvtepSouthboundProvider;
import org.opendaylight.ovsdb.lib.OvsdbConnection;
import org.opendaylight.ovsdb.lib.impl.OvsdbConnectionService;
import org.opendaylight.ovsdb.micro.OvsdbModule;
import org.opendaylight.ovsdb.southbound.SouthboundProvider;
import org.opendaylight.serviceutils.micro.UpgradeModule;

public class OvsdbModuleTest extends AbstractSimpleDistributionTest {

    private static final GuiceClassPathBinder CLASS_PATH_BINDER = new GuiceClassPathBinder("org.opendaylight");

    public @Rule GuiceRule guice = new GuiceRule(new OvsdbModule(CLASS_PATH_BINDER), new CertModule(),
            new InMemoryControllerModule(), new DiagStatusModule(), new WebModule(), new GuiceReadyModule(),
            new AnnotationsModule(), new UpgradeModule());

    @Inject OvsdbConnection ovsdbConnection;
    @Inject SouthboundProvider southboundProvider;
    @Inject OvsdbConnectionService ovsdbConnectionService;
    @Inject HwvtepSouthboundProvider hwvtepSouthboundProvider;

}
