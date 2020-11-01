/*
 * Copyright (c) 2019 Lumina Networks, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.odlmicro.netconf.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.inject.Qualifier;
import org.opendaylight.netconf.mapping.api.NetconfOperationServiceFactory;

/**
 * PingPong {@link NetconfOperationServiceFactory} dependency injection annotation.
 *
 * <p>USAGE: <pre> {@literal @}Singleton
 * public class Thing {
 *     {@literal @}Inject
 *     public Thing({@literal @}AggregatedNetconfOperationServiceFactory NetconfOperationServiceFactory listener) {
 *         ...
 *     }
 * }</pre>
 */
@Qualifier
@Documented
@Retention(RUNTIME)
@Target({ PARAMETER, METHOD, FIELD })
public @interface AggregatedNetconfOperationServiceFactory {

}
