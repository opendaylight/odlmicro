/*
 * Copyright © 2018 Red Hat, Inc. and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.odlmicro.infrautils;

import com.google.inject.AbstractModule;
import org.opendaylight.odlmicro.infrautils.inject.guice.ready.GuiceReadyModule;
import org.opendaylight.odlmicro.infrautils.web.WebModule;

public class InfraUtilsModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new WebModule());
        install(new GuiceReadyModule());
        install(new DiagStatusModule());
        install(new MetricsModule());
        install(new CachesModule());
        install(new JobCoordinatorModule());
    }
}
