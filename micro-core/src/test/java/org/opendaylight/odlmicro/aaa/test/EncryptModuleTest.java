/*
 * Copyright (c) 2017 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.odlmicro.aaa.test;

import javax.inject.Inject;
import org.junit.Rule;
import org.opendaylight.aaa.encrypt.AAAEncryptionService;
import org.opendaylight.odlguice.inject.guice.testutils.AnnotationsModule;
import org.opendaylight.odlguice.inject.guice.testutils.GuiceRule;
import org.opendaylight.odlmicro.aaa.EncryptModule;
import org.opendaylight.odlmicro.controller.InMemoryControllerModule;
import org.opendaylight.odlmicro.infrautils.testutils.AbstractSimpleDistributionTest;

public class EncryptModuleTest extends AbstractSimpleDistributionTest {

    public @Rule GuiceRule guice = new GuiceRule(
            new EncryptModule(), new InMemoryControllerModule(), new AnnotationsModule());

    @Inject AAAEncryptionService encryptionService;

}
