/*
 * Copyright (c) 2019 Lumina Networks, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowplugin.micro;

import org.opendaylight.infrautils.micro.ShellMain;
import org.opendaylight.odlguice.inject.guice.GuiceClassPathBinder;

public final class OpenflowPluginMain {

    private OpenflowPluginMain() { }

    public static void main(String[] args) {
        GuiceClassPathBinder classPathBinder = new GuiceClassPathBinder("org.opendaylight");
        new ShellMain(new OpenFlowPluginModule(classPathBinder)).awaitShutdown();
    }
}
