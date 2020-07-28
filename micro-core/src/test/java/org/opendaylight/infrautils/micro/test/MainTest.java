/*
 * Copyright (c) 2017 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.infrautils.micro.test;

import org.opendaylight.infrautils.micro.Main;
import org.opendaylight.infrautils.ready.guice.ReadyModule;
import org.opendaylight.odlguice.inject.guice.testutils.AbstractGuiceJsr250Module;

/**
 * Unit test for Main.
 *
 * @author Michael Vorburger.ch
 */
public class MainTest {

    //@Test
    public void testMain() {
        new Main(new TestModule()).closeInjector();
    }

    public static class TestModule extends AbstractGuiceJsr250Module {
        @Override
        protected void configureBindings() {
            install(new ReadyModule());

            // bind(SomeInterfaceWithPostConstruct.class).to(SomeClassWithPostConstruct.class);
        }
    }

}
