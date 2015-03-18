/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 18, 2015
 * @author vlads
 */
package com.propertyvista.server.config.appcontext;

import com.propertyvista.config.deployment.ChaineApplicationContextResolver;
import com.propertyvista.config.deployment.CustomDNSApplicationContextResolver;
import com.propertyvista.config.deployment.EnvNApplicationContextResolver;

public class EnvNResolver extends ChaineApplicationContextResolver {

    EnvNResolver(String dnsNameBase) {
        super(//
                new EnvNApplicationContextResolver(dnsNameBase), //
                new CustomDNSApplicationContextResolver());
    }
}
