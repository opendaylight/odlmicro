/*
 * Copyright (c) 2017 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.aaa.micro.test;

import org.junit.Ignore;
import org.junit.Rule;
import org.opendaylight.aaa.micro.AAAModule;
import org.opendaylight.controller.micro.InMemoryControllerModule;
import org.opendaylight.infrautils.micro.testutils.AbstractSimpleDistributionTest;
import org.opendaylight.odlguice.inject.guice.testutils.AnnotationsModule;
import org.opendaylight.odlguice.inject.guice.testutils.GuiceRule;

@Ignore // TODO fix NullPointerException at org.opendaylight.aaa.cert.impl.DefaultMdsalSslData.createKeyStores :155
public class AAAModuleTest extends AbstractSimpleDistributionTest {

    public @Rule GuiceRule guice = new GuiceRule(new AAAModule(), new InMemoryControllerModule(),
            // TODO new WebWiring(true), new ReferencesWiring(),
            new AnnotationsModule());

}
