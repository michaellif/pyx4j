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
import com.propertyvista.config.deployment.FixedDNSApplicationContextResolver;
import com.propertyvista.domain.security.common.VistaApplication;

public class DevResolver extends ChaineApplicationContextResolver {

    public DevResolver() {
        super(//
                new FixedDNSApplicationContextResolver("localhost", VistaApplication.env), //
                new EnvNApplicationContextResolver(".local.devpv.com"), //
                new EnvNApplicationContextResolver(".m.pyx4j.com"), // <!- Host on Mobile Emulator
                new EnvNApplicationContextResolver(".h.pyx4j.com"), // <!- Host VM on WMware
                new EnvNApplicationContextResolver("-00.devpv.com"), // Testing  local deployment
                new CustomDNSApplicationContextResolver());

    }
}
