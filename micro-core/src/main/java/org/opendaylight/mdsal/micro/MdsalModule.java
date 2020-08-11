/*
 * Copyright (c) 2018 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.mdsal.micro;

import com.google.inject.AbstractModule;
import org.opendaylight.mdsal.eos.dom.api.DOMEntityOwnershipService;
import org.opendaylight.mdsal.eos.dom.simple.SimpleDOMEntityOwnershipService;

@SuppressWarnings("deprecation") // sure, but that's the point of this class...
public class MdsalModule extends AbstractModule {

    // see org.opendaylight.controller.sal.restconf.impl.test.incubate.InMemoryMdsalModule ...
    // from https://git.opendaylight.org/gerrit/#/c/79388/

    // TODO [LOW] like InMemoryControllerModule extends AbstractCloseableModule and close up..
    // or rather, better, instead just annotate respective classes with @PreDestroy!

    @Override
    protected void configure() {
        // TODO This is WRONG; later need to use the DistributedEntityOwnershipService instead here!
        bind(DOMEntityOwnershipService.class).to(SimpleDOMEntityOwnershipService.class);
    }
}
