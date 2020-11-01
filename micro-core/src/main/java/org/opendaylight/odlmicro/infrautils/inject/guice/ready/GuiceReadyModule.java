/*
 * Copyright (c) 2020 Lumina Networks, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.odlmicro.infrautils.inject.guice.ready;

import com.google.inject.AbstractModule;
import org.opendaylight.infrautils.ready.SystemReadyMonitor;
import org.opendaylight.infrautils.ready.spi.SimpleSystemReadyMonitor;
import org.opendaylight.odlguice.inject.PostFullSystemInjectionListener;

public class GuiceReadyModule extends AbstractModule implements PostFullSystemInjectionListener {
    private final SimpleSystemReadyMonitor systemReadyMonitor = new SimpleSystemReadyMonitor();

    @Override
    protected void configure() {
        bind(SystemReadyMonitor.class).toInstance(systemReadyMonitor);
        bind(PostFullSystemInjectionListener.class).toInstance(this);
    }

    @Override
    public void onFullSystemInjected() {
        systemReadyMonitor.ready();
    }
}
