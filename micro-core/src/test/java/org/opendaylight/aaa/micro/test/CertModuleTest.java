/*
 * Copyright (c) 2017 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.aaa.micro.test;

import javax.inject.Inject;
import org.junit.Rule;
import org.opendaylight.aaa.cert.api.ICertificateManager;
import org.opendaylight.aaa.micro.CertModule;
import org.opendaylight.controller.micro.InMemoryControllerModule;
import org.opendaylight.infrautils.micro.testutils.AbstractSimpleDistributionTest;
import org.opendaylight.odlguice.inject.guice.testutils.AnnotationsModule;
import org.opendaylight.odlguice.inject.guice.testutils.GuiceRule;

public class CertModuleTest extends AbstractSimpleDistributionTest {

    public @Rule GuiceRule guice = new GuiceRule(
            new CertModule(), new InMemoryControllerModule(), new AnnotationsModule());

    @Inject ICertificateManager certificateManager;

}
