/*
 * Copyright (c) 2016 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.controller.micro;

import com.google.common.annotations.Beta;
import com.google.common.base.Throwables;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.opendaylight.mdsal.binding.api.DataBroker;
import org.opendaylight.mdsal.binding.dom.adapter.AdapterContext;
import org.opendaylight.mdsal.binding.dom.adapter.CurrentAdapterSerializer;
import org.opendaylight.mdsal.dom.api.DOMDataBroker;
import org.opendaylight.mdsal.dom.api.DOMSchemaService;
import org.opendaylight.mdsal.dom.broker.DOMNotificationRouter;
import org.opendaylight.mdsal.micro.binding.dom.adapter.AbstractConcurrentDataBrokerTest;
import org.opendaylight.yangtools.yang.model.api.EffectiveModelContextProvider;

@Beta
public class DataBrokerModule {
    private final boolean useMTDataTreeChangeListenerExecutor;

    private AbstractConcurrentDataBrokerTest dataBrokerTest;

    public DataBrokerModule(final boolean useMTDataTreeChangeListenerExecutor) {
        this.useMTDataTreeChangeListenerExecutor = useMTDataTreeChangeListenerExecutor;
    }

    public static DataBroker dataBroker() {
        return new DataBrokerModule(false).getDataBroker();
    }

    // Suppress IllegalCatch because of AbstractDataBrokerTest (change later)
    @SuppressWarnings("checkstyle:IllegalCatch")
    public DataBroker getDataBroker() {
        try {
            // This is a little bit "upside down" - in the future,
            // we should probably put what is in AbstractDataBrokerTest
            // into this DataBrokerTestModule, and make AbstractDataBrokerTest
            // use it, instead of the way around it currently is (the opposite);
            // this is just for historical reasons... and works for now.
            dataBrokerTest = new AbstractConcurrentDataBrokerTest(useMTDataTreeChangeListenerExecutor) {};
            dataBrokerTest.setup();
            return dataBrokerTest.getDataBroker();
        } catch (Exception e) {
            Throwables.throwIfUnchecked(e);
            throw new IllegalStateException(e);
        }
    }

    public DOMDataBroker getDOMDataBroker() {
        return dataBrokerTest.getDomBroker();
    }

    public AdapterContext getAdapterContext() {
        return dataBrokerTest.getDataBrokerTestCustomizer().getAdapterContext();
    }

    public CurrentAdapterSerializer getCurrentAdapterSerializer() {
        return dataBrokerTest.getDataBrokerTestCustomizer().getAdapterContext().currentSerializer();
    }

    @SuppressFBWarnings(value = "NM_CONFUSING")
    public DOMNotificationRouter getDOMNotificationRouter() {
        return dataBrokerTest.getDataBrokerTestCustomizer().getDomNotificationRouter();
    }

    public DOMSchemaService getSchemaService() {
        return dataBrokerTest.getDataBrokerTestCustomizer().getSchemaService();
    }

    public EffectiveModelContextProvider getSchemaContextProvider() {
        return dataBrokerTest.getDataBrokerTestCustomizer().getSchemaService()::getGlobalContext;
    }
}
