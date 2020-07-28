/*
 * Copyright (c) 2018 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ovsdb.micro;

import org.opendaylight.odlguice.inject.guice.AutoWiringModule;
import org.opendaylight.odlguice.inject.guice.GuiceClassPathBinder;

public class OvsdbModule extends AutoWiringModule {

    public OvsdbModule(GuiceClassPathBinder classPathBinder) {
        super(classPathBinder, "org.opendaylight.ovsdb");
    }

}
