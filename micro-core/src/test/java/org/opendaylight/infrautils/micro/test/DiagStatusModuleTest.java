/*
 * Copyright (c) 2017 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.infrautils.micro.test;

import javax.inject.Inject;
import org.junit.Rule;
import org.junit.Test;
import org.opendaylight.infrautils.diagstatus.DiagStatusService;
import org.opendaylight.infrautils.micro.DiagStatusModule;
import org.opendaylight.infrautils.micro.inject.guice.ready.GuiceReadyModule;
import org.opendaylight.infrautils.web.WebModule;
import org.opendaylight.odlguice.inject.guice.testutils.AnnotationsModule;
import org.opendaylight.odlguice.inject.guice.testutils.GuiceRule;

/**
 * Unit test for {@link DiagStatusModule}.
 *
 * @author Michael Vorburger.ch
 */
public class DiagStatusModuleTest {

    public @Rule GuiceRule guice = new GuiceRule(WebModule.class, DiagStatusModule.class, GuiceReadyModule.class,
            AnnotationsModule.class);

    @Inject DiagStatusService diagStatusService;

    @Test public void testDiagStatusService() {

    }

    // TODO separate DiagStatusWiring0Test VS DiagStatusWiring1Test where *1* registers a
    // Module that actually does bind a ServiceStatusProvider ...
    // not here, but in infrautils.diagstatus

}
