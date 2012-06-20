/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-03-21
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.config;

import com.pyx4j.essentials.server.EssentialsServerSideConfiguration;

public abstract class AbstractVistaServerSideConfiguration extends EssentialsServerSideConfiguration {

    public abstract boolean openDBReset();

    public abstract boolean openIdrequired();

    /**
     * This method should not be used directly since PMC may have custom DNS configuration
     * Use @see VistaDeployment.getBaseApplicationURL(VistaBasicBehavior.TenantPortal, true);
     */
    public abstract String getDefaultBaseURLresidentPortal(String pmcDnsName, boolean secure);

    /**
     * This method should not be used directly since PMC may have custom DNS configuration
     * Use @see VistaDeployment.getBaseApplicationURL(VistaBasicBehavior.CRM, true);
     */
    public abstract String getDefaultBaseURLvistaCrm(String pmcDnsName);

    /**
     * This method should not be used directly since PMC may have custom DNS configuration
     * Use @see VistaDeployment.getBaseApplicationURL(VistaBasicBehavior.ProspectiveApp, true);
     */
    public abstract String getDefaultBaseURLprospectPortal(String pmcDnsName);

    /**
     * This method should not be used directly since PMC may have custom DNS configuration
     * Use @see VistaDeployment.getBaseApplicationURL(VistaBasicBehavior.Admin, true);
     */
    public abstract String getDefaultBaseURLvistaAdmin();

    public abstract String getCaledonCompanyId();

}
