/*
 * Copyright (c) 2019 Lumina Networks, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.odlmicro.netconf;

import org.opendaylight.odlguice.inject.guice.GuiceClassPathBinder;
import org.opendaylight.odlmicro.infrautils.Main;

public final class NetconfMain {

    private NetconfMain() {
    }

    public static void main(String[] args) {
        GuiceClassPathBinder classPathBinder = new GuiceClassPathBinder("org.opendaylight");
        new Main(new NetconfModule(classPathBinder)).awaitShutdown();
    }
}
