/*
 * Copyright (c) 2018 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.netconf.micro.test;

import org.junit.Rule;
import org.opendaylight.infrautils.micro.testutils.AbstractSimpleDistributionTest;
import org.opendaylight.netconf.micro.NetconfModule;
import org.opendaylight.odlguice.inject.guice.GuiceClassPathBinder;
import org.opendaylight.odlguice.inject.guice.testutils.GuiceRule;

public class NetconfModuleTest extends AbstractSimpleDistributionTest {

    private static final GuiceClassPathBinder CLASS_PATH_BINDER = new GuiceClassPathBinder("org.opendaylight");

    public @Rule GuiceRule guice = new GuiceRule(new NetconfModule(CLASS_PATH_BINDER));

}
