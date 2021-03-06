/*
 * Copyright (c) 2017 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.aaa.micro;

import com.google.inject.AbstractModule;
import org.opendaylight.aaa.api.AuthenticationException;
import org.opendaylight.aaa.api.Claim;
import org.opendaylight.aaa.api.PasswordCredentialAuth;
import org.opendaylight.aaa.api.PasswordCredentials;
import org.opendaylight.aaa.filterchain.configuration.CustomFilterAdapterConfiguration;
import org.opendaylight.aaa.filterchain.configuration.impl.CustomFilterAdapterConfigurationImpl;
import org.opendaylight.aaa.shiro.tokenauthrealm.auth.ClaimBuilder;
import org.opendaylight.aaa.shiro.tokenauthrealm.auth.PasswordCredentialBuilder;


public class AAAModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new CertModule());
        install(new ShiroModule());
        bind(PasswordCredentialAuth.class).toInstance(new SimplePasswordCredentialAuth());
        bind(CustomFilterAdapterConfiguration.class).toInstance(new CustomFilterAdapterConfigurationImpl());
    }

    public static class SimplePasswordCredentialAuth implements PasswordCredentialAuth {
        @Override
        public Claim authenticate(PasswordCredentials cred) throws AuthenticationException {
            PasswordCredentials passwordCredentials = new PasswordCredentialBuilder()
                    .setUserName("admin")
                    .setPassword("admin")
                    .setDomain("")
                    .build();
            if (cred.equals(passwordCredentials)) {
                return new ClaimBuilder()
                        .setUser("admin")
                        .build();
            }
            return null;
        }
    }
}
