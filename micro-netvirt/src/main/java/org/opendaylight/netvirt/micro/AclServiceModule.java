/*
 * Copyright (c) 2016 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.netvirt.micro;

import com.google.inject.AbstractModule;
import org.opendaylight.netvirt.aclservice.AclServiceManagerImpl;
import org.opendaylight.netvirt.aclservice.api.AclServiceManager;
import org.opendaylight.netvirt.aclservice.listeners.AclEventListener;
import org.opendaylight.netvirt.aclservice.listeners.AclInterfaceListener;
import org.opendaylight.netvirt.aclservice.listeners.AclInterfaceStateListener;
import org.opendaylight.netvirt.aclservice.listeners.AclNodeListener;

// TODO This is copy/pasted from org.opendaylight.netvirt.aclservice.tests.AclServiceModule, but should not be..
public class AclServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AclServiceManager.class).to(AclServiceManagerImpl.class);
        bind(AclInterfaceStateListener.class);
        bind(AclNodeListener.class);
        bind(AclInterfaceListener.class);
        bind(AclEventListener.class);
    }
}
