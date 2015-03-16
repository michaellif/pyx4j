/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 16, 2015
 * @author vlads
 */
package com.propertyvista.server.config.filter.namespace.config;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.propertyvista.domain.security.common.VistaApplication;

public class NamesConfigNumberedEnv implements NamesConfig {

    final Set<String> baseUrlsHostNameAppPart = new HashSet<String>();

    public NamesConfigNumberedEnv(String envBase) {
        baseUrlsHostNameAppPart.add(envBase);
    }

    @Override
    public Collection<String> baseUrlsHostPmc() {
        return Collections.emptyList();
    }

    @Override
    public Collection<String> baseUrlsHostNameAppPart() {
        return baseUrlsHostNameAppPart;
    }

    @Override
    public VistaApplication getAppSugestionByDomainPart(String domain) {
        // TODO Auto-generated method stub
        return null;
    }

}
