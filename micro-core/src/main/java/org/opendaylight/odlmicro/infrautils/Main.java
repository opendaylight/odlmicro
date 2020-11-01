/*
 * Copyright (c) 2017 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.odlmicro.infrautils;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.opendaylight.odlguice.inject.PostFullSystemInjectionListener;
import org.opendaylight.odlguice.inject.guice.extensions.closeable.CloseableInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Simple Main.
 *
 * @author Michael Vorburger.ch
 */
public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    protected final Injector injector;

    @SuppressWarnings("checkstyle:IllegalCatch")
    public Main(Module mainModule) {
        try {
            LOG.info("Starting up {}...", mainModule);
            // TODO share why PRODUCTION Javadoc, or more, w. infrautils.inject.guice.testutils.GuiceRule
            this.injector = Guice.createInjector(Stage.PRODUCTION, mainModule);
            LOG.info("Start up of dependency injection completed; Guice injector is now ready.");
            injector.getInstance(PostFullSystemInjectionListener.class).onFullSystemInjected();
            LOG.info("Completed invoking PostFullSystemInjectionListener.onFullSystemInjected");
        } catch (Throwable t) {
            // If Guice stuff failed, there may be non-daemon threads which leave us hanging; force exit.
            LOG.error("Failed to start up, going to close up and exit", t);
            close();
            throw t;
        }
    }

    @SuppressFBWarnings("DM_EXIT")
    public final void close() {
        closeInjector();
        LOG.info("Now System.exit(0) so that any hanging non-daemon threads don't prevent JVM stop.");
        System.exit(0);
    }

    @SuppressWarnings("checkstyle:IllegalCatch")
    public final void closeInjector() {
        LOG.info("Initiating orderly shutdown by closing Guice injector...");
        try {
            if (injector != null) {
                injector.getInstance(CloseableInjector.class).close();
            }
        } catch (Throwable t) {
            LOG.warn("Trouble while closing CloseableInjector, but ignoring and continuing anyway", t);
        }
        LOG.info("Shutdown complete; Guice injector closed.");
    }

    public synchronized void awaitShutdown() {
        try {
            Signal.handle(new Signal("TERM"), new SignalHandler() {
                public void handle(Signal sig) {
                    LOG.info("\nCaught signal. Exiting");
                    close();
                }
            });

            LOG.info("Awaiting shutdown signal.");
            while (true) {
                this.wait(2000);
            }
        } catch (InterruptedException ignored) {
            LOG.info("Main Interrupted.");
        } finally {
            this.close();
        }
    }
}
