/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 13, 2015
 * @author vlads
 */
package com.propertyvista.biz.system;

import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.PmcDnsConfigTO;
import com.propertyvista.domain.security.common.VistaApplication;

public class CustomDNSManager {

    // Only resident and site are supported. TODO prospect
    public PmcDnsConfigTO getApplicationDnsConfig(Pmc pmc, VistaApplication application) {
        //TODO
        return null;
    }

    public void updateApplicationDnsConfig(Pmc pmc, VistaApplication application, PmcDnsConfigTO dnsConfig) {

    }
}
